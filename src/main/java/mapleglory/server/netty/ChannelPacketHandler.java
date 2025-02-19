package mapleglory.server.netty;

import mapleglory.handler.ClientHandler;
import mapleglory.handler.field.FieldHandler;
import mapleglory.handler.field.MobHandler;
import mapleglory.handler.field.NpcHandler;
import mapleglory.handler.stage.CashShopHandler;
import mapleglory.handler.stage.MigrationHandler;
import mapleglory.handler.user.*;
import mapleglory.handler.user.item.CashItemHandler;
import mapleglory.handler.user.item.ItemHandler;
import mapleglory.handler.user.item.UpgradeItemHandler;
import mapleglory.server.header.InHeader;

import java.lang.reflect.Method;
import java.util.Map;

public final class ChannelPacketHandler extends PacketHandler {
    private static final Map<InHeader, Method> channelPacketHandlerMap = loadHandlers(
            ClientHandler.class,
            CashShopHandler.class,
            MigrationHandler.class,
            // Field
            FieldHandler.class,
            MobHandler.class,
            NpcHandler.class,
            // User
            UserHandler.class,
            PartyHandler.class,
            GuildHandler.class,
            FriendHandler.class,
            PetHandler.class,
            SummonedHandler.class,
            AttackHandler.class,
            SkillHandler.class,
            HitHandler.class,
            ItemHandler.class,
            CashItemHandler.class,
            UpgradeItemHandler.class
    );

    public ChannelPacketHandler() {
        super(channelPacketHandlerMap);
    }
}
