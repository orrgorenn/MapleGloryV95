package mapleglory.database;

import mapleglory.server.memo.Memo;
import mapleglory.server.memo.MemoType;

import java.time.Instant;
import java.util.List;

public interface MemoAccessor {
    List<Memo> getMemosByCharacterId(int characterId);

    boolean hasMemo(int characterId);

    boolean newMemo(Memo memo, int receiverId);

    boolean deleteMemo(int memoId, int receiverId);
}
