package mapleglory.handler.field;

import mapleglory.handler.Handler;
import mapleglory.packet.field.NpcPacket;
import mapleglory.server.header.InHeader;
import mapleglory.server.packet.InPacket;
import mapleglory.world.field.Field;
import mapleglory.world.field.life.MovePath;
import mapleglory.world.field.npc.Npc;
import mapleglory.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class NpcHandler {
    private static final Logger log = LogManager.getLogger(NpcHandler.class);

    @Handler(InHeader.NpcMove)
    public static void handleNpcMove(User user, InPacket inPacket) {
        final int objectId = inPacket.decodeInt(); // dwNpcId
        final byte oneTimeAction = inPacket.decodeByte(); // nOneTimeAction
        final byte chatIndex = inPacket.decodeByte(); // nChatIdx

        final Field field = user.getField();
        final Optional<Npc> npcResult = field.getNpcPool().getById(objectId);
        if (npcResult.isEmpty()) {
            log.error("Received NpcMove for invalid object with ID : {}", objectId);
            return;
        }
        final Npc npc = npcResult.get();

        final MovePath movePath = npc.isMove() ? MovePath.decode(inPacket) : null;
        if (movePath != null) {
            movePath.applyTo(npc);
        }
        field.broadcastPacket(NpcPacket.npcMove(npc, oneTimeAction, chatIndex, movePath));
    }
}
