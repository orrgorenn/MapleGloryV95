package mapleglory.packet.user;

import mapleglory.server.header.OutHeader;
import mapleglory.server.packet.OutPacket;
import mapleglory.world.field.life.MovePath;
import mapleglory.world.user.Dragon;
import mapleglory.world.user.User;

public final class DragonPacket {
    // CUser::OnDragonPacket -------------------------------------------------------------------------------------------

    public static OutPacket dragonEnterField(User user, Dragon dragon) {
        final OutPacket outPacket = OutPacket.of(OutHeader.DragonEnterField);
        outPacket.encodeInt(user.getCharacterId());
        dragon.encode(outPacket);
        return outPacket;
    }

    public static OutPacket dragonMove(User user, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.DragonMove);
        outPacket.encodeInt(user.getCharacterId());
        movePath.encode(outPacket);
        return outPacket;
    }
}
