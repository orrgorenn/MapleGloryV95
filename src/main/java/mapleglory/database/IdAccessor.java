package mapleglory.database;

import java.util.Optional;

public interface IdAccessor {
    Optional<Integer> nextAccountId();

    Optional<Integer> nextCharacterId();

    Optional<Integer> nextPartyId();

    Optional<Integer> nextGuildId();

    Optional<Integer> nextMemoId();
}
