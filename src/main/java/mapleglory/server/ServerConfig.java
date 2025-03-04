package mapleglory.server;

import mapleglory.util.Util;
import mapleglory.world.GameConstants;

public final class ServerConfig {
    // Database
    public static final String DB_URL_FORMAT = Util.getEnv("DB_URL_FORMAT", "jdbc:mysql://%s:25060/mglory");
    public static final String DB_HOST = Util.getEnv("DB_HOST", "localhost");
    public static final String DB_USER = Util.getEnv("DB_USER", "root");
    public static final String DB_PASS = Util.getEnv("DB_PASS", "");
    public static final int INIT_CONNECTION_POOL_TIMEOUT = Util.getEnv("INIT_CONNECTION_POOL_TIMEOUT", 90);
    public static final int WORLD_ID = Util.getEnv("WORLD_ID", 0);
    public static final String WORLD_NAME = Util.getEnv("WORLD_NAME", "MapleGlory");
    public static final int CHANNELS_PER_WORLD = Util.getEnv("CHANNEL_COUNT", 3);
    public static final int CENTRAL_REQUEST_TTL = 5;
    public static final int FIELD_TICK_INTERVAL = 100; // 100 ms tick
    public static final int SHUTDOWN_TIMEOUT = 30;
    public static final boolean REQUIRE_SECONDARY_PASSWORD = Util.getEnv("REQUIRE_SECONDARY_PASSWORD", true);

    public static final String WZ_DIRECTORY = Util.getEnv("WZ_DIRECTORY", "wz");
    public static final String DATA_DIRECTORY = Util.getEnv("DATA_DIRECTORY", "data");
    public static final int CHARACTER_BASE_SLOTS = Util.getEnv("CHARACTER_BASE_SLOTS", 3);
    public static final int INVENTORY_BASE_SLOTS = Util.getEnv("INVENTORY_BASE_SLOTS", 24);
    public static final int INVENTORY_CASH_SLOTS = GameConstants.INVENTORY_SLOT_MAX;
    public static final int TRUNK_BASE_SLOTS = Util.getEnv("TRUNK_BASE_SLOTS", 16);
    public static final int FRIEND_MAX_BASE = 20;
    public static final int CYGNUS_LEVEL_MAX = 120;
    public static final int ITEM_EXPIRE_INTERVAL = 60; // 180 seconds in BMS
    public static final int WORLD_SPEAKER_COOLTIME = 60;
    public static final String COMMAND_PREFIX = Util.getEnv("COMMAND_PREFIX", "@");
    public static final boolean DEBUG_MODE = Util.getEnv("DEBUG_MODE", true);
}
