package mapleglory.provider.quest.check;

import mapleglory.provider.quest.QuestMobData;
import mapleglory.provider.wz.property.WzListProperty;
import mapleglory.util.Locked;
import mapleglory.world.quest.QuestManager;
import mapleglory.world.quest.QuestRecord;
import mapleglory.world.user.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class QuestMobCheck implements QuestCheck {
    private final int questId;
    private final List<QuestMobData> mobs; // sorted by order

    public QuestMobCheck(int questId, List<QuestMobData> mobs) {
        this.questId = questId;
        this.mobs = mobs;
    }

    public List<QuestMobData> getMobs() {
        return mobs;
    }

    @Override
    public boolean check(Locked<User> locked) {
        final QuestManager qm = locked.get().getQuestManager();
        final Optional<QuestRecord> questRecordResult = qm.getQuestRecord(questId);
        if (questRecordResult.isEmpty()) {
            return false;
        }
        final String qrValue = questRecordResult.get().getValue();
        if (qrValue == null || qrValue.isEmpty()) {
            return false;
        }
        final String requiredValue = mobs.stream()
                .map((mobData) -> String.format("%03d", mobData.getCount()))
                .collect(Collectors.joining());
        return qrValue.equals(requiredValue);
    }

    public static QuestMobCheck from(int questId, WzListProperty mobList) {
        final List<QuestMobData> mobs = QuestMobData.resolveMobData(mobList);
        return new QuestMobCheck(
                questId,
                Collections.unmodifiableList(mobs)
        );
    }
}
