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
    final static int CROCELL_FIELD = 677000011;
    @Script("Enter_Darkportal_P")
    public static void enter_darkportal_p(ScriptManager sm) {
        // Demon's Doorway
        // Victoria Road - The Forest East of Henesys
        if(!sm.hasItem(4032494, 1)) {
            sm.sayOk("You need #v4032494# #t4032494# in order to pass here.");
            return;
        }
        sm.sayNext("You are permitted to enter the Demon's Doorway.");
        final Field newField;
        final Optional<Field> tryField = sm.getField().getFieldStorage().getFieldById(CROCELL_FIELD);
        if (tryField.isPresent()) {
            newField = tryField.get();
            newField.getMobPool().forEach((mob) -> {
                try (var lockedMob = mob.acquire()) {
                    mob.remove(Instant.now());
                }
            });
            newField.setMobSpawn(false);
            sm.warp(newField.getFieldId());
            sm.spawnMob(CROCELL, MobAppearType.NORMAL, 570, 142, true, false);
        }
    }
}
