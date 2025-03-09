package mapleglory.script.continent;

import mapleglory.script.common.Script;
import mapleglory.script.common.ScriptHandler;
import mapleglory.script.common.ScriptManager;
import mapleglory.world.quest.QuestRecordType;

import java.util.Map;

public class Orbis extends ScriptHandler {
    @Script("oldBook1")
    public static void oldBook1(ScriptManager sm) {
        if (sm.hasQuestCompleted(QuestRecordType.AlcasterAndTheDarkCrystal.getQuestId())) {
            final int answer = sm.askMenu("Thanks to you, #b#t4031056##k is safely sealed. As a result, I used up about half of the power I have accumulated over the last 800 years...but can now die in peace. Would you happen to be looking for rare items by any chance? As a sign of appreciation for your hard work, I'll sell some items in my possession to you and ONLY you. Pick out the one you want!", Map.of(
                    0, "#t2050003# (Price : 600 mesos)",
                    1, "#t2050004# (Price : 800 mesos)",
                    2, "#t4006000# (Price : 5,500 mesos)",
                    3, "#t4006001# (Price : 5,500 mesos)"
            ));

            switch (answer) {
                case 0:
                    sellItem(sm,2050003, 300, "The item that cures the state of being sealed and cursed.");
                    break;
                case 1:
                    sellItem(sm, 2050004, 400, "The item that cures everything.");
                    break;
                case 2:
                    sellItem(sm, 4006000, 5000, "The item of magical power used for high level skills.");
                    break;
                case 3:
                    sellItem(sm, 4006001, 5000, "The item of summoning power used for high level skills.");
                    break;
            }
        } else if(sm.getLevel() > 54) {
            sm.sayOk("If you decide to help me, in return I will put items up for sale.");
        } else {
            sm.sayOk("I am Alcaster the Sorcerer, resident of this city for over 300 years, where I have worked on many charms and spells.");
        }
    }

    private static void sellItem(ScriptManager sm, int itemId, int unitPrice, String description) {
        final int nRetNum = sm.askNumber("So the item you need is #b#t" + itemId + "##k, right? That's " + description + " It's not an easy item to get, but for you, I'll sell it for cheap. It'll cost you #b" + unitPrice + " mesos #k per. How many would you like to buy?", 1, 1, 100);
        final int nPrice = unitPrice * nRetNum;
        if(!sm.askYesNo("Do you really want to buy #r" + nRetNum + " #t" + itemId + "#(s)#k? It'll cost you " + unitPrice + " mesos per #t" + itemId + "#, which is #r" + nPrice + "#k mesos in total.")) {
            sm.sayOk("I understand. You see, I have many different items here. Take a look. I am selling these items just for you. So I won't rob you at all.");
            return;
        }

        if(!sm.canAddMoney(-nPrice) || !sm.canAddItem(itemId, nRetNum)) {
            sm.sayOk("Are you sure you have enough mesos? Please check if your use or etc. inventory is full and that you have at least #r" + nPrice + "#k mesos.");
            return;
        }

        sm.addItem(itemId, nRetNum);
        sm.sayOk("Thank you. If some other day you are in need of items, stop by. I may have gotten old with time, but I can still make magic items easily.");
    }

    @Script("oldBook2")
    public static void oldBook2(ScriptManager sm) {
        if (!sm.hasQuestStarted(QuestRecordType.WheresHella.getQuestId())) {
            sm.sayOk("Are you looking for #bHella#k? Technically she lives here, but you won't be able to find her these days. A few months ago, she left town suddenly and never came back. It won't do much good to stop by her house, but at least the housekeeper should be there. How about talking to her?");
        } else if(!sm.hasQuestStarted(QuestRecordType.TheSmallGraveThatsHidden.getQuestId())) {
            sm.sayOk("Where has #bHella#k gone... what? You know that she's alright? Hmmm... I don't know if I should trust a stranger's word, but if it's true, that's great. Of course you already warned Jade, right? Out of everyone, he is the most worried about her.");
        } else {
            sm.sayOk("Monsters have been a lot more evil and cruel lately. And what if they come here?? I hope that never happens, right? Right?");
        }
    }

    @Script("oldBook5")
    public static void oldBook5(ScriptManager sm) {
        final String goAway = "I'm working on an important spell, so please don't disturb me and leave immediately. I can't concentrate when a stranger hangs around my house. Please go away...";

        if (sm.hasQuestCompleted(QuestRecordType.AcquiringTheFairyDust.getQuestId())) {
            final int answer = sm.askMenu("Hella is a good child. Anything I ask, whether difficult or not, she does without complaining about anything. One day she will become a much better witch than I am. What do you want from me anyway??", Map.of(
                    0, "I want to make #t4005004#",
                    1, "Nothing, never mind."
            ));

            switch (answer) {
                case 0:
                    if (!sm.askYesNo("#b#t4005004##k?? How did you... did you hear about it from #b#p2020005##k? Yes, I know how to refine it, but... this ore is very hard to get. To make #b1 #t4005004##k, I need #b10 #t4004004#s#k and 50000 mesos. Do you want one?")) {
                        sm.sayOk("#b#t4005004##k. I haven't seen it for a long time... it's been hundreds of years since I last refined it, so I can barely remember how I did it... of course you won't have it now...");
                        return;
                    }

                    if (!sm.canAddMoney(-50000) || !sm.hasItem(4004004,10) || !sm.canAddItem(4005004, 1)) {
                        sm.sayOk("Are you out of mesos? Make sure you have #b10 #t4004004#s#k, 50000 mesos and space in your ETC inventory.");
                        return;
                    }

                    sm.addMoney(-50000);
                    sm.removeItem(4004004, 10);
                    sm.addItem(4005004, 1);

                    sm.sayOk("Here, take #b1 #t4004004##k. It's been so long since I last made one, I hope it worked... By the way, how did you get the crystal ores? You must be something special indeed. Either way, it's an amazing item. Please put it to good use.");
                case 1:
                    sm.sayOk(goAway);
            }
        } else {
            sm.sayOk(goAway);
        }
    }
}
