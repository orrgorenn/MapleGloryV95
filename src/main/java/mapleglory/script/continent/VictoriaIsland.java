package mapleglory.script.continent;

import mapleglory.provider.reward.Reward;
import mapleglory.script.common.Script;
import mapleglory.script.common.ScriptHandler;
import mapleglory.script.common.ScriptManager;
import mapleglory.server.event.EventState;
import mapleglory.server.event.EventType;
import mapleglory.server.event.Subway;
import mapleglory.util.Util;
import mapleglory.world.job.Job;
import mapleglory.world.job.JobConstants;
import mapleglory.world.quest.QuestRecordType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class VictoriaIsland extends ScriptHandler {
    public static final int TICKET_TO_CONSTRUCTION_SITE_B1 = 4031036;
    public static final int TICKET_TO_CONSTRUCTION_SITE_B2 = 4031037;
    public static final int TICKET_TO_CONSTRUCTION_SITE_B3 = 4031038;
    public static final int KERNING_SQUARE_SUBWAY_1 = 103020010; // Kerning City -> Kerning Square
    public static final int KERNING_SQUARE_SUBWAY_2 = 103020011; // Kerning Square -> Kerning City
    public static final int REGULAR_SAUNA_PRICE = 4999;
    public static final int VIP_SAUNA_PRICE = 9999;

    @Script("victoria_taxi")
    public static void victoria_taxi(ScriptManager sm) {
        // Regular Cab in Victoria (1012000)
        //   Henesys : Henesys (100000000)
        //   Ellinia : Ellinia (101000000)
        //   Perion : Perion (102000000)
        //   Kerning City : Kerning City (103000000)
        //   Lith Harbor : Lith Harbor (104000000)
        //   Nautilus : Nautilus Harbor (120000000)
        final boolean isBeginner = JobConstants.isBeginnerJob(sm.getUser().getJob());
        final int price = isBeginner ? 100 : 1000;
        final List<Integer> towns = Stream.of(
                100000000, // Henesys : Henesys
                101000000, // Ellinia : Ellinia
                102000000, // Perion : Perion
                103000000, // Kerning City : Kerning City
                104000000, // Lith Harbor : Lith Harbor
                120000000 // Nautilus : Nautilus Harbor
        ).filter(mapId -> sm.getFieldId() != mapId).toList();
        final Map<Integer, String> options = createOptions(towns, (mapId) -> String.format("#m%d# (%d Mesos)", mapId, price));
        sm.sayNext("Hello! I'm #p1012000#, and I am here to take you to your destination, quickly and safely. #b#p1012000##k values your satisfaction, so you can always reach your destination at an affordable price. I am here to serve you.");
        final int answer = sm.askMenu("Please select your destination." + (isBeginner ? "\r\nWe have a special 90% discount for beginners." : ""), options);
        if (sm.askYesNo(String.format("You don't have anything else to do here, huh? Do you really want to go to #b#m%d##k? It'll cost you #b%d#k mesos.", towns.get(answer), price))) {
            if (sm.addMoney(-price)) {
                sm.warp(towns.get(answer));
            } else {
                sm.sayOk("You don't have enough mesos. Sorry to say this, but without them, you won't be able to ride the cab.");
            }
        } else {
            sm.sayOk("There's a lot to see in this town, too. Come back and find us when you need to go to a different town.");
        }
    }

    @Script("subway_ticket")
    public static void subway_ticket(ScriptManager sm) {
        // Jake : Subway Worker (1052006)
        //   Victoria Road : Subway Ticketing Booth (103000100)
        //   Kerning City Subway : Subway Ticketing Booth (103020000)
        final Map<Integer, String> options = new HashMap<>();
        if (sm.getLevel() >= 20) {
            options.put(0, itemName(TICKET_TO_CONSTRUCTION_SITE_B1)); // Shumi's Lost Coin
        }
        if (sm.getLevel() >= 30) {
            options.put(1, itemName(TICKET_TO_CONSTRUCTION_SITE_B2)); // Shumi's Lost Bundle of Money
        }
        if (sm.getLevel() >= 40) {
            options.put(2, itemName(TICKET_TO_CONSTRUCTION_SITE_B3)); // Shumi's Lost Sack of Money
        }
        if (options.isEmpty()) {
            sm.sayNext("You can enter the premise once you have bought the ticket; however it doesn't seem like you can enter here. There are foreign devices underground that may be too much for you to handle, so please train yourself, be prepared, and then come back.");
            return;
        }
        final int answer = sm.askMenu("You must purchase the ticket to enter. Once you have made the purchase, you can enter through #p1052007# on the right. What would you like to buy?", options);
        if (answer == 0) {
            if (sm.askYesNo("Will you purchase the Ticket to #bConstruction site B1#k? It'll cost you 500 Mesos. Before making the purchase, please make sure you have an empty slot on your ETC inventory.")) {
                if (sm.canAddItem(TICKET_TO_CONSTRUCTION_SITE_B1, 1) && sm.addMoney(-500)) {
                    sm.addItem(TICKET_TO_CONSTRUCTION_SITE_B1, 1);
                    sm.sayNext("You can insert the ticket in the #p1052007#. I heard Area 1 has some precious items available but with so many traps all over the place most come back out early. Wishing you the best of luck.");
                } else {
                    sm.sayNext("Are you lacking Mesos? Check and see if you have an empty slot on your ETC inventory or not.");
                }
            } else {
                sm.sayNext("You can enter the premise once you have bought the ticket. I heard there are strange devices in there everywhere but in the end, rare precious items await you. So let me know if you ever decide to change your mind.");
            }
        } else if (answer == 1) {
            if (sm.askYesNo("Will you purchase the Ticket to #bConstruction site B2#k? It'll cost you 1200 Mesos. Before making the purchase, please make sure you have an empty slot on your ETC inventory.")) {
                if (sm.canAddItem(TICKET_TO_CONSTRUCTION_SITE_B2, 1) && sm.addMoney(-1200)) {
                    sm.addItem(TICKET_TO_CONSTRUCTION_SITE_B2, 1);
                    sm.sayNext("You can insert the ticket in the #p1052007#. I heard Area 2 has rare, precious items available but with so many traps all over the place most come back out early. Please be safe.");
                } else {
                    sm.sayNext("Are you lacking Mesos? Check and see if you have an empty slot on your ETC inventory or not.");
                }
            } else {
                sm.sayNext("You can enter the premise once you have bought the ticket. I heard there are strange devices in there everywhere but in the end, rare precious items await you. So let me know if you ever decide to change your mind.");
            }
        } else if (answer == 2) {
            if (sm.askYesNo("Will you purchase the Ticket to #bConstruction site B3#k? It'll cost you 2000 Mesos. Before making the purchase, please make sure you have an empty slot on your ETC inventory.")) {
                if (sm.canAddItem(TICKET_TO_CONSTRUCTION_SITE_B3, 1) && sm.addMoney(-2000)) {
                    sm.addItem(TICKET_TO_CONSTRUCTION_SITE_B3, 1);
                    sm.sayNext("You can insert the ticket in the #p1052007#. I heard Area 3 has very rare, very precious items available but with so many traps all over the place most come back out early. Wishing you the best of luck.");
                } else {
                    sm.sayNext("Are you lacking Mesos? Check and see if you have an empty slot on your ETC inventory or not.");
                }
            } else {
                sm.sayNext("You can enter the premise once you have bought the ticket. I heard there are strange devices in there everywhere but in the end, rare precious items await you. So let me know if you ever decide to change your mind.");
            }
        }
    }

    @Script("subway_in")
    public static void subway_in(ScriptManager sm) {
        // The Ticket Gate (1052007)
        //   Victoria Road : Subway Ticketing Booth (103000100)
        //   Kerning City Subway : Subway Ticketing Booth (103020000)
        final int answer = sm.askMenu("Pick your destination.", Map.of(
                0, bold("Kerning City Subway" + red("Beware of Stirges and Wraiths!")),
                1, "Kerning Square Shopping Center (Get on the Subway)",
                2, "Enter Construction Site",
                3, "New Leaf City"
        ));
        if (answer == 0) {
            // Kerning City Subway : Along the Subway
            sm.warp(103020100, "out00");
        } else if (answer == 1) {
            // Kerning Square : Kerning Square Station
            sm.warpInstance(KERNING_SQUARE_SUBWAY_1, "sp", 103020020, 10);
        } else if (answer == 2) {
            // Enter Construction Site
            if (!sm.hasItem(TICKET_TO_CONSTRUCTION_SITE_B1) && !sm.hasItem(TICKET_TO_CONSTRUCTION_SITE_B2) && !sm.hasItem(TICKET_TO_CONSTRUCTION_SITE_B3)) {
                sm.sayOk("Here's the ticket reader. You are not allowed in without the ticket.");
                return;
            }
            final Map<Integer, String> options = new HashMap<>();
            if (sm.hasItem(TICKET_TO_CONSTRUCTION_SITE_B1)) {
                options.put(0, "Construction site B1");
            }
            if (sm.hasItem(TICKET_TO_CONSTRUCTION_SITE_B2)) {
                options.put(1, "Construction site B2");
            }
            if (sm.hasItem(TICKET_TO_CONSTRUCTION_SITE_B3)) {
                options.put(2, "Construction site B3");
            }
            final int ticketAnswer = sm.askMenu("Here's the ticket reader. You will be brought in immediately. Which ticket would you like to use?", options);
            if (ticketAnswer == 0) {
                if (sm.removeItem(TICKET_TO_CONSTRUCTION_SITE_B1, 1)) {
                    sm.warp(910360000, "sp"); // B1 : Area 1
                }
            } else if (ticketAnswer == 1) {
                if (sm.removeItem(TICKET_TO_CONSTRUCTION_SITE_B2, 1)) {
                    sm.warp(910360100, "sp"); // B2 : Area 1
                }
            } else if (ticketAnswer == 2) {
                if (sm.removeItem(TICKET_TO_CONSTRUCTION_SITE_B3, 1)) {
                    sm.warp(910360200, "sp"); // B3 : Area 1
                }
            }
        } else if (answer == 3) {
            // New Leaf City
            if (!sm.hasItem(Masteria.SUBWAY_TICKET_TO_NLC)) {
                sm.sayOk("Here's the ticket reader. You are not allowed in without the ticket.");
                return;
            }
            final EventState eventState = sm.getEventState(EventType.CM_SUBWAY);
            if (eventState == EventState.SUBWAY_BOARDING) {
                if (!sm.askYesNo("It looks like there's plenty of room for this ride. Please have your ticket ready so I can let you in. The ride will be long, but you'll get to your destination just fine. What do you think? Do you want to get on this ride?")) {
                    return;
                }
                if (sm.removeItem(Masteria.SUBWAY_TICKET_TO_NLC, 1)) {
                    sm.warp(Subway.WAITING_ROOM_FROM_KC_TO_NLC, "st00"); // Kerning City Town Street : Waiting Room (From KC to NLC)
                }
            } else if (eventState == EventState.SUBWAY_WAITING) {
                sm.sayNext("This subway is getting ready for takeoff. I'm sorry, but you'll have to get on the next ride. The ride schedule is available through the usher at the ticketing booth.");
            } else {
                sm.sayNext("We will begin boarding 5 minutes before the takeoff. Please be patient and wait for a few minutes. Be aware that the subway will take off right on time, and we stop receiving tickets 1 minute before that, so please make sure to be here on time.");
            }
        }
    }

    @Script("subway_in2")
    public static void subway_in2(ScriptManager sm) {
        // Victoria Road : Subway Ticketing Booth (103000100)
        //   in00 (200, 187)
        // Kerning City Subway : Subway Ticketing Booth (103020000)
        //   in00 (200, 187)
        subway_in(sm);
    }

    @Script("Depart_inSubway")
    public static void Depart_inSubway(ScriptManager sm) {
        // Kerning City Subway : Kerning Square Subway (103020010)
        // Kerning City Subway : Kerning Square Subway (103020011)
        // Kerning City Subway : Kerning Square Subway (103020012)
        if (sm.getFieldId() == KERNING_SQUARE_SUBWAY_1) {
            sm.scriptProgressMessage("The next stop is at Kerning Square Station. The exit is to your left.");
        } else if (sm.getFieldId() == KERNING_SQUARE_SUBWAY_2) {
            sm.scriptProgressMessage("The next stop is at Kerning Subway Station. The exit is to your left.");
        }
    }

    @Script("Depart_ToKerning")
    public static void Depart_ToKerning(ScriptManager sm) {
        // Kerning Square : Kerning Square Station (103020020)
        //   out00 (465, 26)
        sm.playPortalSE();
        sm.warpInstance(KERNING_SQUARE_SUBWAY_2, "sp", 103020000, 10); // Kerning City Subway : Subway Ticketing Booth
    }

    @Script("enter_VDS")
    public static void enter_VDS(ScriptManager sm) {
        // Sleepywood : Sleepywood (105000000)
        //   east00 (1759, 312)
        sm.playPortalSE();
        sm.warp(105010000, "west00"); // Swamp : Silent Swamp
    }

    @Script("enterAchter")
    public static void enterAchter(ScriptManager sm) {
        // Henesys : Henesys Park (100000200)
        //   in02 (3941, 693)
        sm.playPortalSE();
        sm.warp(100000201, "out02"); // Henesys : Bowman Instructional School
    }

    @Script("enterMagiclibrar")
    public static void enterMagiclibrar(ScriptManager sm) {
        // Ellinia : Ellinia (101000000)
        //   jobin00 (-250, -473)
        sm.playPortalSE();
        sm.warp(101000003, "jobout00"); // Ellinia : Magic Library
    }

    @Script("inERShip")
    public static void inERShip(ScriptManager sm) {
        // Port Road : Victoria Tree Platform (104020100)
        //   in01 (-422, -745)
        sm.playPortalSE();
        sm.warp(104020120, "out00"); // Port Road : Station to Ereve
    }

    @Script("Depart_topFloor")
    public static void Depart_topFloor(ScriptManager sm) {
        // Kerning Square : 7th Floor 8th Floor Area A
        sm.playPortalSE();
        sm.warp(103040410, "right01"); // Kerning Square : 7th Floor 8th Floor Area B
    }

    @Script("Depart_topOut")
    public static void Depart_topOut(ScriptManager sm) {
        // Kerning Square : 7th Floor 8th Floor Area A
        sm.playPortalSE();
        sm.warp(103040300, "fromUp"); // Kerning Square : 5th Floor 6th Floor Area A
    }

    @Script("halloween_enter")
    public static void halloween_enter(ScriptManager sm) {
        // Phantom Forest : Haunted House
        sm.playPortalSE();
        sm.warp(682000100);
    }

    @Script("nautil_black")
    public static void nautil_black(ScriptManager sm) {
        // Muirhat (1092007)
        //   Nautilus : Top Floor - Hallway (120000100)

        // TODO
    }

    @Script("nautil_stone")
    public static void nautil_stone(ScriptManager sm) {
        // Shiny Stone (1092016)
        //   Nautilus : Generator Room (120000301)
        if (sm.hasQuestStarted(2166)) {
            sm.sayNext("It's a beautiful, shiny rock. I can feel the mysterious power surrounding it.");
            sm.forceCompleteQuest(2166);
        } else {
            sm.sayNext("I touched the shiny rock with my hand, and I felt a mysterious power flowing into my body.");
        }
    }

    @Script("nautil_letter")
    public static void nautil_letter(ScriptManager sm) {
        // Trash Can (1092018)
        //   Nautilus : Top Floor - Hallway (120000100)
        if (sm.hasQuestCompleted(2162) || sm.hasItem(4031839)) {
            return;
        }
        if (sm.addItem(4031839, 1)) {
            sm.sayNext("(You retrieved a Crumpled Paper standing out of the trash can. It's content seems important.)");
        } else {
            sm.sayNext("(You see a Crumpled Paper standing out of the trash can. It's content seems important, but you can't retrieve it since your inventory is full.)");
        }
    }

    @Script("nautil_cow")
    public static void nautil_cow(ScriptManager sm) {
        // Tangyoon (1092000)
        //   Nautilus : Cafeteria (120000103)
        if (sm.hasQuestStarted(2180)) {
            sm.sayNext("Okay, I'll now send you to the stable where my cows are. Watch out for the calves that drink all the milk. You don't want your effort to go to waste.");
            sm.sayBoth("It won't be easy to tell at a glance between a calf and a cow. Those calves may only be a month or two old, but they have already grown to the size of their mother. They even look alike...even I get confused at times! Good luck!");
            if (sm.addItem(4031847, 1)) {
                sm.warp(912000100, "sp"); // Hidden Chamber : The Nautilus - Stable
            } else {
                sm.sayOk("I can't give you the empty bottle because your inventory is full. Please make some room in your Etc window.");
            }
        }
    }

    @Script("mom_cow")
    public static void mom_cow(ScriptManager sm) {
        // Mother Milk Cow (1092090)
        //   Hidden Chamber : The Nautilus - Stable (912000100)
        // Mother Milk Cow (1092091)
        //   Hidden Chamber : The Nautilus - Stable (912000100)
        // Mother Milk Cow (1092092)
        if (sm.getQRValue(QuestRecordType.NautilusMomCow).equals(String.valueOf(sm.getSpeakerId()))) {
            sm.sayNext("You have taken milk from this cow recently, check another cow.");
            return;
        }
        if (sm.canAddItem(4031848, 1) && sm.hasItem(4031847)) {
            sm.removeItem(4031847, 1);
            sm.addItem(4031848, 1);
            sm.sayNext("Now filling up the bottle with milk. The bottle is now 1/3 full of milk.");
            sm.setQRValue(QuestRecordType.NautilusMomCow, String.valueOf(sm.getSpeakerId()));
        } else if (sm.canAddItem(4031849, 1) && sm.hasItem(4031848)) {
            sm.removeItem(4031848, 1);
            sm.addItem(4031849, 1);
            sm.sayNext("Now filling up the bottle with milk. The bottle is now 2/3 full of milk.");
            sm.setQRValue(QuestRecordType.NautilusMomCow, String.valueOf(sm.getSpeakerId()));
        } else if (sm.canAddItem(4031850, 1) && sm.hasItem(4031849)) {
            sm.removeItem(4031849, 1);
            sm.addItem(4031850, 1);
            sm.sayNext("Now filling up the bottle with milk. The bottle is now completely full of milk.");
            sm.setQRValue(QuestRecordType.NautilusMomCow, String.valueOf(sm.getSpeakerId()));
        }
    }

    @Script("baby_cow")
    public static void baby_cow(ScriptManager sm) {
        // Baby Milk Cow (1092093)
        // Baby Milk Cow (1092094)
        //   Hidden Chamber : The Nautilus - Stable (912000100)
        // Baby Milk Cow (1092095)
        //   Hidden Chamber : The Nautilus - Stable (912000100)
        if (sm.hasItem(4031847)) {
            sm.sayNext("The hungry calf is drinking all the milk! The bottle remains empty...");
        } else if (sm.hasItem(4031848) || sm.hasItem(4031849) || sm.hasItem(4031850)) {
            sm.removeItem(4031848);
            sm.removeItem(4031849);
            sm.removeItem(4031850);
            sm.addItem(4031847, 1);
            sm.sayNext("The hungry calf is drinking all the milk! The bottle is now empty.");
        }
    }

    @Script("end_cow")
    public static void end_cow(ScriptManager sm) {
        // Hidden Chamber : The Nautilus - Stable (912000100)
        //   ntq2 (793, 148)
        if (sm.hasQuestStarted(2180) && !sm.hasItem(4031850)) {
            sm.message("Your milk jug is not full...");
        } else {
            sm.playPortalSE();
            sm.warp(120000103); // Nautilus : Cafeteria
        }
    }

    @Script("nautil_Abel1")
    public static void nautil_Abel1(ScriptManager sm) {
        // Bush (1094002)
        //   Nautilus : Nautilus Harbor (120000000)
        // Bush (1094003)
        //   Nautilus : Nautilus Harbor (120000000)
        // Bush (1094004)
        //   Nautilus : Nautilus Harbor (120000000)
        // Bush (1094005)
        //   Nautilus : Nautilus Harbor (120000000)
        // Bush (1094006)
        //   Nautilus : Nautilus Harbor (120000000)
        final List<Integer> items = List.of(
                4031853,
                4031854,
                4031855
        );
        if (sm.hasQuestStarted(2186) && !sm.hasItem(4031853)) {
            final int itemId = Util.getRandomFromCollection(items).orElseThrow();
            if (sm.addItem(itemId, 1)) {
                if (itemId == 4031853) {
                    sm.sayNext("I found Abel's glasses.");
                } else {
                    sm.sayOk("I found a pair of glasses, but it doesn't seem to be Abel's. Abel's pair is horn-rimmed...");
                }
            } else {
                sm.sayNext("I can't find any space to store Abel's glasses. I better check the Etc. tab of my inventory.");
            }
        }
    }

    @Script("q2186e")
    public static void q2186e(ScriptManager sm) {
        // Help Me Find My Glasses (2186 - end)
        sm.sayNext("What? You found my glasses? I better put it on first, to make sure that it''s really mine. Oh, it really is mine. Thank you so much!\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#v2030019# 5 #t2030019#s\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0#  1000 EXP");
        if (sm.canAddItem(2030019, 5) && sm.removeItem(4031853)) {
            sm.addItem(2030019, 5); // Nautilus Return Scroll
            sm.addExp(1000);
            sm.forceCompleteQuest(2186);
            sm.setNpcAction(1094001, "quest"); // Abel
            sm.sayNext("Yes...time for some fishing!");
        } else {
            sm.sayOk("I need you to have an USE slot available to reward you properly!");
        }
    }

    @Script("pet_letter")
    public static void pet_letter(ScriptManager sm) {
        if(sm.hasItem(4031035, 1)) {
            sm.sayNext("You have made it! Now let me see... you want to learn 'Follow the Lead' skill? Since you've made it so far, I'll teach you the skill!");
            sm.removeItem(4031035);
            sm.addSkill(8, 1, 1);
            sm.sayOk("There you go! Have fun!");
        }
    }

    @Script("pet_lifeitem")
    public static void pet_lifetime(ScriptManager sm) {
        final int answer = sm.askMenu("Do you have any business with me?", Map.of(
                0, "#bPlease tell me about this place.",
                1, "I'm here through a word from Mar the Fairy...#k"
        ));
        if(answer == 0) {
            if(sm.hasItem(4031035, 1)) {
                sm.sayNext("Get that letter, jump over obstacles with your pet, and take that letter to my brother Trainer Frod. Give him the letter and something good is going to happen to your pet.");
                return;
            }
            if(sm.askYesNo("This is the road where you can go take a walk with your pet. You can just walk around with it, or you can train your pet to go through the obstacles here. If you aren't too close with your pet yet, that may present a problem and he will not follow your command as much... So, what do you think? Wanna train your pet?")) {
                if(!sm.canAddItem(4031035, 1)) {
                    sm.sayOk("Please make room in your inventory and talk to me again.");
                    return;
                }
                sm.addItem(4031035, 1);
                sm.sayNext("Ok, here's the letter. He wouldn't know I sent you if you just went there straight, so go through the obstacles with your pet, go to the very top, and then talk to Trainer Frod to give him the letter. It won't be hard if you pay attention to your pet while going through obstacles. Good luck!");
            }
        } else if(answer == 1) {
            sm.sayOk("Hey, are you sure you've met #bMar the Fairy#k? Don't lie to me if you've never met her before because it's obvious. That wasn't even a good lie!!");
        }
    }

    @Script("hotel1")
    public static void hotel1(ScriptManager sm) {
        sm.sayNext("Welcome. We're the Sleepywood Hotel. Our hotel works hard to serve you the best at all times. If you are tired and worn out from hunting, how about a relaxing stay at our hotel?");
        final int answer = sm.askMenu("We offer two kinds of rooms for our service. Please choose the one of your liking.", Map.of(
                0, "Regular sauna (" + REGULAR_SAUNA_PRICE + " mesos per use)",
                1, "VIP sauna (" + VIP_SAUNA_PRICE + " mesos per use)"
        ));
        if(answer == 0) {
            if(sm.askYesNo("You have chosen the regular sauna. Your HP and MP will recover fast and you can even purchase some items there. Are you sure you want to go in?")) {
                if(!sm.canAddMoney(-REGULAR_SAUNA_PRICE)) {
                    sm.sayNext("I'm sorry. It looks like you don't have enough mesos. It will cost you at least " + REGULAR_SAUNA_PRICE + " mesos to stay at our hotel.");
                    return;
                }
                sm.warp(105000011);
                sm.addMoney(-REGULAR_SAUNA_PRICE);
            }
        } else if(answer == 1) {
            if(sm.askYesNo("You've chosen the VIP sauna. Your HP and MP will recover even faster than that of the regular sauna and you can even find a special item in there. Are you sure you want to go in?")) {
                if(!sm.canAddMoney(-VIP_SAUNA_PRICE)) {
                    sm.sayNext("I'm sorry. It looks like you don't have enough mesos. It will cost you at least " + VIP_SAUNA_PRICE + " mesos to stay at our hotel.");
                    return;
                }
                sm.warp(105000012);
                sm.addMoney(-VIP_SAUNA_PRICE);
            }
        }
    }

    @Script("Dual_moveGate")
    public static void dualMoveGate(ScriptManager sm) {
        sm.playPortalSE();
        sm.warp(103050000);
    }

    @Script("dual_ballRoom")
    public static void dualBallRoom(ScriptManager sm) {
        if(sm.hasQuestStarted(2363)) {
            sm.playPortalSE();
            sm.warp(910350000, "out00");
        }
    }

    @Script("dual_ball00")
    public static void dualBall00(ScriptManager sm) {
        sm.dropRewards(List.of(Reward.item(2430071, 1, 1, 1)));
        sm.setReactorState(1032001, 0);
    }

    @Script("consume_2430071")
    public static void consume_2430071(ScriptManager sm) {
        final int randomInt = sm.getRandomIntBelow(1);
        if(randomInt == 0 && !sm.hasItem(4032616, 1)) {
            sm.addItem(4032616, 1);
            sm.broadcastMessage("You've retrieved a Mirror of Insight from the shattered Opalescent Glass Marble.", true);
            sm.avatarOriented("Effect/OnUserEff.img/itemEffect/quest/2430071");
        } else {
            sm.broadcastMessage("The Opalescent Glass Marble has shattered. Nothing is inside.", true);
            sm.avatarOriented("Effect/OnUserEff.img/itemEffect/quest/2430071");
        }
        sm.removeItem(2430071, 1);
    }

    @Script("q2363e")
    public static void q2363e(ScriptManager sm) {
        // Dual Blade : Time For The Awekening - end
        if(sm.askYesNo("This is great. The Mirror of Insight has chosen you, Are you ready to awaken as a Dual Blade?")) {
            if(sm.hasItem(4032616, 1) && !sm.hasQuestCompleted(2363)) {
                sm.removeItem(4032616);
                sm.addItem(1342000, 1);
                sm.forceCompleteQuest(2363);
                sm.setJob(Job.BLADE_RECRUIT);
                sm.sayOk("From this moment, you are a #bBlade Recruit#k. Please have pride in all that you do.");
            }
        }
    }

    @Script("dual_lv20")
    public static void dual_lv20(ScriptManager sm) {
        if(sm.getLevel() >= 20) {
            sm.playPortalSE();
            sm.warp(103050310);
        } else {
            sm.broadcastMessage("You must be level 20.", true);
        }
    }

    @Script("dual_lv25")
    public static void dual_lv25(ScriptManager sm) {
        if(sm.getLevel() >= 25) {
            sm.playPortalSE();
            sm.warp(103050340);
        } else {
            sm.broadcastMessage("You must be level 25.", true);
        }
    }

    @Script("dual_lv30")
    public static void dual_lv30(ScriptManager sm) {
        if(sm.getLevel() >= 30) {
            sm.playPortalSE();
            sm.warp(103050370);
        } else {
            sm.broadcastMessage("You must be level 30.", true);
        }
    }

    @Script("dual_secret")
    public static void dualSecret(ScriptManager sm) {
        if (sm.hasQuestStarted(2369) && !sm.hasItem(4032617)) {
            sm.playPortalSE();
            sm.warpInstance(910350100, "out00", 910350100, 10 * 60);
        }
    }

    @Script("dual_Diary")
    public static void dualDiary(ScriptManager sm) {
        if(!sm.getQRValue(QuestRecordType.DualBladeDualDiary).equals("1")) {
            sm.useSummoningSack(2109012, 98, 149);
            sm.setQRValue(QuestRecordType.DualBladeDualDiary, "1");
        } else if(sm.getField().getMobPool().isEmpty()) {
            if(!sm.canAddItem(4032617, 1)) {
                sm.sayOk("Open up one slot in your Etc inventory before continuing.");
                return;
            }

            sm.addItem(4032617, 1);
            sm.sayOk("You've obtained the Former Dark Lord's Diary. You better leave before someone comes in.");
        }
    }

    @Script("q2369e")
    public static void q2369e(ScriptManager sm) {
        // Lady Syl : Time for The Awakening [2]
        sm.sayNext("Finally... I have my father's Diary. Thank you. I am starting to trust you even more. Your current position doesn't seem to suit your great abilities. I think you have the qualifications to advance to a #bBlade Acolyte#k. I will advance you to a Blade Acolyte now.");
        if (sm.hasItem(4032617, 1) && !sm.hasQuestCompleted(2369)){
            if(!sm.canAddItem(1052244, 1)) {
                sm.sayNext("Please make room in your Equip Inventory.");
                return;
            }

            sm.removeItem(4032616);
            sm.addItem(1052244, 1);
            sm.forceCompleteQuest(2369);
            sm.setJob(Job.BLADE_ACOLYTE);
            sm.addSkill(4311003, 0, 20);
            sm.sayNext("My father's diary... Father would often write in a code that only he and I could understand. Wait, in the last chapter... This!");
        } else if(sm.hasQuestCompleted(2369)) {
            sm.sayNext("My father's diary... Father would often write in a code that only he and I could understand. Wait, in the last chapter... This!");
        }
        sm.sayBoth("This can't be! It's a lie! Jin! How dare you lay a finger on my father's diary!\\r\\n\\r\\n#b(Lady Syl drops the diary and it falls to the ground.)#k");
        sm.sayBoth("#b(You pick up the book and start reading it.)\\r\\n\\r\\n- Date: XX-XX-XXXX -\\r\\nTeacher has passed away... Three days ago, teacher left for the Cursed Sanctuary at the request of Tristan. Syl seemed worried so I decided to go look for him. When I arrived at the entrance of the Sanctuary, I heard a shriek that made me shiver...");
        sm.sayBoth("#bWhen I jumped into the darkness of the sanctuary, I came face to face with a red-eyed monster spewing evil energy. Teacher was nowhere to be seen. The monster started attacking. After a fierece battle, I finally succeeded in killing it. However, the fallen monster soon turned into... teacher.");
        sm.sayBoth("#bI attempted to help teacher, but he passed in my arms. Before he passed, he whispered, My soul was trapped within the Balrog. You freed me... Now, take care of Kerning City and Syl.... and... please don't tell a soul about this. I can't forgive myself for allowing the demon to steal my soul.");
        sm.sayBoth("#bAs he wished, I will never reveal what happened. His secrets--along with his diary-\\r\\n-will forever be sealed.- Jin -");
    }

    @Script("outSecondDH")
    public static void outSecondDH(ScriptManager sm) {
        if(sm.askYesNo("Are you done with the Knighthood Exam? Should I let you out?")) {
            sm.warp(130020000);
        }
    }

    @Script("q20201e")
    public static void q20201e(ScriptManager sm) {
        // Mihile : Dawn Warrior Knighthood Exam - end
        if (sm.hasItem(4032096, 30)) {
            sm.sayNext("So you brought all the #bProof of Exam#k... Okay, I believe that you are not qualified to become an official knight.");
            if(sm.askYesNo("Are you interested in becoming an Official Knight?")) {
                if(!sm.canAddItem(1142066, 1)) {
                    sm.sayOk("Please make room in your EQP inventory.");
                    return;
                }
                sm.removeItem(4032096);
                sm.addItem(1142066, 1);
                sm.forceCompleteQuest(20201);
                sm.setJob(Job.DAWN_WARRIOR_2);
            }
        }
    }

    @Script("q2374e")
    public static void q2374e(ScriptManager sm) {
        sm.sayNext("I've been waiting for you. Do you have Arec's answer?\\r\\nPlease give me his letter.");
        sm.sayBoth("We have finally received Arec's official recognition. This is an important movement for us. It's also time that you experience a change.");
        if(sm.hasItem(4032619) && !sm.hasQuestCompleted(2374)) {
            if(!sm.canAddItem(1132021, 1)) {
                sm.sayOk("Please free at least one Equip slot before advancing to Blade Specialist.");
                return;
            }

            sm.removeItem(4032619);
            sm.addItem(1132021, 1);
            sm.forceCompleteQuest(2374);
            sm.setJob(Job.BLADE_SPECIALIST);
            sm.addSkill(4321000, 0, 20);
            sm.sayOk("Now that we have Arec's Recognition, you can make a job advancement by going to see him when you reach Lv. 70. Finally, a new future has been opened for the Dual Blades.");
        }
    }
}
