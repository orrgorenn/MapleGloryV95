package mapleglory.script;

import mapleglory.script.common.Script;
import mapleglory.script.common.ScriptHandler;
import mapleglory.script.common.ScriptManager;
import mapleglory.world.field.mob.MobAppearType;

public final class Consume extends ScriptHandler {
    @Script("consume_2430112")
    public static void consume_2430112(ScriptManager sm) {
        // Miracle Cube Fragment (2430112)
        if (sm.hasItem(2430112, 10)) {
            if (sm.canAddItem(2049400, 1) && sm.removeItem(2430112, 10)) {
                sm.addItem(2049400, 1); // Advanced Potential Scroll
            } else {
                sm.sayNext("Please check if your inventory is full or not.");
            }
        } else if (sm.hasItem(2430112, 5)) {
            if (sm.canAddItem(2049401, 1) && sm.removeItem(2430112, 5)) {
                sm.addItem(2049401, 1); // Potential Scroll
            } else {
                sm.sayNext("Please check if your inventory is full or not.");
            }
        }
    }

    @Script("blackBag")
    public static void blackBag(ScriptManager sm) {
        // Black Bag (2430032)
        sm.spawnMob(9300388, MobAppearType.NORMAL, sm.getUser().getX(), sm.getUser().getY(), false, true);
        sm.removeItem(2430032);
    }
}
