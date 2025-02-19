package mapleglory.script.quest;

import mapleglory.script.common.Script;
import mapleglory.script.common.ScriptHandler;
import mapleglory.script.common.ScriptManager;
import mapleglory.util.Tuple;
import mapleglory.world.item.InventoryType;
import mapleglory.world.job.Job;

import java.util.List;

public final class ExplorerQuest extends ScriptHandler {
    @Script("enter_archer")
    public static void enter_archer(ScriptManager sm) {
        //  Power B. Fore : Entrance to Bowman Training Center (1012119)
        //  Singing Mushroom Forest : Spore Hill (100020000)
        if (sm.hasQuestStarted(22518)) {
            sm.warpInstance(910060100, "start", 100020000, 60 * 30);
            return;
        }
        sm.warp(910060000); // Victoria Road : Bowman Training Center
    }

    @Script("rogue")
    public static void rogue(ScriptManager sm) {
        // Dark Lord : Thief Job Advancement
        if(sm.getUser().getJob() == 0) {
            sm.sayNext("Want to be a #rthief#k? There are some standards to meet. because we can't just accept EVERYONE in... #bYour level should be at least 10, with at least 25 DEX#k. Let's see.");
            if(sm.getUser().getLevel() >= 10 && sm.canGetFirstJob(4)) {
                if(sm.askYesNo("Oh...! You look like someone that can definitely be a part of us... all you need is a little sinister mind, and... yeah... so, what do you think? Wanna be the Rogue?")) {
                    if (!sm.addItems(List.of(
                            Tuple.of(2070000, 500),
                            Tuple.of(1472061, 1),
                            Tuple.of(1332063, 1)
                    ))) {
                        sm.sayOk("Make some room in your inventory and talk back to me.");
                        return;
                    }
                    sm.addInventorySlots(InventoryType.EQUIP, 4);
                    sm.addInventorySlots(InventoryType.ETC, 4);
                    sm.setJob(Job.ROGUE);
                    sm.forceCompleteQuest(2079);
                    sm.sayNext("Alright, from here out, you are a part of us! You'll be living the life of a wanderer at ..., but just be patient as soon, you'll be living the high life. Alright, it ain't much, but I'll give you some of my abilities... HAAAHHH!!!");
                    sm.sayBoth("You've gotten much stronger now. Plus every single one of your inventories have added slots. A whole row, to be exact. Go see for it yourself. I just gave you a little bit of #bSP#k. When you open up the #bSkill#k menu on the lower left corner of the screen, there are skills you can learn by using SP's. One warning, though: You can't raise it all together all at once. There are also skills you can acquire only after having learned a couple of skills first.");
                    sm.sayBoth("Now a reminder. Once you have chosen, you cannot change up your mind and try to pick another path. Go now, and live as a proud Thief.");
                }
            } else {
                sm.sayOk("Train a bit more until you reach the base requirements and I can show you the way of the #rThief#k.");
            }
        }
    }

    @Script("bowman")
    public static void bowman(ScriptManager sm) {
        // Athena Pierce : Bowman Job Advancement
        if(sm.getUser().getJob() == 0) {
            sm.sayNext("So you decided to become a #rbowman#k? There are some standards to meet, y'know... #bYour level should be at least 10, with at least DEX 25#k. Let's see.");
            if(sm.getUser().getLevel() >= 10 && sm.canGetFirstJob(3)) {
                sm.sayBoth("It is an important and final choice. You will not be able to turn back.");
                if (!sm.addItems(List.of(
                        Tuple.of(1452051, 1),
                        Tuple.of(2060000, 1000)
                ))) {
                    sm.sayOk("Make some room in your inventory and talk back to me.");
                    return;
                }
                sm.addInventorySlots(InventoryType.EQUIP, 4);
                sm.addInventorySlots(InventoryType.ETC, 4);
                sm.setJob(Job.ARCHER);
                sm.sayNext("Alright, from here out, you are a part of us! You'll be living the life of a wanderer at ..., but just be patient as soon, you'll be living the high life. Alright, it ain't much, but I'll give you some of my abilities... HAAAHHH!!!");
                sm.sayBoth("You've gotten much stronger now. Plus every single one of your inventories have added slots. A whole row, to be exact. Go see for it yourself. I just gave you a little bit of #bSP#k. When you open up the #bSkill#k menu on the lower left corner of the screen, there are skills you can learn by using SP's. One warning, though: You can't raise it all together all at once. There are also skills you can acquire only after having learned a couple of skills first.");
                sm.sayBoth("Now a reminder. Once you have chosen, you cannot change up your mind and try to pick another path. Go now, and live as a proud Bowman.");
            } else {
                sm.sayOk("Train a bit more until you reach the base requirements and I can show you the way of the #rBowman#k.");
            }
        }
    }

    @Script("fighter")
    public static void fighter(ScriptManager sm) {
        // Dances with Balrog : Warrior Job Instructor
        if(sm.getUser().getJob() == 0) {
            sm.sayNext("Do you want to become a #rwarrior#k? You need to meet some criteria in order to do so.#bYou should be at least in level 10, and at least 35 STR#k. Let's see...");
            if(sm.getUser().getLevel() >= 10 && sm.canGetFirstJob(1)) {
                sm.sayBoth("It is an important and final choice. You will not be able to turn back.");
                if (!sm.addItem(1302077, 1)) {
                    sm.sayOk("Make some room in your inventory and talk back to me.");
                    return;
                }
                sm.addInventorySlots(InventoryType.EQUIP, 4);
                sm.addInventorySlots(InventoryType.ETC, 4);
                sm.setJob(Job.FIGHTER);
                sm.sayNext("From here on out, you are going to the Warrior path. This is not an easy job, but if you have discipline and confidence in your own body and skills, you will overcome any difficulties in your path. Go, young Warrior!");
                sm.sayBoth("You've gotten much stronger now. Plus every single one of your inventories have added slots. A whole row, to be exact. Go see for it yourself. I just gave you a little bit of #bSP#k. When you open up the #bSkill#k menu on the lower left corner of the screen, there are skills you can learn by using SP's. One warning, though: You can't raise it all together all at once. There are also skills you can acquire only after having learned a couple of skills first.");
                sm.sayBoth("Now a reminder. Once you have chosen, you cannot change up your mind and try to pick another path. Go now, and live as a proud Bowman.");
            } else {
                sm.sayOk("Train a bit more until you reach the base requirements and I can show you the way of the #rWarrior#k.");
            }
        }
    }

    @Script("magician")
    public static void magician(ScriptManager sm) {
        // Grendel the Really Old : Magician Job Advancement
        if(sm.getUser().getJob() == 0) {
            sm.sayNext("Want to be a #rmagician#k? There are some standards to meet. because we can't just accept EVERYONE in... #bYour level should be at least 8, with getting 20 INT#k as your top priority. Let's see...");
            if(sm.getUser().getLevel() >= 10 && sm.canGetFirstJob(2)) {
                sm.sayBoth("Oh...! You look like someone that can definitely be a part of us... all you need is a little sinister mind, and... yeah... so, what do you think? Wanna be the Magician?");
                if (!sm.addItem(1372043, 1)) {
                    sm.sayOk("Make some room in your inventory and talk back to me.");
                    return;
                }
                sm.addInventorySlots(InventoryType.EQUIP, 4);
                sm.addInventorySlots(InventoryType.ETC, 4);
                sm.setJob(Job.MAGICIAN);
                sm.sayNext("Alright, from here out, you are a part of us! You'll be living the life of a wanderer at ..., but just be patient as soon, you'll be living the high life. Alright, it ain't much, but I'll give you some of my abilities... HAAAHHH!!!");
                sm.sayBoth("You've gotten much stronger now. Plus every single one of your inventories have added slots. A whole row, to be exact. Go see for it yourself. I just gave you a little bit of #bSP#k. When you open up the #bSkill#k menu on the lower left corner of the screen, there are skills you can learn by using SP's. One warning, though: You can't raise it all together all at once. There are also skills you can acquire only after having learned a couple of skills first.");
                sm.sayBoth("But remember, skills aren't everything. Your stats should support your skills as a Magician, also. Magicians use INT as their main stat, and LUK as their secondary stat. If raising stats is difficult, just use #bAuto-Assign#k");
                sm.sayBoth("Now, one more word of warning to you. If you fail in battle from this point on, you will lose a portion of your total EXP. Be extra mindful of this, since you have less HP than most.");
                sm.sayBoth("This is all I can teach you. Good luck on your journey, young Magician.");
            } else {
                sm.sayOk("Train a bit more until you reach the base requirements and I can show you the way of the #rMagician#k.");
            }
        }
    }
}
