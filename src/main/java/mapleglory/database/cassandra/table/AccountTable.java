package mapleglory.database.cassandra.table;

import mapleglory.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class AccountTable {
    private static final Logger log = LoggerFactory.getLogger(AccountTable.class);
    public static final String ACCOUNT_ID = "id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String SECONDARY_PASSWORD = "secondary_password";
    public static final String EMAIL = "email";
    public static final String CHARACTER_SLOTS = "character_slots";
    public static final String NX_CREDIT = "nx_credit";
    public static final String NX_PREPAID = "nx_prepaid";
    public static final String MAPLE_POINT = "maple_point";
    public static final String TRUNK_ITEMS = "trunk_items";
    public static final String TRUNK_SIZE = "trunk_size";
    public static final String TRUNK_MONEY = "trunk_money";
    public static final String LOCKER_ITEMS = "locker_items";
    public static final String WISHLIST = "wishlist";
    public static final String LOGGED_IN = "logged_in";
    private static final String tableName = "account";

    public static String getTableName() {
        return tableName;
    }

    public static void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + AccountTable.getTableName() + " (" +
                AccountTable.ACCOUNT_ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
                AccountTable.USERNAME + " VARCHAR(255) UNIQUE, " +
                AccountTable.EMAIL + " VARCHAR(255) UNIQUE, " +
                AccountTable.PASSWORD + " TEXT, " +
                AccountTable.SECONDARY_PASSWORD + " TEXT, " +
                AccountTable.CHARACTER_SLOTS + " INT, " +
                AccountTable.NX_CREDIT + " INT, " +
                AccountTable.NX_PREPAID + " INT, " +
                AccountTable.MAPLE_POINT + " INT, " +
                AccountTable.TRUNK_ITEMS + " JSON, " +
                AccountTable.TRUNK_SIZE + " INT, " +
                AccountTable.TRUNK_MONEY + " INT, " +
                AccountTable.LOCKER_ITEMS + " JSON, " +
                AccountTable.WISHLIST + " JSON, " +
                AccountTable.LOGGED_IN + " BOOL" +
                ")";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {

            stmt.execute(createTableSQL); // Execute CREATE TABLE

            String checkIndexSQL = "SELECT COUNT(1) FROM information_schema.statistics " +
                    "WHERE table_schema = DATABASE() AND table_name = '" + AccountTable.getTableName() + "' " +
                    "AND index_name = 'idx_username'";
            try (ResultSet rs = stmt.executeQuery(checkIndexSQL)) {
                if (rs.next() && rs.getInt(1) == 0) { // If index does not exist
                    String createIndexSQL = "CREATE INDEX idx_username ON " + AccountTable.getTableName() + " (" + AccountTable.USERNAME + ")";
                    stmt.execute(createIndexSQL);
                }
            }
        } catch (SQLException e) {
            log.error("Error creating account table or index in MySQL: {}", e.getMessage());
        }
    }
}
