package mapleglory.database.table;

import mapleglory.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public final class FriendTable {
    private static final Logger log = LoggerFactory.getLogger(FriendTable.class);
    public static final String CHARACTER_ID = "character_id";
    public static final String FRIEND_ID = "friend_id";
    public static final String FRIEND_NAME = "friend_name";
    public static final String FRIEND_GROUP = "friend_group";
    public static final String FRIEND_STATUS = "friend_status";

    private static final String tableName = "friend";

    public static String getTableName() {
        return tableName;
    }

    public static void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                CHARACTER_ID + " INT AUTO_INCREMENT, " +
                FRIEND_ID + " INT, " +
                FRIEND_NAME + " TEXT, " +
                FRIEND_GROUP + " TEXT, " +
                FRIEND_STATUS + " INT, " +
                "PRIMARY KEY (" + CHARACTER_ID + ", " + FRIEND_ID + ")" +
                ")";

        String createIndexSQL = "CREATE INDEX idx_friend_id ON " + getTableName() + " (" + FRIEND_ID + ")";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {

            // Create the table if it does not exist
            stmt.execute(createTableSQL);

            // Check if the index exists before creating it
            String checkIndexSQL = "SELECT COUNT(1) FROM information_schema.statistics " +
                    "WHERE table_schema = DATABASE() AND table_name = ? AND index_name = 'idx_friend_id'";

            try (PreparedStatement checkPs = con.prepareStatement(checkIndexSQL)) {
                checkPs.setString(1, getTableName());
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        stmt.execute(createIndexSQL);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error creating table or index in MySQL: {}", e.getMessage());
        }
    }
}
