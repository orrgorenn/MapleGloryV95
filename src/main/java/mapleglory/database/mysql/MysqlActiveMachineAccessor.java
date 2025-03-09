package mapleglory.database.mysql;

import mapleglory.database.ActiveMachineAccessor;
import mapleglory.database.DatabaseConnection;
import mapleglory.database.table.ActiveMachineTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MysqlActiveMachineAccessor implements ActiveMachineAccessor {
    private static final Logger log = LoggerFactory.getLogger(MysqlActiveMachineAccessor.class);
    @Override
    public int checkActiveInstances(String machineId) {
        String updateQuery = "SELECT COUNT(*) as active_machines FROM " + ActiveMachineTable.getTableName() + " WHERE " + ActiveMachineTable.MACHINE_ID + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(updateQuery)) {

            // Setting parameters in PreparedStatement
            ps.setString(1, machineId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("active_machines");
                }
            }
        } catch (SQLException e) {
            log.error("SQL error checkActiveInstances: {} ({})", machineId, e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean addNewInstance(int accountId, String machineId, String ipAddress) {
        // SQL Insert Query
        String instanceId = UUID.randomUUID().toString();

        String insertQuery = "INSERT INTO " + ActiveMachineTable.getTableName() + " (" +
                ActiveMachineTable.INSTANCE_ID + ", " +
                ActiveMachineTable.MACHINE_ID + ", " +
                ActiveMachineTable.IP_ADDRESS + ", " +
                ActiveMachineTable.ACCOUNT_ID +
                ") VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertQuery)) {

            // Set query parameters
            ps.setString(1, instanceId);
            ps.setString(2, machineId);
            ps.setString(3, ipAddress);
            ps.setInt(4, accountId);

            int rowsInserted = ps.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            log.error("SQL error addNewInstance for account: {} ({})", accountId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeInstance(int accountId) {
        String insertQuery = "DELETE FROM " + ActiveMachineTable.getTableName() + " WHERE " + ActiveMachineTable.ACCOUNT_ID + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertQuery)) {

            // Set query parameters
            ps.setInt(1, accountId);

            int rowsInserted = ps.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            log.error("SQL error removeInstance for account: {} ({})", accountId, e.getMessage());
            return false;
        }
    }

    @Override
    public void clearInstances() {
        String insertQuery = "DELETE FROM " + ActiveMachineTable.getTableName();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertQuery)) {

            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("SQL error clearInstances ({})", e.getMessage());
        }
    }
}
