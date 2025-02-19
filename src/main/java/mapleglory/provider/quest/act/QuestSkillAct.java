package mapleglory.provider.quest.act;

import mapleglory.packet.world.WvsContext;
import mapleglory.provider.SkillProvider;
import mapleglory.provider.quest.QuestSkillData;
import mapleglory.provider.skill.SkillInfo;
import mapleglory.provider.wz.property.WzListProperty;
import mapleglory.util.Locked;
import mapleglory.world.skill.SkillRecord;
import mapleglory.world.user.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class QuestSkillAct implements QuestAct {
    private final List<QuestSkillData> skills;

    public QuestSkillAct(List<QuestSkillData> skills) {
        this.skills = skills;
    }

    @Override
    public boolean canAct(Locked<User> locked, int rewardIndex) {
        return true;
    }

    @Override
    public boolean doAct(Locked<User> locked, int rewardIndex) {
        final User user = locked.get();
        for (QuestSkillData qsd : skills) {
            if (!qsd.getJobs().contains(user.getJob())) {
                continue;
            }
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(qsd.getSkillId());
            if (skillInfoResult.isEmpty()) {
                return false;
            }
            final SkillRecord skillRecord = skillInfoResult.get().createRecord();
            skillRecord.setSkillLevel(qsd.getSkillLevel());
            skillRecord.setMasterLevel(qsd.getMasterLevel());
            user.getSkillManager().addSkill(skillRecord);
            user.updatePassiveSkillData();
            user.validateStat();
            user.write(WvsContext.changeSkillRecordResult(skillRecord, true));
        }
        return true;
    }

    public static QuestSkillAct from(WzListProperty skillList) {
        final List<QuestSkillData> skills = QuestSkillData.resolveSkillData(skillList);
        return new QuestSkillAct(
                Collections.unmodifiableList(skills)
        );
    }
}
