package mapleglory.server.netty;

import mapleglory.handler.ClientHandler;
import mapleglory.handler.stage.LoginHandler;
import mapleglory.server.header.InHeader;

import java.lang.reflect.Method;
import java.util.Map;

public final class LoginPacketHandler extends PacketHandler {
    private static final Map<InHeader, Method> loginPacketHandlerMap = loadHandlers(
            ClientHandler.class,
            LoginHandler.class
    );

    public LoginPacketHandler() {
        super(loginPacketHandlerMap);
    }
}
