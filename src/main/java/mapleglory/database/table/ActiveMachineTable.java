package mapleglory.database.table;

import mapleglory.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class ActiveMachineTable {
    private static final Logger log = LoggerFactory.getLogger(ActiveMachineTable.class);

    public static final String INSTANCE_ID = "instance_id";
    public static final String ACCOUNT_ID = "account_id";
    public static final String MACHINE_ID = "machine_id";
    public static final String IP_ADDRESS = "ip_address";
    public static final String LAST_SEEN = "last_seen";

    private static final String tableName = "active_machine";

    public static String getTableName() {
        return tableName;
    }

    public static void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + ActiveMachineTable.getTableName() + " (" +
                ActiveMachineTable.INSTANCE_ID + " VARCHAR(36) PRIMARY KEY, " +
                ActiveMachineTable.MACHINE_ID + " VARCHAR(255) NOT NULL, " +
                ActiveMachineTable.IP_ADDRESS + " VARCHAR(50), " +
                ActiveMachineTable.LAST_SEEN + " TIMESTAMP DEFAULT NOW() " +
                ")";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {

            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            log.error("Error creating account table or index in MySQL: {}", e.getMessage());
        }
    }
}
