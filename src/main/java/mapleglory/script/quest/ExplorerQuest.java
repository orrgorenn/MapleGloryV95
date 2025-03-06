package mapleglory.script.quest;

import mapleglory.script.common.Script;
import mapleglory.script.common.ScriptHandler;
import mapleglory.script.common.ScriptManager;
import mapleglory.util.Tuple;
import mapleglory.world.field.mob.MobAppearType;
import mapleglory.world.item.InventoryType;
import mapleglory.world.job.Job;
import mapleglory.world.quest.QuestRecordType;

import java.util.List;
import java.util.Map;

public final class ExplorerQuest extends ScriptHandler {
    public static final int MARBAS = 9400612;

    public static final int CLERIC = 230;
    public static final int WIZARD_FP = 210;
    public static final int WIZARD_IL = 220;
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

    @Script("enter_thief")
    public static void enter_thief(ScriptManager sm) {
        if (sm.hasQuestStarted(22515) || sm.hasQuestStarted(22516) || sm.hasQuestStarted(22517) || sm.hasQuestStarted(22518)) {
            sm.warpInstance(910310000, "start", 103010000, 60 * 30);
            return;
        }
        sm.warp(910310000);
    }

    @Script("enter_magicion")
    public static void enter_magicion(ScriptManager sm) {
        if (sm.hasQuestStarted(22515) || sm.hasQuestStarted(22516) || sm.hasQuestStarted(22517) || sm.hasQuestStarted(22518)) {
            sm.warpInstance(910120000, "start", 101000000, 60 * 30);
            return;
        }
        sm.warp(910120000);
    }

    @Script("rogue")
    public static void rogue(ScriptManager sm) {
        // Dark Lord : Thief Job Advancement
        if(sm.getUser().getJob() == 0) {
            sm.sayNext("Want to be a #rthief#k? There are some standards to meet. because we can't just accept EVERYONE in... #bYour level should be at least 10, with at least 25 DEX#k. Let's see.");
            if(sm.getUser().getLevel() >= 10) {
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
                    if(sm.hasQuestStarted(2351)) {
                        sm.setQRValue(QuestRecordType.DualBladeInfiltration, "1");
                    }
                    sm.sayNext("Alright, from here out, you are a part of us! You'll be living the life of a wanderer at ..., but just be patient as soon, you'll be living the high life. Alright, it ain't much, but I'll give you some of my abilities... HAAAHHH!!!");
                    sm.sayBoth("You've gotten much stronger now. Plus every single one of your inventories have added slots. A whole row, to be exact. Go see for it yourself. I just gave you a little bit of #bSP#k. When you open up the #bSkill#k menu on the lower left corner of the screen, there are skills you can learn by using SP's. One warning, though: You can't raise it all together all at once. There are also skills you can acquire only after having learned a couple of skills first.");
                    sm.sayBoth("Now a reminder. Once you have chosen, you cannot change up your mind and try to pick another path. Go now, and live as a proud Thief.");
                }
            } else {
                sm.sayOk("Train a bit more until you reach the base requirements and I can show you the way of the #rThief#k.");
            }
        } else if(sm.getLevel() >= 30 && sm.getJob() == Job.ROGUE) {
            if(!(sm.hasItem(4031011, 1) || sm.hasItem(4031012, 1))) {
                if(!sm.askYesNo("Hmmm...you seem to have gotten a whole lot stronger. You got rid of the old, weak self and and look much more like a thief now. Well, what do you think? Don't you want to get even more powerful than that? Pass a simple test and I'll do just that for you. Do you want to do it?")) {
                    sm.sayNext("Really? It will help you out a great deal on your journey if you get stronger fast...if you choose to change your mind in the future, please feel free to come back. Know that I'll make you much more powerful than you are right now.");
                    return;
                }

                sm.sayNext("Good thinking. But, I need to make sure you are as strong as you look. It's not a hard test, one that should be easy for you to pass. First, take this letter...make sure you don't lose it.");
                if(!sm.canAddItem(4031011, 1)) { // Dark Lord's Letter
                    sm.sayOk("Please make room in your inventory.");
                    return;
                }

                sm.addItem(4031011, 1);
                sm.sayBoth("Please take this letter to #b#p1072003##k at #b#m103030400##k near Kerning City. He's doing the job of an instructor in place of me. Give him the letter and he'll give you the test for me. If you want more details, hear it straight from him. I'll be wishing you good luck.");
            } else if (!sm.hasItem(4031012, 1) && sm.hasItem(4031011, 1)) {
                sm.sayNext("Still haven't met the person yet? Find #b#p1072003##k who's around #b#m103030400##k near Kerning City. Give the letter to him and he may let you know what to do.");
            } else if (sm.hasItem(4031012, 1) && !sm.hasItem(4031011, 1)) {
                sm.sayNext("Hmmm...so you got back here safely. I knew that test would be too easy for you. I admit, you are a great great thief. Now...I'll make you even more powerful than you already are. But, before all that...you need to choose one of two ways. It'll be a difficult decision for you to make, but...if you have any questions, please ask.");
                final int answer = sm.askMenu("I'll choose my occupation!", Map.of(
                        0, "Assassin",
                        1, "Bandit"
                ));

                if(answer == 0) {
                    // Assassin
                    if (!sm.askYesNo("So you want to make the 2nd job advancement as the #bAssassin#? Once you have made the decision, you can't go back and change your mind. You ARE sure about this, right?")) {
                        sm.sayNext("Really? Have to give more thought to it, huh? Take your time, take your time. This is not something you should take lightly ... come talk to me once you have made your decision.");
                        return;
                    }

                    if(sm.getUser().getCharacterStat().getSp().getNonExtendSp() > (sm.getLevel() - 70) * 3) {
                        sm.sayNext("Hmmm...you have too much SP...you can't make the 2nd job advancement with that many SP in store. Use more SP on the skills on the 1st level and then come back.");
                        return;
                    }

                    sm.setJob(Job.ASSASSIN);
                    sm.removeItem(4031012);
                    sm.addInventorySlots(InventoryType.EQUIP, 4);
                    sm.addInventorySlots(InventoryType.ETC, 4);
                    sm.sayNext("Alright, from here on out you are the #bAssassin#. Assassins revel in shadows and darkness, waiting until the right time comes for them to stick a dagger through the enemy's heart, suddenly and swiftly... please keep training. I'll make you even more powerful than you are right now!");
                    sm.sayBoth("I have just given you a book that gives you the the list of skills you can acquire as an assassin. I have also added a whole row to your use inventory, along with boosting up your max HP and MP...go see for it yourself.");
                    sm.sayBoth("I have also given you a little bit of #bSP#. Open the #bSkill Menu# located at the bottom left corner. You'll be able to boost up the newly-acquired 2nd level skills. A word of warning though: You can't boost them up all at once. Some of the skills are only available after you have learned other skills. Make sure to remember that.");
                    sm.sayBoth("Assassins have to be strong. But remember that you can't abuse that power and use it on a weakling. Please use your enormous power the right way, because...for you to use that the right way, that is much harder than just getting stronger. Find me after you have advanced much further.");
                } else if(answer == 1) {
                    // Bandit
                    if (!sm.askYesNo("So you want to make the 2nd job advancement as the #bBandit#? Once you have made the decision, you can't go back and change your mind. You ARE sure about this, right?")) {
                        sm.sayNext("Really? Have to give more thought to it, huh? Take your time, take your time. This is not something you should take lightly ... come talk to me once you have made your decision.");
                        return;
                    }

                    if(sm.getUser().getCharacterStat().getSp().getNonExtendSp() > (sm.getLevel() - 30) * 3) {
                        sm.sayNext("Hmmm...you have too much SP...you can't make the 2nd job advancement with that many SP in store. Use more SP on the skills on the 1st level and then come back.");
                        return;
                    }

                    sm.setJob(Job.BANDIT);
                    sm.removeItem(4031012);
                    sm.addInventorySlots(InventoryType.EQUIP, 4);
                    sm.addInventorySlots(InventoryType.ETC, 4);
                    sm.sayNext("Alright, from here on out you are the #bBandit#k. Bandits have quick hands and quicker feet to dominate the enemies... please keep training. I'll make you even more powerful than you are right now!");
                    sm.sayBoth("I have just given you a book that gives you the the list of skills you can acquire as an assassin. I have also added a whole row to your use inventory, along with boosting up your max HP and MP...go see for it yourself.");
                    sm.sayBoth("I have also given you a little bit of #bSP#k. Open the #bSkill Menu#k located at the bottom left corner. You'll be able to boost up the newly-acquired 2nd level skills. A word of warning though: You can't boost them up all at once. Some of the skills are only available after you have learned other skills. Make sure to remember that.");
                    sm.sayBoth("Bandits have to be strong. But remember that you can't abuse that power and use it on a weakling. Please use your enormous power the right way, because...for you to use that the right way, that is much harder than just getting stronger. Find me after you have advanced much further.");
                }
            }
        } else {
            sm.sayNext("Exploring is good, and getting stronger is good and all... but don't you want to enjoy living the life as you know it? How about becoming a Rouge like us and really LIVE the life? Sounds fun, isn't it?");
        }
    }

    @Script("change_rogue")
    public static void change_rogue(ScriptManager sm) {
        // 2nd Job Advancement Job Instructor
        if(sm.hasItem(4031011, 1) && !sm.hasItem(4031013, 1)) {
            sm.sayNext("Hmmm...it is definitely the letter from #b#p1052001##k... so you came all the way here to take the test and make the 2nd job advancement as the rogue. Alright, I'll explain the test to you. Don't sweat it much, though; it's not that complicated.");
            sm.sayBoth("I'll send you to a hidden map. You'll see monsters not normally seen in normal fields. They look the same like the regular ones, but with a totally different attitude. They neither boost your experience level nor provide you with item.");
            sm.sayBoth("You'll be able to acquire a marble called #b#t4031013##k while knocking down those monsters. It is a special marble made out of their sinister, evil minds. Collect 30 of those, then go talk to a colleague of mine in there. That's how you pass the test.");
            if(!sm.askYesNo("Once you go inside, you can't leave until you take care of your mission. If you die, your experience level will decrease...so you better really buckle up and get ready...well, do you want to go for it now?")) {
                sm.sayNext("You don't seem too prepared for this. Find me when you ARE ready. There are neither portals or stores inside, so you better get 100% ready for it.");
                return;
            }

            sm.sayNext("Alright! I'll let you in! Defeat the monster inside to earn 30 Dark Marble and then talk to my colleague inside; he'll give you #b#t4031012##k as a proof that you've passed the test. Best of luck to you. ");
            sm.warp(910370000);
        } else if (sm.hasItem(4031011, 1) && sm.hasItem(4031013)) {
            if(!sm.askYesNo("So you've given up in the middle of this before. Don't worry about it, because you can always retake the test. Now...do you want to go back in and try again?")) {
                sm.sayNext("You don't seem too prepared for this. Find me when you ARE ready. There are neither portals or stores inside, so you better get 100% ready for it.");
                return;
            }

            sm.sayNext("Alright! I'll let you in! Sorry to say this, but I have to take away all your marbles beforehand. Defeat the monsters inside, collect 30 Dark Marbles, then strike up a conversation with a colleague of mine inside. He'll give you the #b#t4031012##k, the proof that you've passed the test. Best of luck to you.");
            sm.removeItem(4031013);
            sm.warp(910370000);
        }
    }

    @Script("inside_rogue")
    public static void inside_rogue(ScriptManager sm) {
        if(sm.hasItem(4031013, 30)) {
            sm.sayNext("Ohhhhh...you collected all 30 Dark Marbles!! Wasn't it difficult?? Alright. You've passed the test and for that, I'll reward you #b#t4031012##k. Take that item and go back to Kerning City.");
            sm.removeItem(4031013);
            sm.removeItem(4031011);
            sm.addItem(4031012, 1);
            sm.warp(103030400);
        } else {
            if(sm.askYesNo("What's going on? Doesn't look like you have collected 30 #b#t4031013##k yet... If you're having problems with it, then you can leave, come back and try it again. So... wanna give up and get out of here?")) {
                sm.sayNext("Really... alright, I'll let you out. Please don't give up, though. You can always try again, so do not give up. Until then, bye...");
                sm.warp(103030400);
            } else {
                sm.sayNext("That's right! Stop acting weak and start collecting the marbles. Talk to me when you have collected 30 #b#t4031013##k.");
            }
        }
    }

    @Script("bowman")
    public static void bowman(ScriptManager sm) {
        // Athena Pierce : Bowman Job Advancement
        if(sm.getUser().getJob() == 0) {
            sm.sayNext("So you decided to become a #rbowman#k? There are some standards to meet, y'know... #bYour level should be at least 10, with at least DEX 25#k. Let's see.");
            if(sm.getUser().getLevel() >= 10) {
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
            // 1st Job
            sm.sayNext("Do you want to become a #rwarrior#k? You need to meet some criteria in order to do so.#bYou should be at least in level 10, and at least 35 STR#k. Let's see...");
            if(sm.getUser().getLevel() >= 10) {
                sm.sayBoth("It is an important and final choice. You will not be able to turn back.");
                if (!sm.addItem(1302077, 1)) {
                    sm.sayOk("Make some room in your inventory and talk back to me.");
                    return;
                }
                sm.addInventorySlots(InventoryType.EQUIP, 4);
                sm.addInventorySlots(InventoryType.ETC, 4);
                sm.setJob(Job.WARRIOR);
                sm.sayNext("From here on out, you are going to the Warrior path. This is not an easy job, but if you have discipline and confidence in your own body and skills, you will overcome any difficulties in your path. Go, young Warrior!");
                sm.sayBoth("You've gotten much stronger now. Plus every single one of your inventories have added slots. A whole row, to be exact. Go see for it yourself. I just gave you a little bit of #bSP#k. When you open up the #bSkill#k menu on the lower left corner of the screen, there are skills you can learn by using SP's. One warning, though: You can't raise it all together all at once. There are also skills you can acquire only after having learned a couple of skills first.");
                sm.sayBoth("Now a reminder. Once you have chosen, you cannot change up your mind and try to pick another path. Go now, and live as a proud Bowman.");
            } else {
                sm.sayOk("Train a bit more until you reach the base requirements and I can show you the way of the #rWarrior#k.");
            }
        }
    }

    @Script("inside_magician")
    public static void inside_magician(ScriptManager sm) {
        if (!sm.hasItem(4031013, 30)) {
            if(sm.askMenu("You will have to collect me #b30 #t4031013##k. Good luck.", Map.of(0, "#bI would like to leave.")) == 0) {
                sm.warp(101040300);
            }
        } else {
            sm.sayNext("Ohhhhh.. you collected all 30 Dark Marbles!! It should have been difficult... just incredible! Alright. You've passed the test and for that, I'll reward you #bThe Proof of a Hero#k. Take that and go back to Ellinia.");
            sm.removeItem(4031013);
            sm.forceCompleteQuest(100007);
            if(!sm.canAddItem(4031012, 1)) {
                sm.sayOk("Make some space in your inventory and talk back to me.");
                return;
            }
            sm.addItem(4031012, 1);
            sm.warp(101040300);
        }
    }

    @Script("change_magician")
    public static void change_magician(ScriptManager sm) {
        if (sm.hasQuestCompleted(100007)) {
            sm.sayOk("You're truly a hero!");
        } else if (sm.hasQuestCompleted(100006)) {
            sm.sayNext("Alright, I'll let you in! Defeat the monsters inside, collect 30 Dark Marbles, then talk to a colleague of mine inside. " +
                    "He'll give you #bThe Proof of a Hero#k, the proof that you've passed the test. Best of luck to you.");

            // Send the player to the test map
            sm.warp(910140000);
        } else if (sm.hasQuestStarted(100006)) {
            sm.sayNext("Hmmm... it is definitely the letter from #bGrendel the Really Old#k... " +
                    "so you came all the way here to take the test and make the 2nd job advancement as a magician. " +
                    "Alright, I'll explain the test to you. Don't sweat it too much, it's not that complicated.");

            sm.sayNext("I'll send you to a hidden map. You'll see monsters you don't normally see. " +
                    "They look similar to the regular ones but with a totally different attitude. " +
                    "They neither boost your experience level nor provide you with items.");

            sm.sayNext("You'll be able to acquire a marble called #b#t4031013##k while knocking down those monsters. " +
                    "It is a special marble made out of their sinister, evil minds. Collect 30 of those, " +
                    "and then go talk to a colleague of mine in there. That's how you pass the test.");

            if (sm.askYesNo("Once you go inside, you can't leave until you complete your mission. " +
                    "If you die, your experience level will decrease. So you better really buckle up and get ready... " +
                    "Well, do you want to go for it now?")) {
                sm.sayNext("Alright, I'll let you in! Defeat the monsters inside, collect 30 Dark Marbles, " +
                        "then talk to a colleague of mine inside. He'll give you #bThe Proof of a Hero#k, " +
                        "the proof that you've passed the test. Best of luck to you.");
                sm.forceCompleteQuest(100006);
                sm.forceStartQuest(100007);
                sm.removeItem(4031009, 1);
                sm.warp(910140000);
            } else {
                sm.sayOk("Come back when you are ready.");
            }
        } else {
            sm.sayOk("I can show you the way once you're ready for it.");
        }
    }

    @Script("magician")
    public static void magician(ScriptManager sm) {
        // Grendel the Really Old : Magician Job Advancement
        if(sm.getUser().getJob() == Job.BEGINNER.getJobId()) {
            sm.sayNext("Want to be a #rmagician#k? There are some standards to meet. because we can't just accept EVERYONE in... #bYour level should be at least 8, with getting 20 INT#k as your top priority. Let's see...");
            if(sm.getUser().getLevel() >= 8) {
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
        } else if (sm.getUser().getJob() == Job.MAGICIAN.getJobId() && sm.getLevel() >= 30) {
            if (sm.hasItem(4031012)) { // Player has Proof of a Hero
                sm.sayNext("I see you have done well. I will allow you to take the next step on your long road.");

                final int choice = sm.askMenu("Now... have you made up your mind? Please choose the job you'd like for your 2nd job advancement:", Map.of(
                        0, "Wizard (Fire / Poison)",
                        1, "Wizard (Ice / Lightning)",
                        2, "Cleric"
                ));

                Job newJob;
                if (choice == 0) {
                    newJob = Job.WIZARD_FP;
                    sm.sayNext("Magicians that master #rFire/Poison-based magic#k.\r\n\r\n" +
                            "#bWizards#k are an active class that deal magical, elemental damage. " +
                            "With skills like #rMeditation#k and #rSlow#k, #bWizards#k can increase magic attack and reduce enemy mobility. " +
                            "Fire/Poison Wizards use powerful flame and poison attacks.");
                } else if (choice == 1) {
                    newJob = Job.WIZARD_IL;
                    sm.sayNext("Magicians that master #rIce/Lightning-based magic#k.\r\n\r\n" +
                            "#bWizards#k are an active class that deal magical, elemental damage. " +
                            "With skills like #rMeditation#k and #rSlow#k, #bWizards#k can increase magic attack and reduce enemy mobility. " +
                            "Ice/Lightning Wizards use freezing ice and striking lightning attacks.");
                } else {
                    newJob = Job.CLERIC;
                    sm.sayNext("Magicians that master #rHoly magic#k.\r\n\r\n" +
                            "#bClerics#k are a powerful supportive class, welcomed in any party. " +
                            "They have the power to #rHeal#k themselves and their allies, and can buff stats with #rBless#k. " +
                            "Clerics are particularly effective against undead monsters.");
                }

                final boolean confirm = sm.askYesNo("So you want to make the second job advancement as a " +
                        (newJob == Job.WIZARD_FP ? "#bWizard (Fire / Poison)#k?" :
                                newJob == Job.WIZARD_IL ? "#bWizard (Ice / Lightning)#k?" : "#bCleric#k?") +
                        " You know you won’t be able to choose a different job after this, right?");

                if (confirm) {
                    sm.removeItem(4031012, 1); // Remove Proof of a Hero
                    sm.setJob(newJob);
                    sm.sayNext("Alright, you're now a " +
                            (newJob == Job.WIZARD_FP ? "#bWizard (Fire / Poison)#k!" :
                                    newJob == Job.WIZARD_IL ? "#bWizard (Ice / Lightning)#k!" : "#bCleric#k!") +
                            " Magicians and wizards have incredible magical prowess, able to pierce the minds of monsters with ease... Train yourself daily.");
                    sm.sayBoth("I have given you a book listing the skills you can acquire as a " +
                            (newJob == Job.WIZARD_FP ? "#bWizard (Fire / Poison)#k." :
                                    newJob == Job.WIZARD_IL ? "#bWizard (Ice / Lightning)#k." : "#bCleric#k.") +
                            " Also, your inventory has expanded and your max HP/MP have increased. Check them out.");
                    sm.sayBoth("I have also given you a little bit of #bSP#k. Open the #bSkill Menu#k and start boosting your new 2nd-level skills.");
                    sm.sayBoth("This is all I can teach you. Use your new powers wisely, and continue training. Good luck!");
                } else {
                    sm.sayOk("Take your time and come back when you're ready.");
                }
            } else {
                if (sm.hasItem(4031009)) {
                    sm.sayOk("Go and see the #b#p1072001##k at #b#m101020000##k near Ellinia.");
                } else {
                    sm.sayNext("Good decision. You look strong, but I need to test you first. Take my letter and bring it to the instructor near Ellinia.");
                    if (!sm.hasQuestStarted(100006)) {
                        sm.forceStartQuest(100006);
                    }
                    if (!sm.addItem(4031009, 1)) {
                        sm.sayOk("Make some space in your inventory and talk back to me.");
                    } else {
                        sm.sayNext("Take this letter to #b#p1072001##k near Ellinia. He will test you in my place. Best of luck.");
                    }
                }
            }
        } else if(sm.getLevel() >= 70) {
            final int jobId = sm.getUser().getJob();
            switch (jobId) {
                // go to priest
                case CLERIC -> {
                    log.debug("hey");
                }
                // go to arch_mage_fp
                case WIZARD_FP -> {

                }
                // go to arch_mage_il
                case WIZARD_IL -> {

                }
            }
        }
    }

    @Script("thief3")
    public static void thief3(ScriptManager sm) {
        boolean qualifyForJob3 = sm.getJob() == Job.ASSASSIN || sm.getJob() == Job.BANDIT;
        if(!qualifyForJob3) {
            sm.sayOk("May the gods be with you!");
            return;
        }
        if(sm.getLevel() >= 70) {
            if(sm.getUser().getCharacterStat().getSp().getNonExtendSp() > (sm.getLevel() - 70) * 3) {
                sm.sayOk("Hmm...You have too many #bSP#k. You can't make the job advancement with too many SP left.");
                return;
            }

            sm.sayNext("You are indeed a strong one.");
            if(!(sm.getUser().getCharacterStat().getSp().getNonExtendSp() > (sm.getLevel() - 70) * 3)) {
                if (sm.getJob() == Job.BANDIT) {
                    sm.setJob(Job.SHADOWER);
                    sm.sayOk("You are now a #bShadower#k.");
                } else if (sm.getJob() == Job.ASSASSIN) {
                    sm.setJob(Job.HERMIT);
                    sm.sayOk("You are now an #bHermit#k.");
                }
            }
        }
    }

    @Script("warrior3")
    public static void warrior3(ScriptManager sm) {
        boolean qualifyForJob3 = sm.getJob() == Job.FIGHTER || sm.getJob() == Job.PAGE || sm.getJob() == Job.SPEARMAN;
        if(!qualifyForJob3) {
            sm.sayOk("May the gods be with you!");
            return;
        }
        if(sm.getLevel() >= 70) {
            if(sm.getUser().getCharacterStat().getSp().getNonExtendSp() > (sm.getLevel() - 70) * 3) {
                sm.sayOk("Hmm...You have too many #bSP#k. You can't make the job advancement with too many SP left.");
                return;
            }

            sm.sayNext("You are indeed a strong one.");
            if(!(sm.getUser().getCharacterStat().getSp().getNonExtendSp() > (sm.getLevel() - 70) * 3)) {
                if (sm.getJob() == Job.FIGHTER) {
                    sm.setJob(Job.CRUSADER);
                    sm.sayOk("You are now a #bCrusader#k.");
                } else if (sm.getJob() == Job.PAGE) {
                    sm.setJob(Job.WHITE_KNIGHT);
                    sm.sayOk("You are now a #bWhite Knight#k.");
                } else if (sm.getJob() == Job.SPEARMAN) {
                    sm.setJob(Job.DRAGON_KNIGHT);
                    sm.sayOk("You are now an #bDragon Knight#k.");
                }
            }
        }
    }

    @Script("bowman3")
    public static void bowman3(ScriptManager sm) {
        boolean qualifyForJob3 = sm.getJob() == Job.HUNTER || sm.getJob() == Job.CROSSBOWMAN;
        if(!qualifyForJob3) {
            sm.sayOk("May the gods be with you!");
            return;
        }
        if(sm.getLevel() >= 70) {
            if(sm.getUser().getCharacterStat().getSp().getNonExtendSp() > (sm.getLevel() - 70) * 3) {
                sm.sayOk("Hmm...You have too many #bSP#k. You can't make the job advancement with too many SP left.");
                return;
            }

            sm.sayNext("You are indeed a strong one.");
            if(!(sm.getUser().getCharacterStat().getSp().getNonExtendSp() > (sm.getLevel() - 70) * 3)) {
                if (sm.getJob() == Job.HUNTER) {
                    sm.setJob(Job.RANGER);
                    sm.sayOk("You are now a #bRanger#k.");
                } else if (sm.getJob() == Job.CROSSBOWMAN) {
                    sm.setJob(Job.SNIPER);
                    sm.sayOk("You are now a #bSniper#k.");
                }
            }
        }
    }

    @Script("wizard3")
    public static void wizard3(ScriptManager sm) {
        boolean qualifyForJob3 = sm.getJob() == Job.WIZARD_FP || sm.getJob() == Job.WIZARD_IL || sm.getJob() == Job.CLERIC;
        if(!(qualifyForJob3)) {
            sm.sayOk("May the Gods be with you!");
            return;
        }
        if(sm.getLevel() >= 70) {
           if(sm.getUser().getCharacterStat().getSp().getNonExtendSp() > (sm.getLevel() - 70) * 3) {
               // add check if totalSP > lvl * 3
               sm.sayOk("Hmm...You have too many #bSP#k. You can't make the job advancement with too many SP left.");
               return;
           }
            sm.sayNext("You are indeed a strong one.");
            if(!(sm.getUser().getCharacterStat().getSp().getNonExtendSp() > (sm.getLevel() - 70) * 3)) {
                if (sm.getJob() == Job.WIZARD_FP) {
                    sm.setJob(Job.MAGE_FP);
                    sm.sayOk("You are now a #bFire/Poison Mage.#k");
                } else if (sm.getJob() == Job.WIZARD_IL) {
                    sm.setJob(Job.MAGE_IL);
                    sm.sayOk("You are now an #bIce/Lightning Mage#k.");
                } else if (sm.getJob() == Job.CLERIC) {
                    sm.setJob(Job.PRIEST);
                    sm.sayOk("You are now a #bPriest#k.");
                }
            }
        } else {
            sm.sayOk("Please make sure that you are eligible for the job advancement. (level 70+)");
        }
    }

    // TODO: move from here to job
    @Script("Enter_Darkportal_M")
    public static void enter_darkportal_m(ScriptManager sm) {
        if (!sm.hasQuestStarted(28198)) {
            sm.sayOk("...");
            return;
        }
        if (sm.askMenu("Are you sure you want to fight with Marbas the Demon?", Map.of(0, "I am sure.", 1, "Maybe another time.")) == 0) {
            sm.warp(677000001);
            sm.spawnMob(MARBAS, MobAppearType.NORMAL, 174, 70, true, false);
            sm.broadcastMessage("Kill Marbas!", false);
        }
    }

    @Script("q2230e")
    public static void q2230e(ScriptManager sm) {
        // Mar the fairy
        // A Mysterious Small Egg - End
        if(sm.askMenu("Hello, traveler... You have finally come to see me. Have you fulfilled your duties?", Map.of(0, "What duties? Who are you?")) == 0) {
            sm.sayNext("Have you found a small egg in your pocket? That egg is your duty, your responsibility. Life is hard when you're all by yourself. In times like this, there's nothing quite like having a friend that will be there for you at all times. Have you heard of a #bpet#k?\r\nPeople raise pets to ease the burden, sorrow, and loneliness, because knowing that you have someone, or something in this matter, on your side will really bring a peace of mind. But everything has consequences, and with it comes responsibility...");
            sm.sayBoth("Raising a pet requires a huge amount of responsibility. Remember a pet is a form of life, as well, so you'll need to feed it, name it, share your thoughts with it, and ultimately form a bond. That's how the owners get attached to these pets.");
            sm.sayBoth("I wanted to instill this in you, and that's why I sent you a baby that I cherish. The egg you have brought is #bRune Snail#k, a creature that is born through the power of Mana. Since you took great care of it as you brought the egg here, the egg will hatch soon.");
            sm.sayBoth("Rune Snail is a pet of many skills. It'll pick up items, feed you with potions, and do other things that will astound you. The downside is that since Rune Snail was born out of power of Mana, it's lifespan is very short. Once it turns into a doll, it'll never be able to be revived.");
            if(sm.askYesNo("Now do you understand? Every action comes with consequences, and pets are no exception. The egg of the snail shall hatch soon.")) {
                if(!sm.canAddItem(5000054, 1)) {
                    sm.sayOk("Please free a slot in your CASH inventory before you try to receive the pet...");
                    return;
                }
                sm.sayNext("This snail will only be alive for #b5 hours#k. Shower it with love. Your love will be reciprocated in the end.");
                sm.removeItem(4032086);
                sm.forceCompleteQuest(2230);
                sm.addItem(5000054, 1, 5);
            }
        }
    }
}
