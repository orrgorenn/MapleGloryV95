package mapleglory.database.mysql;

import mapleglory.database.DatabaseConnection;
import mapleglory.database.IdAccessor;
import mapleglory.database.table.IdTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class MysqlIdAccessor implements IdAccessor {
    private Optional<Integer> getNextId(String type) {
        String selectSQL = "SELECT NEXT_ID FROM " + IdTable.getTableName() + " WHERE ID_TYPE = ? FOR UPDATE";
        String updateSQL = "UPDATE " + IdTable.getTableName() + " SET NEXT_ID = ? WHERE ID_TYPE = ? AND NEXT_ID = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement selectPS = con.prepareStatement(selectSQL);
             PreparedStatement updatePS = con.prepareStatement(updateSQL)) {

            selectPS.setString(1, type);

            try (ResultSet rs = selectPS.executeQuery()) {
                if (rs.next()) {
                    int nextId = rs.getInt("NEXT_ID");

                    // Prepare and set parameters for the update query
                    updatePS.setInt(1, nextId + 1);
                    updatePS.setString(2, type);
                    updatePS.setInt(3, nextId);

                    // Execute the update query
                    int rowsAffected = updatePS.executeUpdate();

                    if (rowsAffected > 0) {
                        return Optional.of(nextId);
                    } else {
                        // If the update was not applied, retry the process
                        return getNextId(type);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> nextAccountId() {
        return getNextId(IdTable.ACCOUNT_ID);
    }

    @Override
    public Optional<Integer> nextCharacterId() {
        return getNextId(IdTable.CHARACTER_ID);
    }

    @Override
    public Optional<Integer> nextPartyId() {
        return getNextId(IdTable.PARTY_ID);
    }

    @Override
    public Optional<Integer> nextGuildId() {
        return getNextId(IdTable.GUILD_ID);
    }

    @Override
    public Optional<Integer> nextMemoId() {
        return getNextId(IdTable.MEMO_ID);
    }
}
