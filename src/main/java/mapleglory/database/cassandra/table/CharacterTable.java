package mapleglory.database.cassandra.table;

import mapleglory.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class CharacterTable {
    private static final Logger log = LoggerFactory.getLogger(CharacterTable.class);

    public static final String CHARACTER_ID = "id";

    public static final String ACCOUNT_ID = "account_id";
    public static final String CHARACTER_NAME = "character_name";
    public static final String CHARACTER_NAME_INDEX = "character_name_index";
    public static final String CHARACTER_STAT = "character_stat";
    public static final String CHARACTER_EQUIPPED = "character_equipped";
    public static final String EQUIP_INVENTORY = "equip_inventory";
    public static final String CONSUME_INVENTORY = "consume_inventory";
    public static final String INSTALL_INVENTORY = "install_inventory";
    public static final String ETC_INVENTORY = "etc_inventory";
    public static final String CASH_INVENTORY = "cash_inventory";
    public static final String MONEY = "money";
    public static final String EXT_SLOT_EXPIRE = "ext_slot_expire";
    public static final String SKILL_COOLTIMES = "skill_cooltimes";
    public static final String SKILL_RECORDS = "skill_records";
    public static final String QUEST_RECORDS = "quest_records";
    public static final String CONFIG = "config";
    public static final String MINIGAME_RECORD = "minigame_record";
    public static final String MAP_TRANSFER_INFO = "map_transfer_info";
    public static final String WILD_HUNTER_INFO = "wild_hunter_info";
    public static final String ITEM_SN_COUNTER = "item_sn_counter";
    public static final String FRIEND_MAX = "friend_max";
    public static final String PARTY_ID = "party_id";
    public static final String GUILD_ID = "guild_id";
    public static final String CREATION_TIME = "creation_time";
    public static final String MAX_LEVEL_TIME = "max_level_time";

    private static final String tableName = "characters";

    public static String getTableName() {
        return tableName;
    }

    public static void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS `" + CharacterTable.getTableName() + "` (" +
                CharacterTable.CHARACTER_ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
                CharacterTable.ACCOUNT_ID + " INT, " +
                CharacterTable.CHARACTER_NAME + " VARCHAR(255), " +
                CharacterTable.CHARACTER_NAME_INDEX + " VARCHAR(255), " +
                CharacterTable.CHARACTER_STAT + " JSON, " + // Storing as JSON
                CharacterTable.CHARACTER_EQUIPPED + " JSON, " +
                CharacterTable.EQUIP_INVENTORY + " JSON, " +
                CharacterTable.CONSUME_INVENTORY + " JSON, " +
                CharacterTable.INSTALL_INVENTORY + " JSON, " +
                CharacterTable.ETC_INVENTORY + " JSON, " +
                CharacterTable.CASH_INVENTORY + " JSON, " +
                CharacterTable.MONEY + " INT, " +
                CharacterTable.EXT_SLOT_EXPIRE + " TIMESTAMP, " +
                CharacterTable.SKILL_COOLTIMES + " JSON, " + // Map stored as JSON
                CharacterTable.SKILL_RECORDS + " JSON, " + // List stored as JSON
                CharacterTable.QUEST_RECORDS + " JSON, " +
                CharacterTable.MINIGAME_RECORD + " JSON, " +
                CharacterTable.MAP_TRANSFER_INFO + " JSON, " +
                CharacterTable.WILD_HUNTER_INFO + " JSON, " +
                CharacterTable.CONFIG + " JSON, " +
                CharacterTable.ITEM_SN_COUNTER + " INT, " +
                CharacterTable.FRIEND_MAX + " INT, " +
                CharacterTable.PARTY_ID + " INT, " +
                CharacterTable.GUILD_ID + " INT, " +
                CharacterTable.CREATION_TIME + " TIMESTAMP, " +
                CharacterTable.MAX_LEVEL_TIME + " TIMESTAMP, " +
                "FOREIGN KEY (" + CharacterTable.ACCOUNT_ID + ") REFERENCES " + AccountTable.getTableName() + "(" + AccountTable.ACCOUNT_ID + ")" +
                ")";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {

            stmt.execute(createTableSQL);

            createIndexIfNotExists(stmt, "idx_account_id", CharacterTable.getTableName(), CharacterTable.ACCOUNT_ID);
            createIndexIfNotExists(stmt, "idx_character_name", CharacterTable.getTableName(), CharacterTable.CHARACTER_NAME_INDEX);

        } catch (SQLException e) {
            log.error("Error creating table or index in MySQL: {}", e.getMessage());
        }
    }

    private static void createIndexIfNotExists(Statement stmt, String indexName, String tableName, String columnName) throws SQLException {
        String checkIndexSQL = "SELECT COUNT(1) FROM information_schema.statistics " +
                "WHERE table_schema = DATABASE() AND table_name = '" + tableName + "' " +
                "AND index_name = '" + indexName + "'";

        try (ResultSet rs = stmt.executeQuery(checkIndexSQL)) {
            if (rs.next() && rs.getInt(1) == 0) { // If index does not exist
                String createIndexSQL = "CREATE INDEX " + indexName + " ON " + tableName + " (" + columnName + ")";
                stmt.execute(createIndexSQL);
            }
        }
    }
}
