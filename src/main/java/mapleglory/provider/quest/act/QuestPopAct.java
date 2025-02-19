package mapleglory.provider.quest.act;

import mapleglory.packet.world.MessagePacket;
import mapleglory.util.Locked;
import mapleglory.world.user.User;

public final class QuestPopAct implements QuestAct {
    private final int pop;

    public QuestPopAct(int pop) {
        this.pop = pop;
    }

    @Override
    public boolean canAct(Locked<User> locked, int rewardIndex) {
        return true;
    }

    @Override
    public boolean doAct(Locked<User> locked, int rewardIndex) {
        final User user = locked.get();
        user.addPop(pop);
        user.write(MessagePacket.incPop(pop));
        return true;
    }
}
