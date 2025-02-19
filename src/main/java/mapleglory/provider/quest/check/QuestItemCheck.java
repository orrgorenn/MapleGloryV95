package mapleglory.provider.quest.check;

import mapleglory.provider.quest.QuestItemData;
import mapleglory.provider.wz.property.WzListProperty;
import mapleglory.util.Locked;
import mapleglory.world.item.Inventory;
import mapleglory.world.item.InventoryManager;
import mapleglory.world.item.Item;
import mapleglory.world.user.User;

import java.util.Collections;
import java.util.List;

public final class QuestItemCheck implements QuestCheck {
    private final List<QuestItemData> items;

    public QuestItemCheck(List<QuestItemData> items) {
        this.items = items;
    }

    public List<QuestItemData> getItems() {
        return items;
    }

    @Override
    public boolean check(Locked<User> locked) {
        final User user = locked.get();
        final InventoryManager im = user.getInventoryManager();
        final List<QuestItemData> filteredItems = getFilteredItems(user.getGender(), user.getJob());
        for (QuestItemData itemData : filteredItems) {
            final int itemCount = im.getItemCount(itemData.getItemId()) + getEquippedItemCount(im.getEquipped(), itemData.getItemId());
            // Should have item
            if (itemData.getCount() > 0 && itemCount < itemData.getCount()) {
                return false;
            }
            // Should not have item
            if (itemData.getCount() <= 0 && itemCount > 0) {
                return false;
            }
        }
        return true;
    }

    private List<QuestItemData> getFilteredItems(int gender, int job) {
        return items.stream()
                .filter(itemData -> itemData.checkGender(gender) && itemData.checkJob(job))
                .toList();
    }

    private int getEquippedItemCount(Inventory equipped, int itemId) {
        return equipped.getItems().values().stream()
                .filter(item -> item.getItemId() == itemId)
                .mapToInt(Item::getQuantity)
                .sum();
    }

    public static QuestItemCheck from(WzListProperty itemList) {
        final List<QuestItemData> items = QuestItemData.resolveItemData(itemList);
        return new QuestItemCheck(
                Collections.unmodifiableList(items)
        );
    }
}
