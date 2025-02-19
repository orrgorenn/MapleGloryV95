package mapleglory.world.user;

import mapleglory.provider.map.Foothold;
import mapleglory.server.packet.OutPacket;
import mapleglory.util.Encodable;
import mapleglory.world.field.Field;
import mapleglory.world.field.life.Life;

public final class Dragon extends Life implements Encodable {
    private final int jobCode;

    public Dragon(int jobCode) {
        this.jobCode = jobCode;
    }

    public void setPosition(Field field, int x, int y) {
        setField(field);
        setX(x);
        setY(y);
        setFoothold(field.getFootholdBelow(x, y).map(Foothold::getSn).orElse(0));
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(getX()); // ptPos.x
        outPacket.encodeInt(getY()); // ptPos.y
        outPacket.encodeByte(getMoveAction()); // nMoveAction
        outPacket.encodeShort(getFoothold()); // ignored
        outPacket.encodeShort(jobCode); // nJobCode
    }
}
