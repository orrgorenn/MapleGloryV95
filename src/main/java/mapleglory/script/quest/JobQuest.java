package mapleglory.script.quest;

import mapleglory.script.common.Script;
import mapleglory.script.common.ScriptHandler;
import mapleglory.script.common.ScriptManager;
import mapleglory.world.field.Field;
import mapleglory.world.field.mob.MobAppearType;
import mapleglory.world.quest.QuestRecordType;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class JobQuest extends ScriptHandler {
    final static int CROCELL = 9400611;
    final static int CROCELL_PRE_FIELD = 677000006;
    final static int CROCELL_FIELD = 677000007;
    final static int AMDUSIAS = 9400623;
    final static int AMDUSIAS_PRE_FIELD = 677000002;
    final static int AMDUSIAS_FIELD = 677000003;
    final static int VALEFOR_PRE_FIELD = 677000008;
    final static int VALEFOR_FIELD = 677000009;
    final static int VALEFOR = 9400613;


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

    @Script("Enter_Darkportal_T")
    public static void enterDarkportalT(ScriptManager sm) {
        // Demon's Doorway
        // Swamp Region - Dangerous Croco
        if(!sm.hasQuestStarted(28219)) {
            sm.sayOk("Demon's Doorway is closed right now.");
            return;
        }
        final Field newField;
        final Optional<Field> tryField = sm.getField().getFieldStorage().getFieldById(VALEFOR_FIELD);
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
            sm.warp(VALEFOR_PRE_FIELD);
            sm.spawnMobInMap(VALEFOR, MobAppearType.NORMAL, 359, 66, true, newField);
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

    @Script("dual_wallpaper")
    public static void dual_wallpaper(ScriptManager sm) {
        if(sm.hasQuestStarted(2358)) {
            final int mapId = sm.getFieldId();
            String adding = getLocationKey(mapId);

            if(sm.askYesNo("There is an empty space here for you to put up the poster. Do you wish to attach the poster here?")) {
                Set<Character> posterSet = getQuestProgress(sm.getQRValue(QuestRecordType.DualBladeDualWallpaper));
                posterSet.add(adding.charAt(0));
                if (posterSet.contains('1') && posterSet.contains('2') && posterSet.contains('3')) {
                    sm.setQRValue(QuestRecordType.DualBladeDualWallpaper, "211"); // Mark quest as complete
                } else {
                    sm.setQRValue(QuestRecordType.DualBladeDualWallpaper, setToString(posterSet)); // Update progress
                }

                sm.sayOk("The poster has been attached.");
            }
        }
    }

    @Script("dual_blueAlcohol")
    public static void dualBlueAlcohol(ScriptManager sm) {
        final int mapId = sm.getFieldId();
        String adding = getLocationKey(mapId);

        if(sm.hasQuestStarted(2358)) {
            if(sm.askYesNo("It's a half-filled blue bottle... Do you wish to install the bomb?")) {
                Set<Character> posterSet = getQuestProgress(sm.getQRValue(QuestRecordType.DualBladeDualWallpaper));
                posterSet.add(adding.charAt(0));
                if (posterSet.contains('1') && posterSet.contains('2') && posterSet.contains('3')) {
                    sm.setQRValue(QuestRecordType.DualBladeDualWallpaper, "211"); // Mark quest as complete
                } else {
                    sm.setQRValue(QuestRecordType.DualBladeDualWallpaper, setToString(posterSet)); // Update progress
                }

                sm.sayOk("The bomb has been installed.");
            }
        }
    }

    private static String setToString(Set<Character> posterSet) {
        StringBuilder sb = new StringBuilder();
        for (char c : posterSet) {
            sb.append(c);
        }
        return sb.toString();
    }

    private static String getLocationKey(int mapId) {
        return switch (mapId) {
            case 103010100 -> "2";
            case 103000003 -> "3";
            default -> "1";
        };
    }

    private static Set<Character> getQuestProgress(String questData) {
        Set<Character> posterSet = new HashSet<>();
        if (questData != null) {
            for (char c : questData.toCharArray()) {
                posterSet.add(c);
            }
        }
        return posterSet;
    }
}
