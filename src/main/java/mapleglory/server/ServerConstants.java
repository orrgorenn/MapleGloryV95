package mapleglory.server;

import mapleglory.util.Util;

public final class ServerConstants {
    public static final int GAME_VERSION = 95;
    public static final String PATCH = "1";
    public static final byte LOCALE = 8;

    public static final byte[] CENTRAL_HOST = Util.getHost(Util.getEnv("IP_ADDRESS", "176.228.66.214"));
    public static final int CENTRAL_PORT = Util.getEnv("CENTRAL_PORT", 8282);

    public static final byte[] SERVER_HOST = Util.getHost(Util.getEnv("IP_ADDRESS", "176.228.66.214"));
    public static final int LOGIN_PORT = 8484;
    public static final int CHANNEL_PORT = 8585;
}

