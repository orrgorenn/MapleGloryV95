package mapleglory.world.job.cygnus;

import mapleglory.provider.SkillProvider;
import mapleglory.provider.skill.SkillInfo;
import mapleglory.world.field.Field;
import mapleglory.world.field.mob.Mob;
import mapleglory.world.skill.Attack;
import mapleglory.world.skill.Skill;
import mapleglory.world.skill.SkillProcessor;
import mapleglory.world.user.User;
import mapleglory.world.user.stat.CharacterTemporaryStat;
import mapleglory.world.user.stat.TemporaryStatOption;

public final class WindArcher extends SkillProcessor {
    // WIND_ARCHER_1
    public static final int CRITICAL_SHOT = 13000000;
    public static final int THE_EYE_OF_AMAZON = 13000001;
    public static final int FOCUS = 13001002;
    public static final int DOUBLE_SHOT = 13001003;
    public static final int STORM = 13001004;
    // WIND_ARCHER_2
    public static final int BOW_MASTERY = 13100000;
    public static final int THRUST = 13100004;
    public static final int BOW_BOOSTER = 13101001;
    public static final int FINAL_ATTACK = 13101002;
    public static final int SOUL_ARROW = 13101003;
    public static final int STORM_BREAK = 13101005;
    public static final int WIND_WALK = 13101006;
    // WIND_ARCHER_3
    public static final int BOW_EXPERT = 13110003;
    public static final int ARROW_RAIN = 13111000;
    public static final int STRAFE = 13111001;
    public static final int HURRICANE = 13111002;
    public static final int PUPPET = 13111004;
    public static final int EAGLE_EYE = 13111005;
    public static final int WIND_PIERCING = 13111006;
    public static final int WIND_SHOT = 13111007;

    public static void handleAttack(User user, Mob mob, Attack attack, int delay) {
    }

    public static void handleSkill(User user, Skill skill) {
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final int skillId = skill.skillId;
        final int slv = skill.slv;

        final Field field = user.getField();
        switch (skillId) {
            case FINAL_ATTACK:
                user.setTemporaryStat(CharacterTemporaryStat.WindBreakerFinal, TemporaryStatOption.of(1, skillId, si.getDuration(slv)));
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}
