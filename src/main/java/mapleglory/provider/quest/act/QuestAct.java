package mapleglory.provider.quest.act;

import mapleglory.util.Locked;
import mapleglory.world.user.User;

public interface QuestAct {
    boolean canAct(Locked<User> locked, int rewardIndex);

    boolean doAct(Locked<User> locked, int rewardIndex);
}
