package mapleglory.database.table;

import mapleglory.database.DatabaseConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public final class GiftTable {
    private static final Logger log = LoggerFactory.getLogger(GiftTable.class);
    public static final String GIFT_SN = "gift_sn";
    public static final String RECEIVER_ID = "receiver_id";
    public static final String ITEM_ID = "item_id";
    public static final String COMMODITY_ID = "commodity_id";
    public static final String SENDER_ID = "sender_id";
    public static final String SENDER_NAME = "sender_name";
    public static final String SENDER_MESSAGE = "sender_message";
    public static final String PAIR_ITEM_SN = "pair_item_sn";


    private static final String tableName = "gift";


    public static String getTableName() {
        return tableName;
    }

    public static void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                GIFT_SN + " BIGINT, " +
                RECEIVER_ID + " INT, " +
                ITEM_ID + " INT, " +
                COMMODITY_ID + " INT, " +
                SENDER_ID + " INT, " +
                SENDER_NAME + " TEXT, " +
                SENDER_MESSAGE + " TEXT, " +
                PAIR_ITEM_SN + " BIGINT, " +
                "PRIMARY KEY (" + GIFT_SN + ")" +
                ")";

        String createIndexSQL = "CREATE INDEX idx_receiver_id ON " + getTableName() + " (" + RECEIVER_ID + ")";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {

            // Create the table if it does not exist
            stmt.execute(createTableSQL);

            // Check if the index exists before creating it
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
