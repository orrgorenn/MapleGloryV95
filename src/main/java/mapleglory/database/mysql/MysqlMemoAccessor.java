package mapleglory.database.mysql;

import mapleglory.database.DatabaseConnection;
import mapleglory.database.MemoAccessor;
import mapleglory.database.table.GiftTable;
import mapleglory.database.table.MemoTable;
import mapleglory.server.cashshop.Gift;
import mapleglory.server.memo.Memo;
import mapleglory.server.memo.MemoType;
import mapleglory.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MysqlMemoAccessor implements MemoAccessor {
    private Memo loadMemo(ResultSet rs) {
        try {
            final MemoType type = mapleglory.server.memo.MemoType.getByValue(rs.getInt(MemoTable.MEMO_TYPE));
            return new Memo(
                    type != null ? type : mapleglory.server.memo.MemoType.DEFAULT,
                    rs.getInt(MemoTable.MEMO_ID),
                    rs.getString(MemoTable.SENDER_NAME),
                    rs.getString(MemoTable.MEMO_CONTENT),
                    rs.getTimestamp(MemoTable.DATE_SENT).toInstant()
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public List<Memo> getMemosByCharacterId(int characterId) {
        final List<Memo> memos = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM " + MemoTable.getTableName() + " WHERE " + MemoTable.RECEIVER_ID + " = ?")) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    memos.add(loadMemo(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memos;
    }

    @Override
    public boolean hasMemo(int characterId) {
        String selectSQL = "SELECT " + MemoTable.RECEIVER_ID + " FROM " + MemoTable.getTableName() +
                " WHERE " + MemoTable.RECEIVER_ID + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(selectSQL)) {

            ps.setInt(1, characterId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public boolean newMemo(Memo memo, int receiverId) {
        String insertSQL = "INSERT INTO " + MemoTable.getTableName() + " (" +
                MemoTable.MEMO_ID + ", " +
                MemoTable.RECEIVER_ID + ", " +
                MemoTable.MEMO_TYPE + ", " +
                MemoTable.MEMO_CONTENT + ", " +
                MemoTable.SENDER_NAME + ", " +
                MemoTable.DATE_SENT + ") " +
                "VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE MEMO_ID = MEMO_ID";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertSQL)) {
            ps.setInt(1, memo.getMemoId());
            ps.setInt(2, receiverId);
            ps.setInt(3, memo.getType().getValue());
            ps.setString(4, memo.getContent());
            ps.setString(5, memo.getSender());
            ps.setTimestamp(6, Util.toTimestamp(memo.getDateSent()));

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteMemo(int memoId, int receiverId) {
        String deleteSQL = "DELETE FROM " + MemoTable.getTableName() + " WHERE " + MemoTable.MEMO_ID + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(deleteSQL)) {
            ps.setInt(1, memoId);

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
