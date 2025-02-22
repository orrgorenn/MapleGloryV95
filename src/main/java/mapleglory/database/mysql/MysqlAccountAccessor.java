package mapleglory.database.mysql;

import com.google.gson.GsonBuilder;
import io.netty.handler.timeout.TimeoutException;
import mapleglory.database.AccountAccessor;
import mapleglory.database.DatabaseConnection;
import mapleglory.database.table.AccountTable;
import mapleglory.server.ServerConfig;
import mapleglory.server.cashshop.CashItemInfo;
import mapleglory.util.InstantTypeAdapter;
import mapleglory.util.Util;
import mapleglory.world.item.Item;
import mapleglory.world.item.Trunk;
import mapleglory.world.user.Account;
import mapleglory.world.user.Locker;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MysqlAccountAccessor implements AccountAccessor {
    private static final Logger log = LoggerFactory.getLogger(MysqlAccountAccessor.class);
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
            .create();
    private Account loadAccount(ResultSet rs) {
        try {
            final int accountId = rs.getInt(AccountTable.ACCOUNT_ID);
            final String username = rs.getString(AccountTable.USERNAME);
            final String secondaryPassword = rs.getString(AccountTable.SECONDARY_PASSWORD);

            final Account account = new Account(accountId, username);
            account.setHasSecondaryPassword(secondaryPassword != null && !secondaryPassword.isEmpty());
            account.setSlotCount(rs.getInt(AccountTable.CHARACTER_SLOTS));
            account.setNxCredit(rs.getInt(AccountTable.NX_CREDIT));
            account.setNxPrepaid(rs.getInt(AccountTable.NX_PREPAID));
            account.setMaplePoint(rs.getInt(AccountTable.MAPLE_POINT));
            account.setGM(rs.getInt(AccountTable.GM));

            final Trunk trunk = new Trunk(rs.getInt(AccountTable.TRUNK_SIZE));
            final String rsItems = rs.getString(AccountTable.TRUNK_ITEMS);
            final List<Item> items = gson.fromJson(rsItems, new TypeToken<List<Item>>() {}.getType());
            if (items != null) {
                for (Item item : items) {
                    trunk.getItems().add(item);
                }
            }
            trunk.setMoney(rs.getInt(AccountTable.TRUNK_MONEY));
            account.setTrunk(trunk);

            final Locker locker = new Locker();
            final String rsCashItems = rs.getString(AccountTable.LOCKER_ITEMS);
            final List<CashItemInfo> cashItems = gson.fromJson(rsCashItems, new TypeToken<List<CashItemInfo>>() {}.getType());
            if (cashItems != null) {
                for (CashItemInfo cii : cashItems) {
                    locker.addCashItem(cii);
                }
            }
            account.setLocker(locker);

            final String rsWishlist = rs.getString(AccountTable.WISHLIST);
            final List<Integer> wishlist = gson.fromJson(rsWishlist, new TypeToken<List<Integer>>() {}.getType());
            account.setWishlist(Collections.unmodifiableList(wishlist != null ? wishlist : Collections.nCopies(10, 0)));

            return account;
        } catch (SQLException e) {
            return null;
        }
    }
    @Override
    public Optional<Account> getAccountById(int accountId) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM " + AccountTable.getTableName() + " WHERE id = ?")) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final Account acc = loadAccount(rs);
                    if (acc == null) {
                        return Optional.empty();
                    }
                    return Optional.of(acc);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    @Override
    public Optional<Account> getAccountByUsername(String username) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM " + AccountTable.getTableName() + " WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account acc = loadAccount(rs);
                    if (acc == null) {
                        return Optional.empty();
                    }
                    return Optional.of(acc);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean checkPassword(Account account, String password, boolean secondary) {
        final String columnName = secondary ? AccountTable.SECONDARY_PASSWORD : AccountTable.PASSWORD;

        long startTime = System.nanoTime();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT " + columnName + " FROM " + AccountTable.getTableName() + " WHERE " + AccountTable.ACCOUNT_ID + " = ?")) {
            ps.setInt(1, account.getId());
            ps.setQueryTimeout(5);

            // Debugging logs
            long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            log.debug("Query executed in {} ms for Account ID {}", elapsedTime, account.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString(columnName);
                    if (hashedPassword != null && checkHashedPassword(password, hashedPassword)) {
                        return true;
                    }
                }
            }
        } catch (TimeoutException e) {
            log.error("Query timed out for Account ID {}: {}", account.getId(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error checking password for Account ID {}: {}", account.getId(), e.getMessage());
        }

        return false;
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean checkHashedPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    @Override
    public boolean savePassword(Account account, String oldPassword, String newPassword, boolean secondary) {
        final String columnName = secondary ? AccountTable.SECONDARY_PASSWORD : AccountTable.PASSWORD;

        long startTime = System.nanoTime();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT " + columnName + " FROM " + AccountTable.getTableName() + " WHERE " + AccountTable.ACCOUNT_ID + " = ?")) {
            ps.setInt(1, account.getId());
            ps.setQueryTimeout(5);

            // Debugging logs
            long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            log.debug("Query executed in {} ms for Account ID {}", elapsedTime, account.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final String hashedOldPassword = rs.getString(columnName);
                    if (hashedOldPassword == null || checkHashedPassword(oldPassword, hashedOldPassword)) {
                        String updateQuery = "UPDATE " + AccountTable.getTableName() + " SET " + columnName + " = ? WHERE " + AccountTable.ACCOUNT_ID + " = ?";

                        try (PreparedStatement updatePs = con.prepareStatement(updateQuery)) {
                            updatePs.setString(1, hashPassword(newPassword));
                            updatePs.setInt(2, account.getId());
                            int rowsUpdated = updatePs.executeUpdate();

                            return rowsUpdated > 0;
                        }
                    }
                }
            }
        } catch (TimeoutException e) {
            log.error("Query timed out for Account ID {}: {}", account.getId(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error checking password for Account ID {}: {}", account.getId(), e.getMessage());
        }

        return false;
    }

    @Override
    public boolean newAccount(String username, String password) {
        if (getAccountByUsername(username).isPresent()) {
            return false;
        }

        // SQL Insert Query
        String insertQuery = "INSERT INTO " + AccountTable.getTableName() + " (" +
                AccountTable.USERNAME + ", " +
                AccountTable.PASSWORD + ", " +
                AccountTable.CHARACTER_SLOTS + ", " +
                AccountTable.NX_CREDIT + ", " +
                AccountTable.NX_PREPAID + ", " +
                AccountTable.MAPLE_POINT + ", " +
                AccountTable.TRUNK_ITEMS + ", " +
                AccountTable.TRUNK_SIZE + ", " +
                AccountTable.TRUNK_MONEY + ", " +
                AccountTable.LOCKER_ITEMS + ", " +
                AccountTable.WISHLIST +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertQuery)) {

            // Set query parameters
            ps.setString(1, username.toLowerCase());
            ps.setString(2, hashPassword(password));
            ps.setInt(3, ServerConfig.CHARACTER_BASE_SLOTS);
            ps.setInt(4, 0);
            ps.setInt(5, 0);
            ps.setInt(6, 0);
            ps.setString(7, "[]");
            ps.setInt(8, ServerConfig.TRUNK_BASE_SLOTS);
            ps.setInt(9, 0); // TRUNK_MONEY
            ps.setString(10, "[]");
            ps.setString(11, "[]");

            int rowsInserted = ps.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            log.error("SQL error inserting new account for username {}: {}", username, e.getMessage());
            return false;
        }
    }

    @Override
    public void setLoggedStatus(Account account, boolean status) {
        String updateQuery = "UPDATE " + AccountTable.getTableName() + " SET " +
                AccountTable.LOGGED_IN + " = ? " +
                "WHERE " + AccountTable.ACCOUNT_ID + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(updateQuery)) {

            // Setting parameters in PreparedStatement
            ps.setBoolean(1, status);
            ps.setInt(2, account.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("SQL error updating logged in status account ID {}: {}", account.getId(), e.getMessage());
        }
    }

    @Override
    public boolean saveAccount(Account account) {
        String updateQuery = "UPDATE " + AccountTable.getTableName() + " SET " +
                AccountTable.CHARACTER_SLOTS + " = ?, " +
                AccountTable.NX_CREDIT + " = ?, " +
                AccountTable.NX_PREPAID + " = ?, " +
                AccountTable.MAPLE_POINT + " = ?, " +
                AccountTable.TRUNK_ITEMS + " = ?, " +
                AccountTable.TRUNK_SIZE + " = ?, " +
                AccountTable.TRUNK_MONEY + " = ?, " +
                AccountTable.LOCKER_ITEMS + " = ?, " +
                AccountTable.WISHLIST + " = ? " +
                "WHERE " + AccountTable.ACCOUNT_ID + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(updateQuery)) {

            // Setting parameters in PreparedStatement
            ps.setInt(1, account.getSlotCount());
            ps.setInt(2, account.getNxCredit());
            ps.setInt(3, account.getNxPrepaid());
            ps.setInt(4, account.getMaplePoint());

            // Convert Lists to JSON String
            ps.setString(5, Util.convertListToJson(account.getTrunk().getItems()));
            ps.setInt(6, account.getTrunk().getSize());
            ps.setInt(7, account.getTrunk().getMoney());

            ps.setString(8, Util.convertListToJson(account.getLocker().getCashItems()));
            ps.setString(9, Util.convertListToJson(account.getWishlist()));

            ps.setInt(10, account.getId());

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            log.error("SQL error updating account ID {}: {}", account.getId(), e.getMessage());
            return false;
        }
    }
}
