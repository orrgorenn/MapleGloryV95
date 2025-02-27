package mapleglory.script.common;

import mapleglory.provider.reward.Reward;
import mapleglory.server.event.EventState;
import mapleglory.server.event.EventType;
import mapleglory.server.packet.OutPacket;
import mapleglory.util.Tuple;
import mapleglory.world.field.Field;
import mapleglory.world.field.mob.MobAppearType;
import mapleglory.world.item.BodyPart;
import mapleglory.world.item.InventoryType;
import mapleglory.world.job.Job;
import mapleglory.world.quest.QuestRecordType;
import mapleglory.world.user.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface ScriptManager {
    // USER METHODS ----------------------------------------------------------------------------------------------------

    User getUser();

    void dispose();

    void write(OutPacket outPacket);

    void message(String message);

    void scriptProgressMessage(String message);

    void playPortalSE();

    void balloonMsg(String text, int width, int duration);

    void setDirectionMode(boolean set, int delay);

    void avatarOriented(String effectPath);

    void squibEffect(String effectPath);

    void reservedEffect(String effectPath);

    void screenEffect(String effectPath);

    void soundEffect(String effectPath);


    // STAT METHODS ----------------------------------------------------------------------------------------------------

    int getGender();

    int getLevel();

    Job getJob();

    void addExp(int exp);

    void setJob(Job job);

    void setAvatar(int look);

    void addSkill(int skillId, int skillLevel, int masterLevel);

    void removeSkill(int skillId);

    void addSp(int jobLevel, int skillPoint);

    void setConsumeItemEffect(int itemId);

    void resetConsumeItemEffect(int itemId);


    // INVENTORY METHODS -----------------------------------------------------------------------------------------------

    boolean addMoney(int money);

    boolean canAddMoney(int money);

    default boolean addItem(int itemId, int quantity) {
        return addItems(List.of(Tuple.of(itemId, quantity)), 0); // Default: No Expiry
    }

    default boolean addItem(int itemId, int quantity, int hours) {
        return addItems(List.of(Tuple.of(itemId, quantity)), hours); // Overload with Expiry
    }

    boolean addItems(List<Tuple<Integer, Integer>> items);

    boolean addItems(List<Tuple<Integer, Integer>> items, int hours);

    default boolean canAddItem(int itemId, int quantity) {
        return canAddItems(List.of(Tuple.of(itemId, quantity)));
    }

    boolean canAddItems(List<Tuple<Integer, Integer>> items);

    default boolean removeItem(int itemId) {
        final int itemCount = getItemCount(itemId); // TODO : rename method for clarity
        if (itemCount > 0) {
            return removeItem(itemId, itemCount);
        }
        return true;
    }

    boolean removeItem(int itemId, int quantity);

    boolean removeEquipped(BodyPart bodyPart);

    default boolean hasItem(int itemId) {
        return hasItem(itemId, 1);
    }

    boolean hasItem(int itemId, int quantity);

    int getItemCount(int itemId);

    void addInventorySlots(InventoryType inventoryType, int addSlots);


    // QUEST METHODS ---------------------------------------------------------------------------------------------------

    boolean hasQuestStarted(int questId);

    boolean hasQuestCompleted(int questId);

    void forceStartQuest(int questId);

    void forceCompleteQuest(int questId);

    String getQRValue(QuestRecordType questRecordType);

    boolean hasQRValue(QuestRecordType questRecordType, String value);

    void setQRValue(QuestRecordType questRecordType, String value);

    void addQRValue(QuestRecordType questRecordType, String value);


    // WARP METHODS ----------------------------------------------------------------------------------------------------

    void warp(int mapId);

    void warp(int mapId, String portalName);

    void partyWarp(int mapId, String portalName);

    default void warpInstance(int mapId, String portalName, int returnMap, int timeLimit) {
        warpInstance(List.of(mapId), portalName, returnMap, timeLimit, Map.of());
    }

    default void warpInstance(List<Integer> mapIds, String portalName, int returnMap, int timeLimit) {
        warpInstance(mapIds, portalName, returnMap, timeLimit, Map.of());
    }

    void warpInstance(List<Integer> mapIds, String portalName, int returnMap, int timeLimit, Map<String, String> variables);

    default void partyWarpInstance(int mapId, String portalName, int returnMap, int timeLimit) {
        partyWarpInstance(List.of(mapId), portalName, returnMap, timeLimit, Map.of());
    }

    default void partyWarpInstance(List<Integer> mapIds, String portalName, int returnMap, int timeLimit) {
        partyWarpInstance(mapIds, portalName, returnMap, timeLimit, Map.of());
    }

    void partyWarpInstance(List<Integer> mapIds, String portalName, int returnMap, int timeLimit, Map<String, String> variables);


    // FIELD METHODS ---------------------------------------------------------------------------------------------------

    Field getField();

    int getFieldId();

    default void spawnMob(int templateId, MobAppearType appearType, int x, int y, boolean isLeft, boolean originalField) {
        spawnMob(templateId, appearType.getValue(), x, y, isLeft, originalField);
    }

    default void spawnMobInMap(int templateId, MobAppearType appearType, int x, int y, boolean isLeft, Field customField) {
        spawnMob(templateId, appearType.getValue(), x, y, isLeft, customField);
    }

    void spawnMob(int templateId, int summonType, int x, int y, boolean isLeft, boolean originalField);

    void spawnMob(int templateId, int summonType, int x, int y, boolean isLeft, Field customField);

    void spawnNpc(int templateId, int x, int y, boolean isFlip, boolean originalField);

    void removeNpc(int templateId);

    void spawnReactor(int templateId, int x, int y, boolean isFlip, int reactorTime, boolean originalField);

    void dropRewards(List<Reward> rewards);

    void setNpcAction(int templateId, String action);

    void setReactorState(int templateId, int state);


    // EVENT METHODS ---------------------------------------------------------------------------------------------------

    void sleep(long delay, TimeUnit timeUnit);

    boolean checkParty(int memberCount, int levelMin);

    EventState getEventState(EventType eventType);

    String getAreaCheck();

    int getInstanceUserCount();

    String getInstanceVariable(String key);

    void setInstanceVariable(String key, String value);

    void addExpAll(int exp);

    void broadcastPacket(OutPacket outPacket);

    void broadcastMessage(String message, boolean originalField);

    void broadcastScriptProgressMessage(String message);

    void broadcastScreenEffect(String effectPath);

    void broadcastSoundEffect(String effectPath);


    // CONVERSATION METHODS --------------------------------------------------------------------------------------------

    String getScriptName();

    int getSpeakerId();

    void setSpeakerId(int speakerId);

    void setNotCancellable(boolean notCancellable);

    void setPlayerAsSpeaker(boolean playerAsSpeaker);

    void setSpeakerOnRight(boolean speakerOnRight);

    void setFlipSpeaker(boolean flipSpeaker);

    void toggleParam(ScriptMessageParam messageParam, boolean enabled);

    void sayOk(String text, ScriptMessageParam... overrides);

    void sayPrev(String text, ScriptMessageParam... overrides);

    void sayNext(String text, ScriptMessageParam... overrides);

    void sayBoth(String text, ScriptMessageParam... overrides);

    void sayImage(List<String> images, ScriptMessageParam... overrides);

    boolean askYesNo(String text, ScriptMessageParam... overrides);

    boolean askAccept(String text, ScriptMessageParam... overrides);

    int askMenu(String text, Map<Integer, String> options, ScriptMessageParam... overrides);

    int askSlideMenu(int type, Map<Integer, String> options, ScriptMessageParam... overrides);

    int askAvatar(String text, List<Integer> options, ScriptMessageParam... overrides);

    int askNumber(String text, int numberDefault, int numberMin, int numberMax, ScriptMessageParam... overrides);

    String askText(String text, String textDefault, int textLengthMin, int textLengthMax, ScriptMessageParam... overrides);

    String askBoxText(String text, String textDefault, int textBoxColumns, int textBoxLines, ScriptMessageParam... overrides);
}