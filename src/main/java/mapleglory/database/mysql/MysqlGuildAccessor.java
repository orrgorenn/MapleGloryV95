package mapleglory.database.mysql;

import com.fasterxml.jackson.databind.ObjectMapper;
import mapleglory.database.DatabaseConnection;
import mapleglory.database.GuildAccessor;
import mapleglory.database.table.GuildTable;
import mapleglory.server.guild.Guild;
import mapleglory.server.guild.GuildRanking;
import mapleglory.server.guild.GuildBoardEntry;
import mapleglory.server.guild.GuildMember;
import mapleglory.util.Util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class MysqlGuildAccessor implements GuildAccessor {
    private Guild loadGuild(ResultSet rs) {
        try {
            final int guildId = rs.getInt(GuildTable.GUILD_ID);
            final String guildName = rs.getString(GuildTable.GUILD_NAME);
            final Guild guild = new Guild(guildId, guildName);

            // Get the list of grade names (handle NULLs appropriately)
            final String gradeNamesJSON = rs.getString(GuildTable.GRADE_NAMES);
            if (gradeNamesJSON != null) {
                List<String> gradeNames = parseJsonList(gradeNamesJSON, String.class);
                guild.setGradeNames(gradeNames);
            }

            // Get the list of members (handle NULLs appropriately)
            final String membersJSON = rs.getString(GuildTable.MEMBERS);
            if (membersJSON != null) {
                List<GuildMember> members = parseJsonList(membersJSON, GuildMember.class);
                if (members != null) {
                    for (GuildMember member : members) {
                        guild.addMember(member);
                    }
                }
            }

            // Set other fields
            guild.setMemberMax(rs.getInt(GuildTable.MEMBER_MAX));
            guild.setMarkBg(rs.getShort(GuildTable.MARK_BG));
            guild.setMarkBgColor(rs.getByte(GuildTable.MARK_BG_COLOR));
            guild.setMark(rs.getShort(GuildTable.MARK));
            guild.setMarkColor(rs.getByte(GuildTable.MARK_COLOR));
            guild.setNotice(rs.getString(GuildTable.NOTICE));
            guild.setPoints(rs.getInt(GuildTable.POINTS));
            guild.setLevel(rs.getByte(GuildTable.LEVEL));

            // Get the board entries (handle NULLs appropriately)
            final String boardEntriesJSON = rs.getString(GuildTable.BOARD_ENTRY_LIST);
            if (boardEntriesJSON != null) {
                List<GuildBoardEntry> boardEntries = parseJsonList(boardEntriesJSON, GuildBoardEntry.class);
                guild.getBoardEntries().addAll(boardEntries);
            }

            // Set board entry notice and counter
            String boardNoticeJson = rs.getString(GuildTable.BOARD_ENTRY_NOTICE);
            if (boardNoticeJson != null) {
                guild.setBoardNoticeEntry(parseJson(boardNoticeJson, GuildBoardEntry.class));
            } else {
                guild.setBoardNoticeEntry(null);
            }

            // Set the board entry counter
            int boardEntryCounterValue = rs.getInt(GuildTable.BOARD_ENTRY_COUNTER);
            if (rs.wasNull()) {
                guild.setBoardEntryCounter(new AtomicInteger(0)); // Default to 0 if value is NULL
            } else {
                guild.setBoardEntryCounter(new AtomicInteger(boardEntryCounterValue));
            }


            return guild;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private <T> List<T> parseJsonList(String json, Class<T> type) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private <T> T parseJson(String json, Class<T> type) {
        if (json == null || json.isEmpty()) {
            return null;  // Return null if JSON string is empty or null
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;  // Handle any parsing errors gracefully
        }
    }

    @Override
    public Optional<Guild> getGuildById(int guildId) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM " + GuildTable.getTableName() + " WHERE " + GuildTable.GUILD_ID + " = ?")) {
            ps.setInt(1, guildId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final Guild guild = loadGuild(rs);
                    if (guild == null) {
                        return Optional.empty();
                    }
                    return Optional.of(guild);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean checkGuildNameAvailable(String name) {
        String selectSQL = "SELECT " + GuildTable.GUILD_NAME_INDEX + " FROM " + GuildTable.getTableName() +
                " WHERE LOWER(" + GuildTable.GUILD_NAME_INDEX + ") = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(selectSQL)) {

            // Set the parameter for the lowercase name
            ps.setString(1, name.toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final String existingName = rs.getString(GuildTable.GUILD_NAME_INDEX);
                    if (existingName != null && existingName.equalsIgnoreCase(name)) {
                        return false;  // Found an existing name that matches
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;  // No matching name found

    }

    @Override
    public boolean newGuild(Guild guild) {
        String insertSQL = "INSERT INTO " + GuildTable.getTableName() + " (" +
                GuildTable.GUILD_NAME + ", " +
                GuildTable.GUILD_NAME_INDEX + ", " +
                GuildTable.GRADE_NAMES + ", " +
                GuildTable.MEMBERS + ", " +
                GuildTable.MEMBER_MAX + ", " +
                GuildTable.MARK_BG + ", " +
                GuildTable.MARK_BG_COLOR + ", " +
                GuildTable.MARK + ", " +
                GuildTable.MARK_COLOR + ", " +
                GuildTable.NOTICE + ", " +
                GuildTable.POINTS + ", " +
                GuildTable.LEVEL + ", " +
                GuildTable.BOARD_ENTRY_LIST + ", " +
                GuildTable.BOARD_ENTRY_NOTICE + ", " +
                GuildTable.BOARD_ENTRY_COUNTER + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertSQL)) {

            // Set the parameters from the newGuild object
            ps.setString(1, guild.getGuildName());
            ps.setString(2, guild.getGuildName().toLowerCase());  // Convert name to lowercase for index
            ps.setString(3, Util.convertListToJson(guild.getGradeNames()));  // Assuming JSON for grade names
            ps.setString(4, Util.convertListToJson(guild.getGuildMembers()));  // Assuming JSON for members
            ps.setInt(5, guild.getMemberMax());
            ps.setShort(6, guild.getMarkBg());
            ps.setByte(7, guild.getMarkBgColor());
            ps.setShort(8, guild.getMark());
            ps.setByte(9, guild.getMarkColor());
            ps.setString(10, guild.getNotice());
            ps.setInt(11, guild.getPoints());
            ps.setByte(12, guild.getLevel());
            ps.setString(13, Util.convertListToJson(guild.getBoardEntries()));  // Assuming JSON for board entries
            ps.setString(14, Util.convertObjectToJson(guild.getBoardNoticeEntry()));  // Assuming JSON for board notice entry
            ps.setInt(15, guild.getBoardEntryCounter().get());

            // Execute the insert statement
            int rowsAffected = ps.executeUpdate();

            // Return true if the insert was successful (rows affected > 0)
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean saveGuild(Guild guild) {
        String updateSQL = "UPDATE " + GuildTable.getTableName() + " SET " +
                GuildTable.GUILD_NAME + " = ?, " +
                GuildTable.GUILD_NAME_INDEX + " = ?, " +
                GuildTable.GRADE_NAMES + " = ?, " +
                GuildTable.MEMBERS + " = ?, " +
                GuildTable.MEMBER_MAX + " = ?, " +
                GuildTable.MARK_BG + " = ?, " +
                GuildTable.MARK_BG_COLOR + " = ?, " +
                GuildTable.MARK + " = ?, " +
                GuildTable.MARK_COLOR + " = ?, " +
                GuildTable.NOTICE + " = ?, " +
                GuildTable.POINTS + " = ?, " +
                GuildTable.LEVEL + " = ?, " +
                GuildTable.BOARD_ENTRY_LIST + " = ?, " +
                GuildTable.BOARD_ENTRY_NOTICE + " = ?, " +
                GuildTable.BOARD_ENTRY_COUNTER + " = ? " +
                "WHERE " + GuildTable.GUILD_ID + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(updateSQL)) {

            // Set the parameters
            ps.setString(1, guild.getGuildName());
            ps.setString(2, guild.getGuildName().toLowerCase());  // Convert name to lowercase for index
            ps.setString(3, Util.convertListToJson(guild.getGradeNames()));  // Assuming JSON for grade names
            ps.setString(4, Util.convertListToJson(guild.getGuildMembers()));  // Assuming JSON for members
            ps.setInt(5, guild.getMemberMax());
            ps.setShort(6, guild.getMarkBg());
            ps.setByte(7, guild.getMarkBgColor());
            ps.setShort(8, guild.getMark());
            ps.setByte(9, guild.getMarkColor());
            ps.setString(10, guild.getNotice());
            ps.setInt(11, guild.getPoints());
            ps.setByte(12, guild.getLevel());
            ps.setString(13, Util.convertListToJson(guild.getBoardEntries()));
            ps.setString(14, Util.convertObjectToJson(guild.getBoardNoticeEntry()));
            ps.setInt(15, guild.getBoardEntryCounter().get());
            ps.setInt(16, guild.getGuildId());

            // Execute the update statement
            int rowsAffected = ps.executeUpdate();

            // Return true if the update was applied (rows affected > 0)
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteGuild(int guildId) {
        String deleteSQL = "DELETE FROM " + GuildTable.getTableName() + " WHERE " + GuildTable.GUILD_ID + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(deleteSQL)) {
            ps.setInt(1, guildId);

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<GuildRanking> getGuildRankings() {
        String selectSQL = "SELECT " + GuildTable.GUILD_NAME + ", " +
                GuildTable.POINTS + ", " +
                GuildTable.MARK + ", " +
                GuildTable.MARK_COLOR + ", " +
                GuildTable.MARK_BG + ", " +
                GuildTable.MARK_BG_COLOR + " FROM " + GuildTable.getTableName();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(selectSQL);
             ResultSet rs = ps.executeQuery()) {
            List<GuildRanking> guildRankings = new ArrayList<>();

            while (rs.next()) {
                guildRankings.add(new GuildRanking(
                        rs.getString(GuildTable.GUILD_NAME),
                        rs.getInt(GuildTable.POINTS),
                        rs.getShort(GuildTable.MARK),
                        rs.getByte(GuildTable.MARK_COLOR),
                        rs.getShort(GuildTable.MARK_BG),
                        rs.getByte(GuildTable.MARK_BG_COLOR)
                ));
            }

            return guildRankings.stream()
                    .sorted(Comparator.comparing(GuildRanking::getPoints).reversed())
                    .toList();
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

    }
}
