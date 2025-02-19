package mapleglory.util;

import mapleglory.server.packet.InPacket;
import mapleglory.server.packet.OutPacket;

import java.util.Arrays;
import java.util.Set;

public final class BitFlag<T extends BitIndex> implements Encodable {
    private final int[] flags;

    public BitFlag(int size) {
        assert (size % 32 == 0);
        this.flags = new int[size / 32];
    }

    public boolean hasFlag(T bitIndex) {
        if (bitIndex.getArrayIndex() >= flags.length) {
            return false;
        }
        return (flags[bitIndex.getArrayIndex()] & bitIndex.getBitPosition()) != 0;
    }

    public void setFlag(T bitIndex) {
        if (bitIndex.getArrayIndex() >= flags.length) {
            return;
        }
        flags[bitIndex.getArrayIndex()] |= bitIndex.getBitPosition();
    }

    public boolean isEmpty() {
        for (int i : flags) {
            if (i != 0) {
                return false;
            }
        }
        return true;
    }

    public void clear() {
        Arrays.fill(flags, 0);
    }

    @Override
    public void encode(OutPacket outPacket) {
        for (int i = flags.length - 1; i >= 0; i--) {
            outPacket.encodeInt(flags[i]);
        }
    }

    public static <T extends BitIndex> BitFlag<T> decode(InPacket inPacket, int size) {
        final BitFlag<T> bitFlag = new BitFlag<>(size);
        for (int i = bitFlag.flags.length - 1; i >= 0; i--) {
            bitFlag.flags[i] = inPacket.decodeInt();
        }
        return bitFlag;
    }

    public static <T extends BitIndex> BitFlag<T> from(Set<T> flagSet, int size) {
        final BitFlag<T> bitFlag = new BitFlag<>(size);
        for (T flag : flagSet) {
            bitFlag.setFlag(flag);
        }
        return bitFlag;
    }
}
