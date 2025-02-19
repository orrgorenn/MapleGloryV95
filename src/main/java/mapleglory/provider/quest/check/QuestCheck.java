package mapleglory.provider.quest.check;

import mapleglory.util.Locked;
import mapleglory.world.user.User;

public interface QuestCheck {
    boolean check(Locked<User> locked);
}
