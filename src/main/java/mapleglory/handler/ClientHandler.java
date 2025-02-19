package mapleglory.handler;

import mapleglory.server.header.InHeader;
import mapleglory.server.header.OutHeader;
import mapleglory.server.node.Client;
import mapleglory.server.packet.InPacket;
import mapleglory.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ClientHandler {
    private static final Logger log = LogManager.getLogger(ClientHandler.class);

    @Handler(InHeader.AliveAck)
    public static void handleAliveAck(Client c, InPacket inPacket) {
    }

    @Handler(InHeader.ExceptionLog)
    public static void handleExceptionLog(Client c, InPacket inPacket) {
        final String data = inPacket.decodeString();
        log.error("Exception log : {}", data);
    }

    @Handler(InHeader.ClientDumpLog)
    public static void handleClientDumpLog(Client c, InPacket inPacket) {
        final short callType = inPacket.decodeShort();
        final int errorType = inPacket.decodeInt();
        final int bufferSize = inPacket.decodeShort();
        inPacket.decodeInt(); // unk

        final short op = inPacket.decodeShort();
        log.error("Error {} | {}({}) | {}", errorType, OutHeader.getByValue(op), Util.opToString(op), inPacket);
    }
}
