package mapleglory.packet.world;

import mapleglory.server.header.OutHeader;
import mapleglory.server.memo.Memo;
import mapleglory.server.memo.MemoResultType;
import mapleglory.server.packet.OutPacket;

import java.util.List;

public final class MemoPacket {
    // CWvsContext::OnMemoResult ---------------------------------------------------------------------------------------

    public static OutPacket load(List<Memo> memos) {
        final OutPacket outPacket = MemoPacket.of(MemoResultType.Load);
        // lReceivedMemo
        outPacket.encodeByte(memos.size());
        for (Memo memo : memos) {
            memo.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket sendSucceed() {
        return MemoPacket.of(MemoResultType.Send_Succeed);
    }

    public static OutPacket sendWarningOnline() {
        return MemoPacket.sendWarning(0);
    }

    public static OutPacket sendWarningName() {
        return MemoPacket.sendWarning(1);
    }

    public static OutPacket sendWarningFull() {
        return MemoPacket.sendWarning(2);
    }

    public static OutPacket sendWarning(int warningType) {
        final OutPacket outPacket = MemoPacket.of(MemoResultType.Send_Succeed);
        outPacket.encodeByte(warningType);
        // 0 : The other character is online now.\r\nPlease use the whisper function%2C
        // 1 : Please check the name of the receiving character.
        // 2 : The receiver's inbox is full.\r\nPlease try again.
        return outPacket;
    }

    public static OutPacket receive() {
        return MemoPacket.of(MemoResultType.Receive);
    }

    private static OutPacket of(MemoResultType resultType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MemoResult);
        outPacket.encodeByte(resultType.getValue());
        return outPacket;
    }
}
