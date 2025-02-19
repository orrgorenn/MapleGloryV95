package mapleglory.server.migration;

import mapleglory.server.packet.InPacket;
import mapleglory.server.packet.OutPacket;
import mapleglory.util.Encodable;

public final class TransferInfo implements Encodable {
    private final byte[] channelHost;
    private final int channelPort;

    public TransferInfo(byte[] channelHost, int channelPort) {
        assert channelHost.length == 4;
        this.channelHost = channelHost;
        this.channelPort = channelPort;
    }

    public byte[] getChannelHost() {
        return channelHost;
    }

    public int getChannelPort() {
        return channelPort;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeArray(channelHost);
        outPacket.encodeInt(channelPort);
    }

    public static TransferInfo decode(InPacket inPacket) {
        final byte[] channelHost = inPacket.decodeArray(4);
        final int channelPort = inPacket.decodeInt();
        return new TransferInfo(channelHost, channelPort);
    }
}
