package mapleglory.world.field;

import mapleglory.packet.user.SummonedPacket;
import mapleglory.world.field.summoned.Summoned;
import mapleglory.world.field.summoned.SummonedEnterType;
import mapleglory.world.job.resistance.Mechanic;
import mapleglory.world.user.User;

public final class SummonedPool extends FieldObjectPool<Summoned> {
    public SummonedPool(Field field) {
        super(field);
    }

    public void addSummoned(User user, Summoned summoned) {
        summoned.setField(field);
        summoned.setId(field.getNewObjectId());
        addObject(summoned);
        field.broadcastPacket(SummonedPacket.summonedEnterField(user, summoned));
        summoned.setEnterType(SummonedEnterType.DEFAULT);
    }

    public boolean removeSummoned(User user, Summoned summoned) {
        if (!removeObject(summoned)) {
            return false;
        }
        if (summoned.getSkillId() == Mechanic.ACCELERATION_BOT_EX_7) {
            Mechanic.handleRemoveAccelerationBot(summoned);
        }
        field.broadcastPacket(SummonedPacket.summonedLeaveField(user, summoned));
        return true;
    }
}
