package mapleglory.database.table;

import mapleglory.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public final class GuildTable {
    private static final Logger log = LoggerFactory.getLogger(GuildTable.class);
    public static final String GUILD_ID = "id";
    public static final String GUILD_NAME = "guild_name";
    public static final String GUILD_NAME_INDEX = "guild_name_index";
    public static final String GRADE_NAMES = "grade_names";
    public static final String MEMBERS = "members";
    public static final String MEMBER_MAX = "member_max";
    public static final String MARK_BG = "mark_bg";
    public static final String MARK_BG_COLOR = "mark_bg_color";
    public static final String MARK = "mark";
    public static final String MARK_COLOR = "mark_color";
    public static final String NOTICE = "notice";
    public static final String POINTS = "points";
    public static final String LEVEL = "level";
    public static final String BOARD_ENTRY_LIST = "board_entry_list";
    public static final String BOARD_ENTRY_NOTICE = "board_entry_notice";
    public static final String BOARD_ENTRY_COUNTER = "board_entry_counter";

    private static final String tableName = "guild";

    public static String getTableName() {
        return tableName;
    }

    public static void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                GUILD_ID + " INT, " +
                GUILD_NAME + " TEXT, " +
                GUILD_NAME_INDEX + " TEXT, " +  // Keep TEXT for this column
                GRADE_NAMES + " JSON, " +
                MEMBERS + " JSON, " +
                MEMBER_MAX + " INT, " +
                MARK_BG + " SMALLINT, " +
                MARK_BG_COLOR + " TINYINT, " +
                MARK + " SMALLINT, " +
                MARK_COLOR + " TINYINT, " +
                NOTICE + " TEXT, " +
                POINTS + " INT, " +
                LEVEL + " TINYINT, " +
                BOARD_ENTRY_LIST + " JSON, " +
                BOARD_ENTRY_NOTICE + " JSON, " +
                BOARD_ENTRY_COUNTER + " INT, " +
                "PRIMARY KEY (" + GUILD_ID + ")" +
                ")";

        String createIndexSQL = "CREATE INDEX idx_guild_name_index ON " + getTableName() + " (" + GUILD_NAME_INDEX + "(255))";  // Specify the length for TEXT

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {

            // Create the table if it does not exist
            stmt.execute(createTableSQL);

            // Check if the index exists before creating it
            String checkIndexSQL = "SELECT COUNT(1) FROM information_schema.statistics " +
                    "WHERE table_schema = DATABASE() AND table_name = ? AND index_name = 'idx_guild_name_index'";

            try (PreparedStatement checkPs = con.prepareStatement(checkIndexSQL)) {
                checkPs.setString(1, getTableName());  // Safely set the table name

                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) { // If index does not exist
                        stmt.execute(createIndexSQL); // Create the index
                    }
                }
            }

        } catch (SQLException e) {
            log.error("Error creating table or index in MySQL: {}", e.getMessage());
        }
    }
}