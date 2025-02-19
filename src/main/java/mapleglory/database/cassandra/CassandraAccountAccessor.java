package mapleglory.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import io.netty.handler.timeout.TimeoutException;
import mapleglory.database.AccountAccessor;
import mapleglory.database.DatabaseManager;
import mapleglory.database.cassandra.table.AccountTable;
import mapleglory.server.ServerConfig;
import mapleglory.server.cashshop.CashItemInfo;
import mapleglory.world.item.Item;
import mapleglory.world.item.Trunk;
import mapleglory.world.user.Account;
import mapleglory.world.user.Locker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import static mapleglory.database.DatabaseManager.DATABASE_KEYSPACE;

public final class CassandraAccountAccessor extends CassandraAccessor implements AccountAccessor {
    private static final Logger log = LogManager.getLogger(CassandraAccountAccessor.class);
    public CassandraAccountAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private Account loadAccount(Row row) {
        final int accountId = row.getInt(AccountTable.ACCOUNT_ID);
        final String username = row.getString(AccountTable.USERNAME);
        final String secondaryPassword = row.getString(AccountTable.SECONDARY_PASSWORD);

        final Account account = new Account(accountId, username);
        account.setHasSecondaryPassword(secondaryPassword != null && !secondaryPassword.isEmpty());
        account.setSlotCount(row.getInt(AccountTable.CHARACTER_SLOTS));
        account.setNxCredit(row.getInt(AccountTable.NX_CREDIT));
        account.setNxPrepaid(row.getInt(AccountTable.NX_PREPAID));
        account.setMaplePoint(row.getInt(AccountTable.MAPLE_POINT));

        final Trunk trunk = new Trunk(row.getInt(AccountTable.TRUNK_SIZE));
        final List<Item> items = row.getList(AccountTable.TRUNK_ITEMS, Item.class);
        if (items != null) {
            for (Item item : items) {
                trunk.getItems().add(item);
            }
        }
        trunk.setMoney(row.getInt(AccountTable.TRUNK_MONEY));
        account.setTrunk(trunk);

        final Locker locker = new Locker();
        final List<CashItemInfo> cashItems = row.getList(AccountTable.LOCKER_ITEMS, CashItemInfo.class);
        if (cashItems != null) {
            for (CashItemInfo cii : cashItems) {
                locker.addCashItem(cii);
            }
        }
        account.setLocker(locker);

        final List<Integer> wishlist = row.getList(AccountTable.WISHLIST, Integer.class);
        account.setWishlist(Collections.unmodifiableList(wishlist != null ? wishlist : Collections.nCopies(10, 0)));

        return account;
    }

    private String lowerUsername(String username) {
        return username.toLowerCase();
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean checkHashedPassword(String password, String hashedPassword) {

        return BCrypt.checkpw(password, hashedPassword);
    }

    @Override
    public Optional<Account> getAccountById(int accountId) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), AccountTable.getTableName()).all()
                        .whereColumn(AccountTable.ACCOUNT_ID).isEqualTo(literal(accountId))
                        .build()
        );
        for (Row row : selectResult) {
            return Optional.of(loadAccount(row));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Account> getAccountByUsername(String username) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), AccountTable.getTableName()).all()
                        .whereColumn(AccountTable.USERNAME).isEqualTo(literal(lowerUsername(username)))
                        .build()
        );
        for (Row row : selectResult) {
            return Optional.of(loadAccount(row));
        }
        return Optional.empty();
    }

    @Override
    public boolean checkPassword(Account account, String password, boolean secondary) {
        final String columnName = secondary ? AccountTable.SECONDARY_PASSWORD : AccountTable.PASSWORD;

        try {
            // Start measuring execution time
            long startTime = System.nanoTime();

            // Optimized query: Fetches only the required column
            SimpleStatement statement = SimpleStatement.builder(
                            "SELECT " + columnName + " FROM " + DATABASE_KEYSPACE + "." + AccountTable.getTableName() +
                                    " WHERE " + AccountTable.ACCOUNT_ID + " = ?")
                    .addPositionalValues(account.getId())
                    .setExecutionProfileName(DatabaseManager.PROFILE_ONE)
                    .setTimeout(java.time.Duration.ofSeconds(5)) // ✅ Avoids hanging
                    .build();

            // Execute the query
            ResultSet resultSet = getSession().execute(statement);

            // Debugging logs
            long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            log.debug("Query executed in {} ms for Account ID {}", elapsedTime, account.getId());

            // Process the result
            for (Row row : resultSet) {
                String hashedPassword = row.getString(columnName);
                if (hashedPassword != null && checkHashedPassword(password, hashedPassword)) {
                    return true;
                }
            }

        } catch (TimeoutException e) {
            log.error("Query timed out for Account ID {}: {}", account.getId(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error checking password for Account ID {}: {}", account.getId(), e.getMessage());
        }

        return false; // Default return if no match is found
    }

    @Override
    public boolean savePassword(Account account, String oldPassword, String newPassword, boolean secondary) {
        final String columnName = secondary ? AccountTable.SECONDARY_PASSWORD : AccountTable.PASSWORD;
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), AccountTable.getTableName()).all()
                        .column(columnName)
                        .whereColumn(AccountTable.ACCOUNT_ID).isEqualTo(literal(account.getId()))
                        .build()
        );
        for (Row row : selectResult) {
            final String hashedOldPassword = row.getString(columnName);
            if (hashedOldPassword == null || checkHashedPassword(oldPassword, hashedOldPassword)) {
                final ResultSet updateResult = getSession().execute(
                        update(getKeyspace(), AccountTable.getTableName())
                                .setColumn(columnName, literal(hashPassword(newPassword)))
                                .whereColumn(AccountTable.ACCOUNT_ID).isEqualTo(literal(account.getId()))
                                .build()
                );
                return updateResult.wasApplied();
            }
        }
        return false;
    }

    @Override
    public synchronized boolean newAccount(String username, String password) {
        final Optional<Integer> accountId = DatabaseManager.idAccessor().nextAccountId();
        if (accountId.isEmpty()) {
            return false;
        }
        if (getAccountByUsername(username).isPresent()) {
            return false;
        }
        final ResultSet insertResult = getSession().execute(
                insertInto(getKeyspace(), AccountTable.getTableName())
                        .value(AccountTable.ACCOUNT_ID, literal(accountId.get()))
                        .value(AccountTable.USERNAME, literal(lowerUsername(username)))
                        .value(AccountTable.PASSWORD, literal(hashPassword(password)))
                        .value(AccountTable.CHARACTER_SLOTS, literal(ServerConfig.CHARACTER_BASE_SLOTS))
                        .value(AccountTable.NX_CREDIT, literal(0))
                        .value(AccountTable.NX_PREPAID, literal(0))
                        .value(AccountTable.MAPLE_POINT, literal(0))
                        .value(AccountTable.TRUNK_ITEMS, literal(List.of()))
                        .value(AccountTable.TRUNK_SIZE, literal(ServerConfig.TRUNK_BASE_SLOTS))
                        .value(AccountTable.TRUNK_MONEY, literal(0))
                        .value(AccountTable.LOCKER_ITEMS, literal(List.of()))
                        .value(AccountTable.WISHLIST, literal(List.of()))
                        .ifNotExists()
                        .build()
        );
        return insertResult.wasApplied();
    }

    @Override
    public boolean saveAccount(Account account) {
        final CodecRegistry registry = getSession().getContext().getCodecRegistry();
        final ResultSet updateResult = getSession().execute(
                update(getKeyspace(), AccountTable.getTableName())
                        .setColumn(AccountTable.CHARACTER_SLOTS, literal(account.getSlotCount()))
                        .setColumn(AccountTable.NX_CREDIT, literal(account.getNxCredit()))
                        .setColumn(AccountTable.NX_PREPAID, literal(account.getNxPrepaid()))
                        .setColumn(AccountTable.MAPLE_POINT, literal(account.getMaplePoint()))
                        .setColumn(AccountTable.TRUNK_ITEMS, literal(account.getTrunk().getItems(), registry))
                        .setColumn(AccountTable.TRUNK_SIZE, literal(account.getTrunk().getSize()))
                        .setColumn(AccountTable.TRUNK_MONEY, literal(account.getTrunk().getMoney()))
                        .setColumn(AccountTable.LOCKER_ITEMS, literal(account.getLocker().getCashItems(), registry))
                        .setColumn(AccountTable.WISHLIST, literal(account.getWishlist()))
                        .whereColumn(AccountTable.ACCOUNT_ID).isEqualTo(literal(account.getId()))
                        .build()
        );
        return updateResult.wasApplied();
    }
}
