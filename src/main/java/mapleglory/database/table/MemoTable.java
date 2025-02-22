package mapleglory.database.table;

import mapleglory.database.DatabaseConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public final class MemoTable {
    private static final Logger log = LoggerFactory.getLogger(MemoTable.class);
    public static final String MEMO_ID = "id";
    public static final String RECEIVER_ID = "receiver_id";
    public static final String MEMO_TYPE = "memo_type";
    public static final String MEMO_CONTENT = "memo_content";
    public static final String SENDER_NAME = "sender_name";
    public static final String DATE_SENT = "date_sent";

    private static final String tableName = "memo";

    public static String getTableName() {
        return tableName;
    }

    public static void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                MEMO_ID + " INT AUTO_INCREMENT, " +
                RECEIVER_ID + " INT, " +
                MEMO_TYPE + " INT, " +
                MEMO_CONTENT + " TEXT, " +
                SENDER_NAME + " TEXT, " +
                DATE_SENT + " TIMESTAMP, " +
                "PRIMARY KEY (" + MEMO_ID + ")" +
                ")";

        String createIndexSQL = "CREATE INDEX idx_receiver_id ON " + getTableName() + " (" + RECEIVER_ID + ")";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute(createTableSQL);

            String checkIndexSQL = "SELECT COUNT(1) FROM information_schema.statistics " +
                    "WHERE table_schema = DATABASE() AND table_name = ? AND index_name = 'idx_receiver_id'";

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
