package mapleglory.script.event;

import mapleglory.script.common.Script;
import mapleglory.script.common.ScriptHandler;
import mapleglory.script.common.ScriptManager;
import mapleglory.world.quest.QuestRecordType;


public class Gachapon extends ScriptHandler {
    final static int DEF_RETURN_MAP = 100000000;
    @Script("GachaponEvent")
    public static void gachaponEvent(ScriptManager sm) {
        final int returnMap = !sm.getQRValue(QuestRecordType.GachaponEvent).isEmpty() ? Integer.parseInt(sm.getQRValue(QuestRecordType.GachaponEvent)) : DEF_RETURN_MAP;

        if (sm.getFieldId() == 910030000) {
            if (sm.askYesNo("Are you sure you want to leave? You won't be able to return once you exit the map.")) {
                sm.warp(returnMap);
            }
        } else {
            sm.sayOk("Gachapon's are now live for testing!\r\nBut only if you can find a ticket...");
        }
    }
}
