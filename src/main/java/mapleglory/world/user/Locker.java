package mapleglory.world.user;

import mapleglory.server.cashshop.CashItemInfo;
import mapleglory.world.GameConstants;

import java.util.ArrayList;
import java.util.List;

public final class Locker {
    private final List<CashItemInfo> cashItems = new ArrayList<>();

    public List<CashItemInfo> getCashItems() {
        return cashItems;
    }

    public void addCashItem(CashItemInfo cashItemInfo) {
        cashItems.add(cashItemInfo);
    }

    public int getRemaining() {
        return Math.max(GameConstants.LOCKER_SLOT_MAX - cashItems.size(), 0);
    }
}
