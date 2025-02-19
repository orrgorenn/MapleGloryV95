package mapleglory.packet;

import mapleglory.server.header.OutHeader;
import mapleglory.server.packet.OutPacket;

public final class ClientPacket {
    // CClientSocket::ProcessPacket ------------------------------------------------------------------------------------

    public static OutPacket migrateCommand(byte[] channelHost, int channelPort) {
        assert channelHost.length == 4;
        final OutPacket outPacket = OutPacket.of(OutHeader.MigrateCommand);
        outPacket.encodeByte(true);
        outPacket.encodeArray(channelHost);
        outPacket.encodeShort(channelPort);
        return outPacket;
    }

    public static OutPacket aliveReq() {
        return OutPacket.of(OutHeader.AliveReq);
    }
}
