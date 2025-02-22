package mapleglory.database;

import mapleglory.database.table.*;
import mapleglory.database.mysql.*;

public final class DatabaseManager {
    private static IdAccessor idAccessor;
    private static AccountAccessor accountAccessor;
    private static CharacterAccessor characterAccessor;
    private static FriendAccessor friendAccessor;
    private static GuildAccessor guildAccessor;
    private static GiftAccessor giftAccessor;
    private static MemoAccessor memoAccessor;

    public static IdAccessor idAccessor() { return idAccessor; }

    public static AccountAccessor accountAccessor() {
        return accountAccessor;
    }

    public static CharacterAccessor characterAccessor() {
        return characterAccessor;
    }

    public static FriendAccessor friendAccessor() {
        return friendAccessor;
    }

    public static GuildAccessor guildAccessor() {
        return guildAccessor;
    }

    public static GiftAccessor giftAccessor() {
        return giftAccessor;
    }

    public static MemoAccessor memoAccessor() {
        return memoAccessor;
    }

    public static void initialize() {
        // Create Tables
        IdTable.createTable();
        AccountTable.createTable();
        CharacterTable.createTable();
        FriendTable.createTable();
        GuildTable.createTable();
        GiftTable.createTable();
        MemoTable.createTable();

        // Create Accessors
        idAccessor = new MysqlIdAccessor();
        accountAccessor = new MysqlAccountAccessor();
        characterAccessor = new MysqlCharacterAccessor();
        friendAccessor = new MysqlFriendAccessor();
        guildAccessor = new MysqlGuildAccessor();
        giftAccessor = new MysqlGiftAccessor();
        memoAccessor = new MysqlMemoAccessor();
    }

    public static void shutdown() {}
}
