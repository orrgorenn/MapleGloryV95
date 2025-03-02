package mapleglory.database.mysql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mapleglory.database.CharacterAccessor;
import mapleglory.database.CharacterInfo;
import mapleglory.database.DatabaseConnection;
import mapleglory.database.table.CharacterTable;
import mapleglory.server.rank.CharacterRank;
import mapleglory.util.InstantTypeAdapter;
import mapleglory.util.Util;
import mapleglory.world.item.Inventory;
import mapleglory.world.item.InventoryManager;
import mapleglory.world.job.JobConstants;
import mapleglory.world.quest.QuestManager;
import mapleglory.world.quest.QuestRecord;
import mapleglory.world.skill.SkillManager;
import mapleglory.world.skill.SkillRecord;
import mapleglory.world.user.AvatarData;
import mapleglory.world.user.CharacterData;
import mapleglory.world.user.PersonalInfo;
import mapleglory.world.user.data.*;
import mapleglory.world.user.stat.CharacterStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MysqlCharacterAccessor implements CharacterAccessor {
    private static final Logger log = LoggerFactory.getLogger(MysqlCharacterAccessor.class);
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
            .create();
    @Override
    public boolean checkCharacterNameAvailable(String name) {
        String selectQuery = "SELECT " + CharacterTable.CHARACTER_NAME + " FROM " + CharacterTable.getTableName() +
                " WHERE " + CharacterTable.CHARACTER_NAME_INDEX + " = ?";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(selectQuery)) {
            ps.setString(1, name.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String existingName = rs.getString(CharacterTable.CHARACTER_NAME);
                    if (existingName != null && existingName.equalsIgnoreCase(name)) {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            log.error("SQL error checking character name availability '{}': {}", name, e.getMessage());
        }

        return true;
    }

    private Map<Integer, Instant> getSkillCooltimes(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName); // Retrieve JSON from MySQL

        if (json == null || json.isEmpty()) {
            return Map.of();
        }

        // Convert JSON String to Map<Integer, Instant>
        Map<Integer, Timestamp> tempMap = gson.fromJson(json, new TypeToken<Map<Integer, Timestamp>>() {}.getType());

        // Convert Timestamp values to Instant
        return tempMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toInstant()));
    }

    private CharacterData loadCharacterData(ResultSet rs) {
        try {
            final int accountId = rs.getInt(CharacterTable.ACCOUNT_ID);

            final CharacterData cd = new CharacterData(accountId);

            final String csString = rs.getString(CharacterTable.CHARACTER_STAT);
            final CharacterStat cs = gson.fromJson(csString, new TypeToken<CharacterStat>() {}.getType());
            cs.setId(rs.getInt(CharacterTable.CHARACTER_ID));
            cs.setName(rs.getString(CharacterTable.CHARACTER_NAME));
            cd.setCharacterStat(cs);

            final InventoryManager im = new InventoryManager();
            final String rsEq = rs.getString(CharacterTable.CHARACTER_EQUIPPED);
            final String rsEqp = rs.getString(CharacterTable.EQUIP_INVENTORY);
            final String rsCon = rs.getString(CharacterTable.CONSUME_INVENTORY);
            final String rsIns = rs.getString(CharacterTable.INSTALL_INVENTORY);
            final String rsEtc = rs.getString(CharacterTable.ETC_INVENTORY);
            final String rsCash = rs.getString(CharacterTable.CASH_INVENTORY);
            im.setEquipped(gson.fromJson(rsEq, new TypeToken<Inventory>() {}.getType()));
            im.setEquipInventory(gson.fromJson(rsEqp, new TypeToken<Inventory>() {}.getType()));
            im.setConsumeInventory(gson.fromJson(rsCon, new TypeToken<Inventory>() {}.getType()));
            im.setInstallInventory(gson.fromJson(rsIns, new TypeToken<Inventory>() {}.getType()));
            im.setEtcInventory(gson.fromJson(rsEtc, new TypeToken<Inventory>() {}.getType()));
            im.setCashInventory(gson.fromJson(rsCash, new TypeToken<Inventory>() {}.getType()));
            im.setMoney(rs.getInt(CharacterTable.MONEY));
            Timestamp timestamp = rs.getTimestamp(CharacterTable.EXT_SLOT_EXPIRE);
            im.setExtSlotExpire(timestamp != null ? timestamp.toInstant() : null);
            cd.setInventoryManager(im);

            final SkillManager sm = new SkillManager();
            final Map<Integer, Instant> skillCooltimes = getSkillCooltimes(rs, CharacterTable.SKILL_COOLTIMES);
            if (skillCooltimes != null) {
                sm.getSkillCooltimes().putAll(skillCooltimes);
            }
            final String rsSkill = rs.getString(CharacterTable.SKILL_RECORDS);
            final List<SkillRecord> skillRecords = gson.fromJson(rsSkill, new TypeToken<List<SkillRecord>>() {}.getType());
            if (skillRecords != null) {
                for (SkillRecord sr : skillRecords) {
                    sm.addSkill(sr);
                }
            }
            cd.setSkillManager(sm);

            final QuestManager qm = new QuestManager();
            final String rsQr = rs.getString(CharacterTable.QUEST_RECORDS);
            final List<QuestRecord> questRecords = gson.fromJson(rsQr, new TypeToken<List<QuestRecord>>() {}.getType());
            if (questRecords != null) {
                for (QuestRecord qr : questRecords) {
                    qm.addQuestRecord(qr);
                }
            }
            cd.setQuestManager(qm);

            final String rsCm = rs.getString(CharacterTable.CONFIG);
            final ConfigManager cm = gson.fromJson(rsCm, new TypeToken<ConfigManager>() {}.getType());
            cd.setConfigManager(cm);

            final String rsPi = rs.getString(CharacterTable.PERSONAL_INFO);
            final PersonalInfo pi = gson.fromJson(rsPi, new TypeToken<PersonalInfo>() {}.getType());
            cd.setPersonalInfo(pi);

            final String rsMgr = rs.getString(CharacterTable.MINIGAME_RECORD);
            final MiniGameRecord mgr = gson.fromJson(rsMgr, new TypeToken<MiniGameRecord>() {}.getType());
            cd.setMiniGameRecord(mgr);

            final CoupleRecord cr = CoupleRecord.from(im.getEquipped(), im.getEquipInventory());
            cd.setCoupleRecord(cr);

            final String rsMti = rs.getString(CharacterTable.MAP_TRANSFER_INFO);
            final MapTransferInfo mti = gson.fromJson(rsMti, new TypeToken<MapTransferInfo>() {}.getType());
            cd.setMapTransferInfo(mti);

            final String rsWhi = rs.getString(CharacterTable.WILD_HUNTER_INFO);
            final WildHunterInfo whi = gson.fromJson(rsWhi, new TypeToken<WildHunterInfo>() {}.getType());
            cd.setWildHunterInfo(whi);

            cd.setItemSnCounter(new AtomicInteger(rs.getInt(CharacterTable.ITEM_SN_COUNTER)));
            cd.setFriendMax(rs.getInt(CharacterTable.FRIEND_MAX));
            cd.setPartyId(rs.getInt(CharacterTable.PARTY_ID));
            cd.setGuildId(rs.getInt(CharacterTable.GUILD_ID));

            Timestamp tsCT = rs.getTimestamp(CharacterTable.CREATION_TIME);
            cd.setCreationTime(tsCT != null ? tsCT.toInstant() : null);

            Timestamp tsMlt = rs.getTimestamp(CharacterTable.CREATION_TIME);
            cd.setMaxLevelTime(tsMlt != null ? tsMlt.toInstant() : null);
            return cd;
        } catch (SQLException e) {
            log.error("Error loading CharacterData: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public Optional<CharacterData> getCharacterById(int characterId) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM " + CharacterTable.getTableName() + " WHERE id = ?")) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final CharacterData chr = loadCharacterData(rs);
                    if (chr == null) {
                        return Optional.empty();
                    }
                    return Optional.of(chr);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<CharacterData> getAllCharacters(int accountId) {
        final List<CharacterData> chrs = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM " + CharacterTable.getTableName() + " WHERE " + CharacterTable.ACCOUNT_ID + " = ?")) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    chrs.add(loadCharacterData(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chrs;
    }

    @Override
    public Optional<CharacterData> getCharacterByName(String name) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM " + CharacterTable.getTableName() + " WHERE " + CharacterTable.CHARACTER_NAME + " = ?")) {
            ps.setString(1, name.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final CharacterData chr = loadCharacterData(rs);
                    if (chr == null) {
                        return Optional.empty();
                    }
                    return Optional.of(chr);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<CharacterInfo> getCharacterInfoByName(String name) {
        String query = "SELECT " +
                CharacterTable.ACCOUNT_ID + ", " +
                CharacterTable.CHARACTER_ID + ", " +
                CharacterTable.CHARACTER_NAME +
                " FROM " + CharacterTable.getTableName() +
                " WHERE " + CharacterTable.CHARACTER_NAME_INDEX + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, name.toLowerCase()); // Set the parameter value

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new CharacterInfo(
                            rs.getInt(CharacterTable.ACCOUNT_ID),
                            rs.getInt(CharacterTable.CHARACTER_ID),
                            rs.getString(CharacterTable.CHARACTER_NAME)
                    ));
                }
            }
        } catch (SQLException e) {
            log.error("SQL error fetching character info for '{}': {}", name, e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Integer> getAccountIdByCharacterId(int characterId) {
        String query = "SELECT " +
                CharacterTable.ACCOUNT_ID +
                " FROM " + CharacterTable.getTableName() +
                " WHERE " + CharacterTable.CHARACTER_ID + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, characterId); // Set the parameter value

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt(CharacterTable.ACCOUNT_ID));
                }
            }
        } catch (SQLException e) {
            log.error("SQL error fetching character info for '{}': {}", characterId, e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public List<AvatarData> getAvatarDataByAccountId(int accountId) {
        List<AvatarData> avatarDataList = new ArrayList<>();

        String query = "SELECT " +
                CharacterTable.CHARACTER_ID + ", " +
                CharacterTable.CHARACTER_NAME + ", " +
                CharacterTable.CHARACTER_STAT + ", " +
                CharacterTable.CHARACTER_EQUIPPED +
                " FROM " + CharacterTable.getTableName() +
                " WHERE " + CharacterTable.ACCOUNT_ID + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String characterStatJson = rs.getString(CharacterTable.CHARACTER_STAT);
                    CharacterStat characterStat = gson.fromJson(characterStatJson, CharacterStat.class);

                    String inventoryJson = rs.getString(CharacterTable.CHARACTER_EQUIPPED);
                    Inventory equipped = gson.fromJson(inventoryJson, Inventory.class);

                    characterStat.setId(rs.getInt(CharacterTable.CHARACTER_ID));
                    characterStat.setName(rs.getString(CharacterTable.CHARACTER_NAME));

                    avatarDataList.add(AvatarData.from(characterStat, equipped));
                }
            }
        } catch (SQLException e) {
            log.error("SQL error fetching avatar data for account ID {}: {}", accountId, e.getMessage());
        }

        return avatarDataList;
    }

    @Override
    public boolean newCharacter(CharacterData characterData) {
        String name = characterData.getCharacterName().toLowerCase();
        boolean nameAvailable = checkCharacterNameAvailable(name);
        if (!nameAvailable) {
            return false;
        }
        return saveCharacter(characterData);
    }

    @Override
    public boolean saveCharacter(CharacterData characterData) {
        String updateQuery = "UPDATE " + CharacterTable.getTableName() + " SET " +
                CharacterTable.ACCOUNT_ID + " = ?, " +
                CharacterTable.CHARACTER_NAME + " = ?, " +
                CharacterTable.CHARACTER_NAME_INDEX + " = ?, " +
                CharacterTable.CHARACTER_STAT + " = ?, " +
                CharacterTable.CHARACTER_EQUIPPED + " = ?, " +
                CharacterTable.EQUIP_INVENTORY + " = ?, " +
                CharacterTable.CONSUME_INVENTORY + " = ?, " +
                CharacterTable.INSTALL_INVENTORY + " = ?, " +
                CharacterTable.ETC_INVENTORY + " = ?, " +
                CharacterTable.CASH_INVENTORY + " = ?, " +
                CharacterTable.MONEY + " = ?, " +
                CharacterTable.EXT_SLOT_EXPIRE + " = ?, " +
                CharacterTable.SKILL_COOLTIMES + " = ?, " +
                CharacterTable.SKILL_RECORDS + " = ?, " +
                CharacterTable.QUEST_RECORDS + " = ?, " +
                CharacterTable.CONFIG + " = ?, " +
                CharacterTable.MINIGAME_RECORD + " = ?, " +
                CharacterTable.MAP_TRANSFER_INFO + " = ?, " +
                CharacterTable.WILD_HUNTER_INFO + " = ?, " +
                CharacterTable.ITEM_SN_COUNTER + " = ?, " +
                CharacterTable.FRIEND_MAX + " = ?, " +
                CharacterTable.PARTY_ID + " = ?, " +
                CharacterTable.GUILD_ID + " = ?, " +
                CharacterTable.CREATION_TIME + " = ?, " +
                CharacterTable.MAX_LEVEL_TIME + " = ?, " +
                CharacterTable.PERSONAL_INFO + " = ? " +
                "WHERE " + CharacterTable.CHARACTER_ID + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(updateQuery)) {

            // ✅ Set parameters in PreparedStatement
            ps.setInt(1, characterData.getAccountId());
            ps.setString(2, characterData.getCharacterName());
            ps.setString(3, characterData.getCharacterName());

            // Convert objects to JSON before inserting into MySQL
            ps.setString(4, gson.toJson(characterData.getCharacterStat()));
            ps.setString(5, gson.toJson(characterData.getInventoryManager().getEquipped()));
            ps.setString(6, gson.toJson(characterData.getInventoryManager().getEquipInventory()));
            ps.setString(7, gson.toJson(characterData.getInventoryManager().getConsumeInventory()));
            ps.setString(8, gson.toJson(characterData.getInventoryManager().getInstallInventory()));
            ps.setString(9, gson.toJson(characterData.getInventoryManager().getEtcInventory()));
            ps.setString(10, gson.toJson(characterData.getInventoryManager().getCashInventory()));

            ps.setInt(11, characterData.getInventoryManager().getMoney());
            ps.setTimestamp(12, Util.toTimestamp(characterData.getInventoryManager().getExtSlotExpire()));

            ps.setString(13, gson.toJson(characterData.getSkillManager().getSkillCooltimes()));
            ps.setString(14, gson.toJson(characterData.getSkillManager().getSkillRecords()));
            ps.setString(15, gson.toJson(characterData.getQuestManager().getQuestRecords()));
            ps.setString(16, gson.toJson(characterData.getConfigManager()));
            ps.setString(17, gson.toJson(characterData.getMiniGameRecord()));
            ps.setString(18, gson.toJson(characterData.getMapTransferInfo()));
            ps.setString(19, gson.toJson(characterData.getWildHunterInfo()));

            ps.setInt(20, characterData.getItemSnCounter().get());
            ps.setInt(21, characterData.getFriendMax());
            ps.setInt(22, characterData.getPartyId());
            ps.setInt(23, characterData.getGuildId());

            ps.setTimestamp(24, Util.toTimestamp(characterData.getCreationTime()));
            ps.setTimestamp(25, Util.toTimestamp(characterData.getMaxLevelTime()));

            ps.setString(26, gson.toJson(characterData.getPersonalInfo()));

            ps.setInt(27, characterData.getCharacterId()); // WHERE condition

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0; // ✅ Returns true if at least one row was updated

        } catch (SQLException e) {
            log.error("SQL error updating character data for character ID {}: {}", characterData.getCharacterId(), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteCharacter(int accountId, int characterId) {
        String deleteQuery = "DELETE FROM " + CharacterTable.getTableName() +
                " WHERE " + CharacterTable.CHARACTER_ID + " = ? AND " + CharacterTable.ACCOUNT_ID + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(deleteQuery)) {

            // ✅ Set parameters
            ps.setInt(1, characterId);
            ps.setInt(2, accountId);

            // ✅ Execute delete and check affected rows
            int rowsDeleted = ps.executeUpdate();
            boolean success = rowsDeleted > 0;

            if (success) {
                log.info("Deleted character ID " + characterId + " for account ID " + accountId);
            } else {
                log.warn("No character deleted. Character ID " + characterId + " may not exist for account ID " + accountId);
            }

            return success;
        } catch (SQLException e) {
            log.error("SQL error deleting character ID " + characterId + " for account ID " + accountId + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public Map<Integer, CharacterRank> getCharacterRanks() {
        List<CharacterRankData> rankDataList = new ArrayList<>();

        String query = "SELECT " +
                CharacterTable.CHARACTER_ID + ", " +
                CharacterTable.CHARACTER_STAT + ", " +
                CharacterTable.MAX_LEVEL_TIME +
                " FROM " + CharacterTable.getTableName();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int characterId = rs.getInt(CharacterTable.CHARACTER_ID);

                // ✅ Convert JSON to CharacterStat object
                String characterStatJson = rs.getString(CharacterTable.CHARACTER_STAT);
                CharacterStat characterStat = gson.fromJson(characterStatJson, CharacterStat.class);

                // ✅ Convert Timestamp to Instant
                Timestamp timestamp = rs.getTimestamp(CharacterTable.MAX_LEVEL_TIME);
                Instant maxLevelTime = (timestamp != null) ? timestamp.toInstant() : null;

                // ✅ Add to list
                rankDataList.add(new CharacterRankData(
                        characterId,
                        JobConstants.getJobCategory(characterStat.getJob()),
                        characterStat.getCumulativeExp(),
                        maxLevelTime
                ));
            }
        } catch (SQLException e) {
            log.error("SQL error fetching character rankings: " + e.getMessage());
        }

        // ✅ Sort rank data
        rankDataList.sort(Comparator.comparing(CharacterRankData::getCumulativeExp).reversed()
                .thenComparing(CharacterRankData::getMaxLevelTime));

        // ✅ Compute job & world rankings
        Map<Integer, Integer> jobRanks = new HashMap<>();
        Map<Integer, CharacterRank> characterRanks = new LinkedHashMap<>();

        for (CharacterRankData rankData : rankDataList) {
            int characterId = rankData.getCharacterId();
            int jobCategory = rankData.getJobCategory();
            int worldRank = characterRanks.size() + 1;
            int jobRank = jobRanks.getOrDefault(jobCategory, 0) + 1;

            jobRanks.put(jobCategory, jobRank);
            characterRanks.put(characterId, new CharacterRank(characterId, worldRank, jobRank));
        }

        return characterRanks;
    }

    private static class CharacterRankData {
        private final int characterId;
        private final int jobCategory;
        private final long cumulativeExp;
        private final Instant maxLevelTime;

        private CharacterRankData(int characterId, int jobCategory, long cumulativeExp, Instant maxLevelTime) {
            this.characterId = characterId;
            this.jobCategory = jobCategory;
            this.cumulativeExp = cumulativeExp;
            this.maxLevelTime = maxLevelTime;
        }

        public int getCharacterId() {
            return characterId;
        }

        public int getJobCategory() {
            return jobCategory;
        }

        public long getCumulativeExp() {
            return cumulativeExp;
        }

        public Instant getMaxLevelTime() {
            return maxLevelTime != null ? maxLevelTime : Instant.MAX;
        }
    }
}
