package mapleglory.util;

import mapleglory.server.packet.OutPacket;

public interface Encodable {
    void encode(OutPacket outPacket);
}
