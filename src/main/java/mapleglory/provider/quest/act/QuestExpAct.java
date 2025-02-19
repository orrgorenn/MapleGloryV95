package mapleglory.provider.quest.act;

import mapleglory.packet.world.MessagePacket;
import mapleglory.util.Locked;
import mapleglory.world.user.User;

public final class QuestExpAct implements QuestAct {
    private final int exp;

    public QuestExpAct(int exp) {
        this.exp = exp;
    }

    @Override
    public boolean canAct(Locked<User> locked, int rewardIndex) {
        return true;
    }

    @Override
    public boolean doAct(Locked<User> locked, int rewardIndex) {
        final User user = locked.get();
        user.addExp(exp);
        user.write(MessagePacket.incExp(exp, 0, true, true));
        return true;
    }
}
