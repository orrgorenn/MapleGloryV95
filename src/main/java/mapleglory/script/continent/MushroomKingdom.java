package mapleglory.script.continent;

import mapleglory.script.common.Script;
import mapleglory.script.common.ScriptHandler;
import mapleglory.script.common.ScriptManager;
import mapleglory.world.quest.QuestRecordType;

public class MushroomKingdom extends ScriptHandler {
    @Script("q2301s")
    // Grendel the Really Old
    // Endangered Mushking Empire - Start
    public static void q2301s(ScriptManager sm) {
        if (sm.askAccept("Now that you have made the job advancement, you look like you're ready for this. I have something I'd like to ask you for help. Are you willing to listen?")) {
            sm.sayNext("What happened is that the #bKingdom of Mushroom#k is currently in disarray. Kingdom of Mushroom is located near Henesys, featuring the peace-loving, intelligent King Mush. Recently, he began to feel ill, so he decided to appoint his only daughter #bPrincess Violetta#k. Something must have happened since then for the kingdom to be in its current state.");
            sm.sayBoth("I am not aware of the exact details, but it's obvious something terrible had taken place, so I think it'll be better if you go there and assess the damage yourself. An explorer like you seem more than capable of saving Kingdom of Mushroom. I have just written you a #brecommendation letter#k, so I suggest you head over to Kingdom of Mushroom immediately and look for the #bHead Patrol Officer#k.\n" +
                    "\n" +
                    "#fUI/UIWindow.img/QuestIcon/4/0#\n\n" +
                    "#v4032375# #t4032375#");
            sm.askYesNo("By the way, do you know where Kingdom of Mushroom is located? It'll be okay if you can find your way there, but if you don't mind, I can take you straight to the entrance.");
            if (sm.canAddItem(4032375, 1)) {
                if (!sm.hasItem(4032375, 1)) {
                    sm.addItem(4032375, 1);
                }
                sm.warp(106020000);
                sm.forceStartQuest(2301);
            } else {
                sm.sayOk("Please have a slot available in your Etc inventory.");
            }
        }
    }

    @Script("q2301e")
    // Head Security Officer
    // Endangered Mushking Empire - End
    public static void q2301e(ScriptManager sm) {
        if (!sm.hasItem(4032375, 1)) {
            sm.sayNext("What do you want, hmmm?");
            return;
        }
        sm.sayNext("Hmmm? Is that a #brecommendation letter from the job instructor#k??! What is this, are you the one that came to save us, the Kingdom of Mushroom?");
        sm.sayBoth("Hmmm... okay. Since the letter is from the job instructor, I suppose you are really the one. I apologize for not introducing myself to you earlier. I'm the #bHead Security Officer#k in charge of protecting King Mush. As you can see, this temporary hideout is protected by the team of security and soldiers. Our situation may be dire, but nevertheless, welcome to Kingdom of Mushroom.");
        sm.removeItem(4032375);
        sm.getUser().addQuestExp(6000);
        sm.forceCompleteQuest(2301);
        sm.forceStartQuest(2312);
    }
}
