package mapleglory.database.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import mapleglory.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public final class IdTable {
    public static final String ID_TYPE = "id_type";
    public static final String NEXT_ID = "next_id";

    // ID types
    public static final String ACCOUNT_ID = "account_id";
    public static final String CHARACTER_ID = "character_id";
    public static final String PARTY_ID = "party_id";
    public static final String GUILD_ID = "guild_id";
    public static final String MEMO_ID = "memo_id";

    private static final String tableName = "id_table";

    public static String getTableName() {
        return tableName;
    }

    public static void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                "ID_TYPE VARCHAR(255) PRIMARY KEY, " +
                "NEXT_ID INT)";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {

            // Execute the table creation statement
            stmt.executeUpdate(createTableSQL);

            // Insert initial values if they do not exist
            String insertSQL = "INSERT INTO " + getTableName() + " (ID_TYPE, NEXT_ID) " +
                    "SELECT ?, 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM " + getTableName() + " WHERE ID_TYPE = ?)";

            try (PreparedStatement ps = con.prepareStatement(insertSQL)) {
                for (String idType : List.of(ACCOUNT_ID, CHARACTER_ID, PARTY_ID, GUILD_ID, MEMO_ID)) {
                    ps.setString(1, idType);  // Set ID_TYPE
                    ps.setString(2, idType);  // Check if ID_TYPE already exists
                    ps.executeUpdate(); // Execute the insert
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
