package mapleglory.handler.stage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mapleglory.database.DatabaseConnection;
import mapleglory.database.DatabaseManager;
import mapleglory.database.table.CharacterTable;
import mapleglory.handler.Handler;
import mapleglory.packet.stage.LoginPacket;
import mapleglory.packet.stage.LoginResultType;
import mapleglory.provider.EtcProvider;
import mapleglory.provider.ItemProvider;
import mapleglory.provider.SkillProvider;
import mapleglory.provider.item.ItemInfo;
import mapleglory.provider.skill.SkillInfo;
import mapleglory.server.ServerConfig;
import mapleglory.server.header.InHeader;
import mapleglory.server.migration.MigrationInfo;
import mapleglory.server.migration.TransferInfo;
import mapleglory.server.node.ChannelInfo;
import mapleglory.server.node.Client;
import mapleglory.server.node.LoginServerNode;
import mapleglory.server.packet.InPacket;
import mapleglory.server.packet.OutPacket;
import mapleglory.util.InstantTypeAdapter;
import mapleglory.util.Util;
import mapleglory.world.GameConstants;
import mapleglory.world.item.*;
import mapleglory.world.job.Job;
import mapleglory.world.job.RaceSelect;
import mapleglory.world.quest.QuestManager;
import mapleglory.world.skill.SkillManager;
import mapleglory.world.user.Account;
import mapleglory.world.user.AvatarData;
import mapleglory.world.user.CharacterData;
import mapleglory.world.user.data.*;
import mapleglory.world.user.stat.CharacterStat;
import mapleglory.world.user.stat.ExtendSp;
import mapleglory.world.user.stat.StatConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public final class LoginHandler {
    private static final Logger log = LogManager.getLogger(LoginHandler.class);

    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
            .create();

    @Handler(InHeader.CheckPassword)
    public static void handleCheckPassword(Client c, InPacket inPacket) {
        final String username = inPacket.decodeString();
        final String password = inPacket.decodeString();
        final byte[] machineId = inPacket.decodeArray(16);
        final int gameRoomClient = inPacket.decodeInt();
        final byte gameStartMode = inPacket.decodeByte();
        final byte worldId = inPacket.decodeByte();
        final byte channelId = inPacket.decodeByte();
        final byte[] partnerCode = inPacket.decodeArray(4);

        // Resolve account
        final Optional<Account> accountResult = DatabaseManager.accountAccessor().getAccountByUsername(username);
        if (accountResult.isEmpty()) {
            if (ServerConfig.AUTO_CREATE_ACCOUNT) {
                DatabaseManager.accountAccessor().newAccount(username, password);
            }
            c.write(LoginPacket.checkPasswordResultFail(LoginResultType.NotRegistered));
            return;
        }
        final Account account = accountResult.get();

        // Check if logged in
        final LoginServerNode loginServerNode = (LoginServerNode) c.getServerNode();
        loginServerNode.submitOnlineRequest(account, (online) -> {
            final boolean isConnected = loginServerNode.isConnected(account);
            if (online || isConnected) {
                c.write(LoginPacket.checkPasswordResultFail(LoginResultType.AlreadyConnected));
                return;
            }

            // Check password
            if (!DatabaseManager.accountAccessor().checkPassword(account, password, false)) {
                c.write(LoginPacket.checkPasswordResultFail(LoginResultType.IncorrectPassword));
                return;
            }

            c.setAccount(account);
            c.setMachineId(machineId);
            c.getServerNode().addClient(c);
            c.write(LoginPacket.checkPasswordResultSuccess(account, c.getClientKey()));
        });
    }

    @Handler({ InHeader.WorldInfoRequest, InHeader.WorldRequest })
    public static void handleWorldRequest(Client c, InPacket inPacket) {
        final LoginServerNode loginServerNode = (LoginServerNode) c.getServerNode();
        c.write(LoginPacket.worldInformation(loginServerNode.getChannels()));
        c.write(LoginPacket.worldInformationEnd());
        c.write(LoginPacket.latestConnectedWorld(ServerConfig.WORLD_ID));
    }

    @Handler(InHeader.ViewAllChar)
    public static void handleViewAllChar(Client c, InPacket inPacket) {
        c.write(LoginPacket.viewAllCharResult());
    }

    @Handler(InHeader.CheckUserLimit)
    public static void handleCheckUserLimit(Client c, InPacket inPacket) {
        final int worldId = inPacket.decodeShort();
        c.write(LoginPacket.checkUserLimitResult());
    }

    @Handler(InHeader.SelectWorld)
    public static void handleSelectWorld(Client c, InPacket inPacket) {
        final byte gameStartMode = inPacket.decodeByte();
        if (gameStartMode != 2) {
            c.write(LoginPacket.selectWorldResultFail(LoginResultType.Unknown));
            return;
        }

        final byte worldId = inPacket.decodeByte();
        final byte channelId = inPacket.decodeByte();
        inPacket.decodeInt(); // unk

        // Check World ID and Channel ID
        final LoginServerNode loginServerNode = (LoginServerNode) c.getServerNode();
        final Optional<ChannelInfo> channelInfoResult = loginServerNode.getChannelById(channelId);
        if (worldId != ServerConfig.WORLD_ID || channelInfoResult.isEmpty()) {
            c.write(LoginPacket.selectWorldResultFail(LoginResultType.Unknown));
            return;
        }

        // Check Account
        final Account account = c.getAccount();
        if (account == null) {
            c.write(LoginPacket.selectWorldResultFail(LoginResultType.Unknown));
            return;
        }
        if (!c.getServerNode().isConnected(account)) {
            c.write(LoginPacket.selectWorldResultFail(LoginResultType.Unknown));
            return;
        }

        loadCharacterList(c);
        account.setChannelId(channelId);
        c.write(LoginPacket.selectWorldResultSuccess(account));
    }

    @Handler(InHeader.CheckDuplicatedID)
    public static void handleCheckDuplicatedId(Client c, InPacket inPacket) {
        final String name = inPacket.decodeString();
        // Validation done on client side, server side validation in handleCreateNewCharacter
        if (DatabaseManager.characterAccessor().checkCharacterNameAvailable(name)) {
            c.write(LoginPacket.checkDuplicatedIdResult(name, 0)); // Success
        } else {
            c.write(LoginPacket.checkDuplicatedIdResult(name, 1)); // This name is currently being used.
        }
    }

    @Handler(InHeader.CreateNewCharacter)
    public static void handleCreateNewCharacter(Client c, InPacket inPacket) {
        final String name = inPacket.decodeString();
        final int selectedRace = inPacket.decodeInt();
        final short selectedSubJob = inPacket.decodeShort();
        final int[] selectedAL = new int[]{
                inPacket.decodeInt(), // face
                inPacket.decodeInt(), // hair
                inPacket.decodeInt(), // hair color
                inPacket.decodeInt(), // skin
                inPacket.decodeInt(), // coat
                inPacket.decodeInt(), // pants
                inPacket.decodeInt(), // shoes
                inPacket.decodeInt(), // weapon
        };
        final byte gender = inPacket.decodeByte();

        // Validate character
        if (!GameConstants.isValidCharacterName(name) || EtcProvider.isForbiddenName(name)) {
            c.write(LoginPacket.createNewCharacterResultFail(LoginResultType.InvalidCharacterName));
            return;
        }
        if (!DatabaseManager.characterAccessor().checkCharacterNameAvailable(name)) {
            c.write(LoginPacket.createNewCharacterResultFail(LoginResultType.InvalidCharacterName));
            return;
        }

        final Optional<RaceSelect> raceSelectResult = RaceSelect.getByRace(selectedRace);
        if (raceSelectResult.isEmpty()) {
            log.error("Could not resolve selected race : {}", selectedRace);
            c.close();
            return;
        }
        final RaceSelect raceSelect = raceSelectResult.get();
        final Job job = raceSelect.getJob();
        if (selectedSubJob != 0 && job != Job.BEGINNER) {
            log.error("Tried to create a character with job : {} and sub job : {}", job, selectedSubJob);
            c.close();
            return;
        }
        for (int i = 0; i < selectedAL.length; i++) {
            if (!EtcProvider.isValidStartingItem(i, selectedAL[i])) {
                log.error("Tried to create a character with an invalid starting item : {}", selectedAL[i]);
                c.close();
                return;
            }
        }
        if (gender < 0 || gender > 2) {
            log.error("Tried to create a character with an invalid gender : {}", gender);
            c.close();
            return;
        }

        // Create character
        String insertQuery = "INSERT INTO " + CharacterTable.getTableName() + " (" +
                CharacterTable.ACCOUNT_ID + ", " +
                CharacterTable.CHARACTER_NAME + ", " +
                CharacterTable.CHARACTER_NAME_INDEX + ", " +
                CharacterTable.CHARACTER_STAT + ", " +
                CharacterTable.CHARACTER_EQUIPPED + ", " +
                CharacterTable.EQUIP_INVENTORY + ", " +
                CharacterTable.CONSUME_INVENTORY + ", " +
                CharacterTable.INSTALL_INVENTORY + ", " +
                CharacterTable.ETC_INVENTORY + ", " +
                CharacterTable.CASH_INVENTORY + ", " +
                CharacterTable.MONEY + ", " +
                CharacterTable.EXT_SLOT_EXPIRE + ", " +
                CharacterTable.SKILL_COOLTIMES + ", " +
                CharacterTable.SKILL_RECORDS + ", " +
                CharacterTable.QUEST_RECORDS + ", " +
                CharacterTable.CONFIG + ", " +
                CharacterTable.MINIGAME_RECORD + ", " +
                CharacterTable.MAP_TRANSFER_INFO + ", " +
                CharacterTable.WILD_HUNTER_INFO + ", " +
                CharacterTable.ITEM_SN_COUNTER + ", " +
                CharacterTable.FRIEND_MAX + ", " +
                CharacterTable.PARTY_ID + ", " +
                CharacterTable.GUILD_ID + ", " +
                CharacterTable.CREATION_TIME + ", " +
                CharacterTable.MAX_LEVEL_TIME + ") VALUES (" +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Instant now = Instant.now();

        final int accountId = c.getAccount().getId();

        // ✅ Initialize character data
        CharacterData characterData = new CharacterData(accountId);
        characterData.setItemSnCounter(new AtomicInteger(1));
        characterData.setCreationTime(now);

        // ✅ Initialize character stats
        short level = 1;
        int hp = StatConstants.getMinHp(level, job.getJobId());
        int mp = StatConstants.getMinMp(level, job.getJobId());

        CharacterStat cs = new CharacterStat();
        cs.setName(name);
        cs.setGender(gender);
        cs.setSkin((byte) selectedAL[3]);
        cs.setFace(selectedAL[0]);
        cs.setHair(selectedAL[1] + selectedAL[2]);
        cs.setLevel(level);
        cs.setJob(job.getJobId());
        cs.setSubJob(selectedSubJob);
        cs.setBaseStr((short) 4);
        cs.setBaseDex((short) 4);
        cs.setBaseInt((short) 4);
        cs.setBaseLuk((short) 4);
        cs.setHp(hp);
        cs.setMaxHp(hp);
        cs.setMp(mp);
        cs.setMaxMp(mp);
        cs.setAp((short) 0);
        cs.setSp(ExtendSp.from(Map.of()));
        cs.setExp(0);
        cs.setPop((short) 0);
        cs.setPosMap(GameConstants.getStartingMap(job, selectedSubJob));
        cs.setPortal((byte) 0);
        characterData.setCharacterStat(cs);

        // ✅ Initialize inventory
        InventoryManager im = new InventoryManager();
        im.setEquipped(new Inventory(Short.MAX_VALUE));
        im.setEquipInventory(new Inventory(ServerConfig.INVENTORY_BASE_SLOTS));
        im.setConsumeInventory(new Inventory(ServerConfig.INVENTORY_BASE_SLOTS));
        im.setInstallInventory(new Inventory(ServerConfig.INVENTORY_BASE_SLOTS));
        im.setEtcInventory(new Inventory(ServerConfig.INVENTORY_BASE_SLOTS));
        im.setCashInventory(new Inventory(ServerConfig.INVENTORY_CASH_SLOTS));
        im.setMoney(0);
        im.setExtSlotExpire(now);
        characterData.setInventoryManager(im);

        for (int i = 4; i < selectedAL.length; i++) {
            final int itemId = selectedAL[i];
            if (itemId == 0) {
                continue;
            }
            final BodyPart bodyPart;
            if (i == 4) {
                bodyPart = BodyPart.CLOTHES;
            } else if (i == 5) {
                bodyPart = BodyPart.PANTS;
            } else if (i == 6) {
                bodyPart = BodyPart.SHOES;
            } else { // i == 7
                bodyPart = BodyPart.WEAPON;
            }
            if (!ItemConstants.isCorrectBodyPart(itemId, bodyPart, gender)) {
                log.error("Incorrect body part {} for item {}", bodyPart.name(), itemId);
                continue;
            }
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
            if (itemInfoResult.isEmpty()) {
                log.error("Failed to resolve item {}", itemId);
                continue;
            }
            final ItemInfo ii = itemInfoResult.get();
            final Item item = ii.createItem(characterData.getNextItemSn());
            im.getEquipped().putItem(bodyPart.getValue(), item);
        }

        // ✅ Initialize skills
        SkillManager sm = new SkillManager();
        for (SkillInfo skillInfo : SkillProvider.getSkillsForJob(job)) {
            if (!skillInfo.isInvisible()) {
                sm.addSkill(skillInfo.createRecord());
            }
        }
        characterData.setSkillManager(sm);

        // ✅ Initialize quest, config, and other managers
        characterData.setQuestManager(new QuestManager());
        characterData.setConfigManager(ConfigManager.defaults());
        characterData.setMiniGameRecord(new MiniGameRecord());
        characterData.setMapTransferInfo(new MapTransferInfo());
        characterData.setWildHunterInfo(new WildHunterInfo());
        characterData.setFriendMax(ServerConfig.FRIEND_MAX_BASE);

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            // ✅ Set parameters (without character_id)
            ps.setInt(1, accountId);
            ps.setString(2, name);
            ps.setString(3, name.toLowerCase());

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

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedCharacterId = rs.getInt(1);
                        characterData.getCharacterStat().setId(generatedCharacterId);

                        loadCharacterList(c);
                        c.write(LoginPacket.createNewCharacterResultSuccess(characterData));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("SQL error inserting character: {}", e.getMessage());
            c.write(LoginPacket.createNewCharacterResultFail(LoginResultType.DBFail));
        }
    }

    @Handler(InHeader.SelectCharacter)
    public static void handleSelectCharacter(Client c, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final String macAddress = inPacket.decodeString(); // CLogin::GetLocalMacAddress
        final String macAddressWithHddSerial = inPacket.decodeString(); // CLogin::GetLocalMacAddressWithHDDSerialNo

        final Account account = c.getAccount();
        if (ServerConfig.REQUIRE_SECONDARY_PASSWORD || account == null || !account.canSelectCharacter(characterId)) {
            c.write(LoginPacket.selectCharacterResultFail(LoginResultType.Unknown));
            return;
        }
        handleMigration(c, account, characterId);
    }

    @Handler(InHeader.DeleteCharacter)
    public static void handleDeleteCharacter(Client c, InPacket inPacket) {
        final String secondaryPassword = inPacket.decodeString();
        final int characterId = inPacket.decodeInt();

        final Account account = c.getAccount();
        if (account == null || !account.canSelectCharacter(characterId) ||
                !c.getServerNode().isConnected(account)) {
            c.write(LoginPacket.deleteCharacterResult(LoginResultType.Unknown, characterId));
            return;
        }
        if (!DatabaseManager.accountAccessor().checkPassword(account, secondaryPassword, true)) {
            c.write(LoginPacket.deleteCharacterResult(LoginResultType.IncorrectSPW, characterId));
            return;
        }
        if (!DatabaseManager.characterAccessor().deleteCharacter(account.getId(), characterId)) {
            c.write(LoginPacket.deleteCharacterResult(LoginResultType.DBFail, characterId));
            return;
        }

        loadCharacterList(c);
        c.write(LoginPacket.deleteCharacterResult(LoginResultType.Success, characterId));
    }

    @Handler(InHeader.EnableSPWRequest)
    public static void handleEnableSpwRequest(Client c, InPacket inPacket) {
        inPacket.decodeByte(); // 1
        final int characterId = inPacket.decodeInt(); // dwCharacterID
        final String macAddress = inPacket.decodeString(); // CLogin::GetLocalMacAddress
        final String macAddressWithHddSerial = inPacket.decodeString(); // CLogin::GetLocalMacAddressWithHDDSerialNo
        final String secondaryPassword = inPacket.decodeString(); // sSPW

        final Account account = c.getAccount();
        if (account == null || !account.canSelectCharacter(characterId) || !c.getServerNode().isConnected(account) ||
                account.hasSecondaryPassword() || !DatabaseManager.accountAccessor().savePassword(account, "", secondaryPassword, true)) {
            c.write(LoginPacket.selectCharacterResultFail(LoginResultType.Unknown));
            return;
        }
        handleMigration(c, account, characterId);
    }

    @Handler(InHeader.CheckSPWRequest)
    public static void handleCheckSpwRequest(Client c, InPacket inPacket) {
        final String secondaryPassword = inPacket.decodeString(); // sSPW
        final int characterId = inPacket.decodeInt(); // dwCharacterID
        final String macAddress = inPacket.decodeString(); // CLogin::GetLocalMacAddress
        final String macAddressWithHddSerial = inPacket.decodeString(); // CLogin::GetLocalMacAddressWithHDDSerialNo

        final Account account = c.getAccount();
        if (account == null) {
            log.debug("account = null");
        } else {
            log.debug("Acc: {}, canSelect: {}, isConnected: {}, hasSecPass: {}", account, account.canSelectCharacter(characterId), c.getServerNode().isConnected(account), account.hasSecondaryPassword());
        }
        log.debug("mac: {}, withHdd: {}", macAddress, macAddressWithHddSerial);
        if (account == null || !account.canSelectCharacter(characterId) || !c.getServerNode().isConnected(account) ||
                !account.hasSecondaryPassword()) {
            log.debug("send char result failed");
            c.write(LoginPacket.selectCharacterResultFail(LoginResultType.Unknown));
            return;
        }
        if (!DatabaseManager.accountAccessor().checkPassword(account, secondaryPassword, true)) {
            OutPacket checkSecondaryPasswordResult = LoginPacket.checkSecondaryPasswordResult();
            log.debug("send checkSecondaryPasswordResult: {}", checkSecondaryPasswordResult);
            c.write(checkSecondaryPasswordResult);
            return;
        }
        log.debug("handling migration");
        handleMigration(c, account, characterId);
    }

    private static void loadCharacterList(Client c) {
        // Resolve character list for account, sorted by highest level
        final Account account = c.getAccount();
        final List<AvatarData> characterList = DatabaseManager.characterAccessor().getAvatarDataByAccountId(c.getAccount().getId());
        account.setCharacterList(characterList.stream().sorted(Comparator.comparingInt(AvatarData::getLevel).reversed()).toList());
    }

    private static void handleMigration(Client c, Account account, int characterId) {
        log.debug("inside handleMigration")
        // Check that client requirements are set
        if (c.getMachineId() == null || c.getMachineId().length != 16 || c.getClientKey() == null || c.getClientKey().length != 8) {
            log.error("Tried to submit migration request without client requirements for character ID : {}", characterId);
            c.write(LoginPacket.selectCharacterResultFail(LoginResultType.Unknown));
            return;
        }

        // Resolve target channel
        final int targetChannelId = account.getChannelId();
        final LoginServerNode loginServerNode = (LoginServerNode) c.getServerNode();
        final Optional<ChannelInfo> channelInfoResult = loginServerNode.getChannelById(targetChannelId);
        if (channelInfoResult.isEmpty()) {
            log.error("Could not resolve target channel for migration request for character ID : {}", characterId);
            c.write(LoginPacket.selectCharacterResultFail(LoginResultType.Unknown));
            return;
        }

        // Create and submit migration request
        final MigrationInfo migrationInfo = MigrationInfo.from(targetChannelId, account.getId(), characterId, c.getMachineId(), c.getClientKey());
        loginServerNode.submitLoginRequest(migrationInfo, (transferResult) -> {
            if (transferResult.isEmpty()) {
                log.error("Failed to submit migration request for character ID : {}", characterId);
                c.write(LoginPacket.selectCharacterResultFail(LoginResultType.Unknown));
                return;
            }
            final TransferInfo transferInfo = transferResult.get();
            c.write(LoginPacket.selectCharacterResultSuccess(transferInfo.getChannelHost(), transferInfo.getChannelPort(), characterId));
        });
    }
}
