package mapleglory.script.boss;

import mapleglory.script.common.Script;
import mapleglory.script.common.ScriptHandler;
import mapleglory.script.common.ScriptManager;
import mapleglory.world.field.Field;
import mapleglory.world.field.mob.MobAppearType;

import java.time.Instant;

public class Scarlion extends ScriptHandler {
    final static int SCARLION_BOSS = 9420546;
    @Script("MalaysiaBoss_GL")
    public static void MalaysiaBossGL(ScriptManager sm) {
        if(sm.askYesNo("Do you want to go to the Spooky World Entrance?")) {
            sm.partyWarpInstance(551030200, "sp", 551030100, 60 * 60);
        }
    }

    @Script("myboss0")
    public static void myboss0(ScriptManager sm) {
        sm.broadcastMessage("Beware! The furious Scarlion has shown himself!", true);
        sm.spawnMob(SCARLION_BOSS, MobAppearType.NORMAL, -527, 637, true, true);
    }

    @Script("myboss1")
    public static void myboss1(ScriptManager sm) {
        sm.broadcastMessage("Beware! The furious Scarlion has shown himself!", true);
        sm.spawnMob(SCARLION_BOSS, MobAppearType.NORMAL, -238, 636, true, true);
    }

    @Script("Malay_Warp")
    public static void malayWarp(ScriptManager sm) {
        if(sm.askYesNo("Do you want to go out?")) {
            final Field field = sm.getField();
            field.setMobSpawn(false);
            field.getMobPool().respawnMobs(Instant.MAX);
            sm.partyWarp(551030100, "sp");
            sm.setReactorState(5511000, 0);
            sm.setReactorState(5511001, 0);
        }
    }
}
