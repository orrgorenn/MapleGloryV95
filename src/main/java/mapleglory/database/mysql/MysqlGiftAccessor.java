package mapleglory.database.mysql;

import mapleglory.database.DatabaseConnection;
import mapleglory.database.GiftAccessor;
import mapleglory.database.table.GiftTable;
import mapleglory.server.cashshop.Gift;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MysqlGiftAccessor implements GiftAccessor {
    private Gift loadGift(ResultSet rs) {
        try {
            return new Gift(
                    rs.getLong(GiftTable.GIFT_SN),
                    rs.getInt(GiftTable.ITEM_ID),
                    rs.getInt(GiftTable.COMMODITY_ID),
                    rs.getInt(GiftTable.SENDER_ID),
                    rs.getString(GiftTable.SENDER_NAME),
                    rs.getString(GiftTable.SENDER_MESSAGE),
                    rs.getLong(GiftTable.PAIR_ITEM_SN));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public List<Gift> getGiftsByCharacterId(int characterId) {
        final List<Gift> gifts = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM " + GiftTable.getTableName() + " WHERE " + GiftTable.RECEIVER_ID + " = ?")) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    gifts.add(loadGift(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gifts;
    }

    @Override
    public Optional<Gift> getGiftByItemSn(long itemSn) {
        String selectSQL = "SELECT * FROM " + GiftTable.getTableName() + " WHERE " + GiftTable.GIFT_SN + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(selectSQL)) {

            ps.setLong(1, itemSn);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(loadGift(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty(); // Return an empty Optional if no result is found

    }

    @Override
    public boolean newGift(Gift gift, int receiverId) {
        String insertSQL = "INSERT INTO " + GiftTable.getTableName() + " (" +
                GiftTable.GIFT_SN + ", " +
                GiftTable.RECEIVER_ID + ", " +
                GiftTable.ITEM_ID + ", " +
                GiftTable.COMMODITY_ID + ", " +
                GiftTable.SENDER_NAME + ", " +
                GiftTable.SENDER_MESSAGE + ", " +
                GiftTable.PAIR_ITEM_SN + ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE GIFT_SN = GIFT_SN";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertSQL)) {

            ps.setLong(1, gift.getGiftSn());
            ps.setInt(2, receiverId);
            ps.setInt(3, gift.getItemId());
            ps.setInt(4, gift.getCommodityId());
            ps.setString(5, gift.getSenderName());
            ps.setString(6, gift.getSenderMessage());
            ps.setLong(7, gift.getPairItemSn());

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean deleteGift(Gift gift) {
        String deleteSQL = "DELETE FROM " + GiftTable.getTableName() + " WHERE " + GiftTable.GIFT_SN + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(deleteSQL)) {
            ps.setLong(1, gift.getGiftSn());

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }
}
