package mapleglory.provider.quest.act;

import mapleglory.packet.user.QuestPacket;
import mapleglory.provider.ItemProvider;
import mapleglory.util.Locked;
import mapleglory.world.user.User;

public final class QuestBuffAct implements QuestAct {
    private final int buffItemId;

    public QuestBuffAct(int buffItemId) {
        this.buffItemId = buffItemId;
    }

    @Override
    public boolean canAct(Locked<User> locked, int rewardIndex) {
        if (ItemProvider.getItemInfo(buffItemId).isEmpty()) {
            locked.get().write(QuestPacket.failedUnknown());
            return false;
        }
        return true;
    }

    @Override
    public boolean doAct(Locked<User> locked, int rewardIndex) {
        locked.get().setConsumeItemEffect(ItemProvider.getItemInfo(buffItemId).orElseThrow());
        return true;
    }
}
