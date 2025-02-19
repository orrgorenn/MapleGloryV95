package mapleglory.provider.quest.check;

import mapleglory.util.Locked;
import mapleglory.world.user.User;

public final class QuestLevelCheck implements QuestCheck {
    private final int level;
    private final boolean isMinimum;

    public QuestLevelCheck(int level, boolean isMinimum) {
        this.level = level;
        this.isMinimum = isMinimum;
    }

    @Override
    public boolean check(Locked<User> locked) {
        final User user = locked.get();
        if (isMinimum) {
            return user.getLevel() >= level;
        } else {
            return user.getLevel() <= level;
        }
    }
}
