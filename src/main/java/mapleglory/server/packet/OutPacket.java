package mapleglory.server.packet;

import mapleglory.server.header.CentralHeader;
import mapleglory.server.header.OutHeader;
import mapleglory.util.FileTime;

import java.time.Instant;

public interface OutPacket {
    void encodeByte(byte value);

    default void encodeByte(boolean value) {
        encodeByte(value ? 1 : 0);
    }

    default void encodeByte(short value) {
        encodeByte((byte) value);
    }

    default void encodeByte(int value) {
        encodeByte((byte) value);
    }

    void encodeShort(short value);

    default void encodeShort(int value) {
        encodeShort((short) value);
    }

    void encodeInt(int value);

    default void encodeInt(long value) {
        encodeInt((int) value);
    }

    default void encodeInt(boolean value) {
        encodeInt(value ? 1 : 0);
    }

    default void encodePositionInt(int x, int y) {
        encodeInt(x);
        encodeInt(y);
    }

    void encodeLong(long value);

    void encodeDouble(double value);

    default void encodeFT(FileTime ft) {
        encodeInt(ft.getLowDateTime());
        encodeInt(ft.getHighDateTime());
    }

    default void encodeFT(Instant timestamp) {
        encodeFT(timestamp != null ? FileTime.from(timestamp) : FileTime.DEFAULT_TIME);
    }

    void encodeArray(byte[] value);

    void encodeString(String value, int length);

    void encodeString(String value);

    int getSize();

    OutHeader getHeader();

    byte[] getData();

    static OutPacket of() {
        return new NioBufferOutPacket();
    }

    static OutPacket of(byte[] data) {
        final OutPacket outPacket = OutPacket.of();
        outPacket.encodeArray(data);
        return outPacket;
    }

    static OutPacket of(short op) {
        final OutPacket outPacket = OutPacket.of();
        outPacket.encodeShort(op);
        return outPacket;
    }

    static OutPacket of(OutHeader op) {
        final OutPacket outPacket = OutPacket.of();
        outPacket.encodeShort(op.getValue());
        return outPacket;
    }

    static OutPacket of(CentralHeader op) {
        final OutPacket outPacket = OutPacket.of();
        outPacket.encodeShort(op.getValue());
        return outPacket;
    }
}
