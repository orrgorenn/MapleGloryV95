package mapleglory.script.quest;

import mapleglory.script.common.Script;
import mapleglory.script.common.ScriptHandler;
import mapleglory.script.common.ScriptManager;
import mapleglory.world.field.Field;
import mapleglory.world.field.mob.MobAppearType;

import java.time.Instant;
import java.util.Optional;

public class JobQuest extends ScriptHandler {
    final static int CROCELL = 9400611;
    final static int CROCELL_PRE_FIELD = 677000006;
    final static int CROCELL_FIELD = 677000007;
    final static int AMDUSIAS = 9400623;
    final static int AMDUSIAS_PRE_FIELD = 677000002;
    final static int AMDUSIAS_FIELD = 677000003;

    @Script("Enter_Darkportal_P")
    public static void enter_darkportal_p(ScriptManager sm) {
        // Demon's Doorway
        // Victoria Road - The Forest East of Henesys
        if(!sm.hasQuestStarted(28256)) {
            sm.sayOk("Demon's Doorway is closed right now.");
            return;
        }
        final Field newField;
        final Optional<Field> tryField = sm.getField().getFieldStorage().getFieldById(CROCELL_FIELD);
        if (tryField.isPresent()) {
            newField = tryField.get();
            if (newField.getUserPool().getCount() > 0) {
                sm.sayNext("Someone is already in that map.");
                return;
            }
            sm.sayNext("You are permitted to enter the Demon's Doorway.");
            newField.getMobPool().forEach((mob) -> {
                try (var lockedMob = mob.acquire()) {
                    mob.remove(Instant.now());
                }
            });
            newField.setMobSpawn(false);
            sm.warp(CROCELL_PRE_FIELD);
            sm.spawnMobInMap(CROCELL, MobAppearType.NORMAL, 342, 75, true, newField);
        }
    }

    @Script("Enter_Darkportal_H")
    // Demon's Doorway
    // Singing Mushroom Forest - Ghost Mushroom Forest
    public static void enter_darkportal_h(ScriptManager sm) {
        if(!sm.hasQuestStarted(28238)) {
            sm.sayOk("Demon's Doorway is closed right now.");
            return;
        }
        final Field newField;
        final Optional<Field> tryField = sm.getField().getFieldStorage().getFieldById(AMDUSIAS_FIELD);
        if (tryField.isPresent()) {
            newField = tryField.get();
            if (newField.getUserPool().getCount() > 0) {
                sm.sayNext("Someone is already in that map.");
                return;
            }
            sm.sayNext("You are permitted to enter the Demon's Doorway.");
            newField.getMobPool().forEach((mob) -> {
                try (var lockedMob = mob.acquire()) {
                    mob.remove(Instant.now());
                }
            });
            newField.setMobSpawn(false);
            sm.warp(AMDUSIAS_PRE_FIELD);
            sm.spawnMobInMap(AMDUSIAS, MobAppearType.NORMAL, 511, 35, true, newField);
        }
    }
}
