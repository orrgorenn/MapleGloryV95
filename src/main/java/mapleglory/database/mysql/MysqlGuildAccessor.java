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
        if (!checkGuildNameAvailable(guild.getGuildName())) {
            return false;
        }
        return saveGuild(guild);
    }

    @Override
    public boolean saveGuild(Guild guild) {
        String insertOrUpdateSQL = "INSERT INTO " + GuildTable.getTableName() + " (" +
                GuildTable.GUILD_ID + ", " +
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
                GuildTable.BOARD_ENTRY_COUNTER + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                GuildTable.GUILD_NAME + " = VALUES(" + GuildTable.GUILD_NAME + "), " +
                GuildTable.GUILD_NAME_INDEX + " = VALUES(" + GuildTable.GUILD_NAME_INDEX + "), " +
                GuildTable.GRADE_NAMES + " = VALUES(" + GuildTable.GRADE_NAMES + "), " +
                GuildTable.MEMBERS + " = VALUES(" + GuildTable.MEMBERS + "), " +
                GuildTable.MEMBER_MAX + " = VALUES(" + GuildTable.MEMBER_MAX + "), " +
                GuildTable.MARK_BG + " = VALUES(" + GuildTable.MARK_BG + "), " +
                GuildTable.MARK_BG_COLOR + " = VALUES(" + GuildTable.MARK_BG_COLOR + "), " +
                GuildTable.MARK + " = VALUES(" + GuildTable.MARK + "), " +
                GuildTable.MARK_COLOR + " = VALUES(" + GuildTable.MARK_COLOR + "), " +
                GuildTable.NOTICE + " = VALUES(" + GuildTable.NOTICE + "), " +
                GuildTable.POINTS + " = VALUES(" + GuildTable.POINTS + "), " +
                GuildTable.LEVEL + " = VALUES(" + GuildTable.LEVEL + "), " +
                GuildTable.BOARD_ENTRY_LIST + " = VALUES(" + GuildTable.BOARD_ENTRY_LIST + "), " +
                GuildTable.BOARD_ENTRY_NOTICE + " = VALUES(" + GuildTable.BOARD_ENTRY_NOTICE + "), " +
                GuildTable.BOARD_ENTRY_COUNTER + " = VALUES(" + GuildTable.BOARD_ENTRY_COUNTER + ")";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertOrUpdateSQL)) {

            // Set the parameters from the newGuild object
            ps.setInt(1, guild.getGuildId());
            ps.setString(2, guild.getGuildName());
            ps.setString(3, guild.getGuildName().toLowerCase());
            ps.setString(4, Util.convertListToJson(guild.getGradeNames()));
            ps.setString(5, Util.convertListToJson(guild.getGuildMembers()));
            ps.setInt(6, guild.getMemberMax());
            ps.setShort(7, guild.getMarkBg());
            ps.setByte(8, guild.getMarkBgColor());
            ps.setShort(9, guild.getMark());
            ps.setByte(10, guild.getMarkColor());
            ps.setString(11, guild.getNotice());
            ps.setInt(12, guild.getPoints());
            ps.setByte(13, guild.getLevel());
            ps.setString(14, Util.convertListToJson(guild.getBoardEntries()));
            ps.setString(15, Util.convertObjectToJson(guild.getBoardNoticeEntry()));
            ps.setInt(16, guild.getBoardEntryCounter().get());

            // Execute the statement
            int rowsAffected = ps.executeUpdate();

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
