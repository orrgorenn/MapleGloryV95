package mapleglory.script.continent;

import mapleglory.script.common.Script;
import mapleglory.script.common.ScriptHandler;
import mapleglory.script.common.ScriptManager;
import mapleglory.util.Tuple;

import java.util.List;
import java.util.Map;

public class ElNath extends ScriptHandler {
    @Script("refine_elnath")
    public static void refineElNath(ScriptManager sm) {
        if (sm.askYesNo("Looks like you have quite a bit of ores and jewels with you. For a small service fee, I can refine them into the materials needed to create shields or weapons. I've been doing this for 50 years, so it's a piece of cake! What do you think? You want me to do it?")) {
            final int answer1 = sm.askMenu("Good decision! Give me the ores and the service fee, and I can refine them so that they'll be of some use. Before doing so, don't forget to check your etc. inventory to make sure that you have enough free space for the new items. Let's see, what would you like me to do?", Map.of(
                    0, "Refine the ore of a mineral",
                    1, "Refine the ore of a jewel",
                    2, "Refine a rare gem",
                    3, "Refine a crystal",
                    4, "Create materials",
                    5, "Create arrows"
            ));

            switch (answer1) {
                case 0:
                    final int answer2 = sm.askMenu("Which of these minerals would you like to make?", Map.of(
                            0, "#t4011000#",
                            1, "#t4011001#",
                            2, "#t4011002#",
                            3, "#t4011003#",
                            4, "#t4011004#",
                            5, "#t4011005#",
                            6, "#t4011006#"
                    ));

                    switch (answer2) {
                        case 0:
                            handleRefine(sm, 1, "#t4011000#", "#v4010000#", "#t4010000#s", 300);
                            break;
                        case 1:
                            handleRefine(sm, 2, "#t4011001#", "#v4010001#", "#t4010001#s", 300);
                            break;
                        case 2:
                            handleRefine(sm, 3, "#t4011002#", "#v4010002#", "#t4010002#s", 300);
                            break;
                        case 3:
                            handleRefine(sm, 4, "#t4011003#", "#v4010003#", "#t4010003#s", 500);
                            break;
                        case 4:
                            handleRefine(sm, 5, "#t4011004#", "#v4010004#", "#t4010004#s", 500);
                            break;
                        case 5:
                            handleRefine(sm, 6, "#t4011005#", "#v4010005#", "#t4010005#s", 500);
                            break;
                        case 6:
                            handleRefine(sm, 7, "#t4011006#", "#v4010006#", "#t4010006#s", 800);
                            break;
                    }

                    break;
            }
        } else {
            sm.sayOk("I understand. Is the service fee too high for you? But understand that I'll be in this town for a long time, so if you ever want to refine anything just bring it to me.");
        }
    }

    public static void handleRefine(ScriptManager sm, int index, String makeItem, String needItemIcon, String needItemString, int unitPrice) {
        if (index == 200 || index == 201) {
            final int numOfItems = sm.askNumber("Very good, very good ... how many #b" + makeItem + "s#k would you like to make?", 1, 1, 100);
            final int nPrice = unitPrice * numOfItems;
            if(!sm.askYesNo("Alright, you wanna create #b" + numOfItems + " " + makeItem + "#k(s)?? For that you will need #r" + nPrice + "mesos and " + needItemIcon + " " + numOfItems + " " + needItemString + "#k each. What do you think? Do you really want to do it?")) {
                sm.sayOk("I understand. Is the service fee too high for you? But understand that I'll be in this town for a long time, so if you ever want to refine anything just bring it to me.");
                return;
            }

            // a rare jewel
            if (index == 200) {
                List<Tuple<Integer, Integer>> removeItems = List.of(
                        Tuple.of(4011000, numOfItems),
                        Tuple.of(4011001, numOfItems),
                        Tuple.of(4011002, numOfItems),
                        Tuple.of(4011003, numOfItems),
                        Tuple.of(4011004, numOfItems),
                        Tuple.of(4011005, numOfItems),
                        Tuple.of(4011006, numOfItems)
                );
                if(!handleTransaction(sm, numOfItems, nPrice, removeItems, 4011007)) return;
            } else {
                List<Tuple<Integer, Integer>> removeItems = List.of(
                        Tuple.of(4021000, numOfItems),
                        Tuple.of(4021001, numOfItems),
                        Tuple.of(4021002, numOfItems),
                        Tuple.of(4021003, numOfItems),
                        Tuple.of(4021004, numOfItems),
                        Tuple.of(4021005, numOfItems),
                        Tuple.of(4021006, numOfItems),
                        Tuple.of(4021007, numOfItems),
                        Tuple.of(4021008, numOfItems)
                );
                if(!handleTransaction(sm, numOfItems, nPrice, removeItems, 4021009)) return;
            }

            sm.sayOk("Here! Take #b" + numOfItems + " " + makeItem + "#k(s). It's been 50 years, but I still have my skills. If you need my help in the near future, feel free to drop by.\"");
        } else {
            final int numOfItems = sm.askNumber("To make a " + makeItem + ", I will need the following materials. How many would you like to make?\r\n\r\n#b" + needItemIcon + " 10 " + needItemString + "\r\n" + unitPrice + " mesos#k", 1, 1, 100);
            final int nPrice = unitPrice * numOfItems;
            final int nAllNum = numOfItems * 10;
            if (!sm.askYesNo("You want to make #b" + numOfItems + " " + makeItem + "(s)#k?? Then you will need #r" + nPrice + " mesos and " + needItemIcon + " " + nAllNum + " " + needItemString + "#k(s). What do you think? You wanna do it?")) {
                sm.sayOk("I understand... Is the service fee too high for you? Know that I will be in this town for a long time, so if you ever want to refine anything just bring it to me.");
                return;
            }

            // mineral
            if (index >= 1 && index <= 7) {
                List<Tuple<Integer, Integer>> removeItems = List.of(
                        Tuple.of(4010000 + (index - 1), nAllNum)
                );
                if(!handleTransaction(sm, numOfItems, nPrice, removeItems, 4011000 + (index - 1))) return;
            }

            // jewel
            if (index >= 100 && index <= 108) {
                List<Tuple<Integer, Integer>> removeItems = List.of(
                        Tuple.of(4020000 + (index - 1), nAllNum)
                );
                if(!handleTransaction(sm, numOfItems, nPrice, removeItems, 4021000 + (index - 1))) return;
            }

            // crystal
            if (index >= 300 && index <= 304) {
                List<Tuple<Integer, Integer>> removeItems = List.of(
                        Tuple.of(4004000 + (index - 1), nAllNum)
                );
                if(!handleTransaction(sm, numOfItems, nPrice, removeItems, 4005000 + (index - 1))) return;
            }

            sm.sayOk("Here! Take #b" + numOfItems + " " + makeItem + "(s)#k. It's been 50 years, but I still have my skills. If you need my help in the near future, feel free to drop by.");
        }
    }

    private static boolean handleTransaction(ScriptManager sm, int numOfItems, int mesos, List<Tuple<Integer, Integer>> removeItems, int itemToGive) {
        if(!sm.hasItems(removeItems) || !sm.canAddMoney(-mesos) || !sm.canAddItem(itemToGive, numOfItems)) {
            sm.sayOk("Hmm... Please make sure you have all the necessary materials, and that you have some free space in your etc. inventory...");
            return false;
        }

        sm.addMoney(-mesos);
        for (var item : removeItems) {
            sm.removeItem(item.getLeft(), item.getRight());
        }
        sm.addItem(itemToGive, numOfItems);
        return true;
    }
}
