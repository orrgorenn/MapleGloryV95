package mapleglory.database.mysql;

import mapleglory.database.DatabaseConnection;
import mapleglory.database.FriendAccessor;
import mapleglory.database.table.FriendTable;
import mapleglory.database.table.GiftTable;
import mapleglory.world.user.friend.Friend;
import mapleglory.world.user.friend.FriendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MysqlFriendAccessor implements FriendAccessor {
    private static final Logger log = LoggerFactory.getLogger(MysqlAccountAccessor.class);

    private Friend loadFriend(ResultSet rs) {
        try {
            final int characterId = rs.getInt(FriendTable.CHARACTER_ID);
            final int friendId = rs.getInt(FriendTable.FRIEND_ID);
            final String friendName = rs.getString(FriendTable.FRIEND_NAME);
            final String friendGroup = rs.getString(FriendTable.FRIEND_GROUP);
            final FriendStatus status = FriendStatus.getByValue(rs.getInt(FriendTable.FRIEND_STATUS));
            return new Friend(characterId, friendId, friendName, friendGroup, status);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Friend> getFriendsByCharacterId(int characterId) {
        final List<Friend> friends = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM " + FriendTable.getTableName() + " WHERE " + FriendTable.CHARACTER_ID + " = ?")) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    friends.add(loadFriend(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    @Override
    public List<Friend> getFriendsByFriendId(int friendId) {
        final List<Friend> friends = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM " + FriendTable.getTableName() + " WHERE " + FriendTable.FRIEND_ID + " = ?")) {
            ps.setInt(1, friendId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    friends.add(loadFriend(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    @Override
    public boolean saveFriend(Friend friend, boolean force) {
        String insertQuery;

        if (force) {
            // Regular INSERT that overwrites if exists
            insertQuery = "REPLACE INTO " + FriendTable.getTableName() + " (" +
                    FriendTable.CHARACTER_ID + ", " +
                    FriendTable.FRIEND_ID + ", " +
                    FriendTable.FRIEND_NAME + ", " +
                    FriendTable.FRIEND_GROUP + ", " +
                    FriendTable.FRIEND_STATUS + ") " +
                    "VALUES (?, ?, ?, ?, ?)";
        } else {
            // INSERT IF NOT EXISTS using ON DUPLICATE KEY IGNORE (similar to IF NOT EXISTS in Cassandra)
            insertQuery = "INSERT IGNORE INTO  " + FriendTable.getTableName() + " (" +
                    FriendTable.CHARACTER_ID + ", " +
                    FriendTable.FRIEND_ID + ", " +
                    FriendTable.FRIEND_NAME + ", " +
                    FriendTable.FRIEND_GROUP + ", " +
                    FriendTable.FRIEND_STATUS + ") " +
                    "VALUES (?, ?, ?, ?, ?)";
        }

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(insertQuery)) {
            stmt.setInt(1, friend.getCharacterId());
            stmt.setInt(2, friend.getFriendId());
            stmt.setString(3, friend.getFriendName());
            stmt.setString(4, friend.getFriendGroup());
            stmt.setInt(5, friend.getStatus().getValue());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;  // Returns true if the insert was successful

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteFriend(int characterId, int friendId) {
        String deleteSQL = "DELETE FROM " + FriendTable.getTableName() + " WHERE " +
                FriendTable.CHARACTER_ID + " = ? AND " +
                FriendTable.FRIEND_ID + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(deleteSQL)) {
            ps.setInt(1, characterId);
            ps.setInt(2, friendId);

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
