package mapleglory.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mapleglory.packet.CentralPacket;
import mapleglory.packet.field.FieldPacket;
import mapleglory.packet.user.UserRemote;
import mapleglory.server.ServerConstants;
import mapleglory.server.header.CentralHeader;
import mapleglory.server.migration.MigrationInfo;
import mapleglory.server.migration.TransferInfo;
import mapleglory.server.node.ChannelServerNode;
import mapleglory.server.node.ServerExecutor;
import mapleglory.server.packet.InPacket;
import mapleglory.server.packet.OutPacket;
import mapleglory.server.user.RemoteUser;
import mapleglory.util.Util;
import mapleglory.world.job.resistance.BattleMage;
import mapleglory.world.user.GuildInfo;
import mapleglory.world.user.PartyInfo;
import mapleglory.world.user.User;
import mapleglory.world.user.stat.CharacterTemporaryStat;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public final class ChannelServerHandler extends SimpleChannelInboundHandler<InPacket> {
    private static final Logger log = LogManager.getLogger(ChannelServerHandler.class);
    private final ChannelServerNode channelServerNode;

    public ChannelServerHandler(ChannelServerNode channelServerNode) {
        this.channelServerNode = channelServerNode;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        final int op = inPacket.decodeShort();
        final CentralHeader header = CentralHeader.getByValue(op);
        log.log(Level.TRACE, "[ChannelServerNode] | {}({}) {}", header, Util.opToString(op), inPacket);
        ServerExecutor.submitService(() -> {
            switch (header) {
                case InitializeRequest -> {
                    ctx.channel().writeAndFlush(CentralPacket.initializeResult(channelServerNode.getChannelId(), ServerConstants.SERVER_HOST, channelServerNode.getChannelPort()));
                }
                case ShutdownRequest -> {
                    try {
                        channelServerNode.shutdown();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                case MigrateResult -> handleMigrateResult(inPacket);
                case TransferResult -> handleTransferResult(inPacket);
                case UserPacketReceive -> handleUserPacketReceive(inPacket);
                case UserPacketBroadcast -> handleUserPacketBroadcast(inPacket);
                case UserQueryResult -> handleUserQueryResult(inPacket);
                case WorldSpeakerRequest -> handleWorldSpeakerRequest(inPacket);
                case ServerPacketBroadcast -> handleServerPacketBroadcast(inPacket);
                case MessengerResult -> handleMessengerResult(inPacket);
                case PartyResult -> handlePartyResult(inPacket);
                case GuildResult -> handleGuildResult(inPacket);
                case null -> {
                    log.error("Central client {} received an unknown opcode : {}", channelServerNode.getChannelId() + 1, op);
                }
                default -> {
                    log.error("Central client {} received an unhandled header : {}", channelServerNode.getChannelId() + 1, header);
                }
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (channelServerNode.isShutdown()) {
            return;
        }
        log.error("Central client {} lost connection to central server", channelServerNode.getChannelId() + 1);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught while handling packet", cause);
        cause.printStackTrace();
    }


    // HANDLER METHODS -------------------------------------------------------------------------------------------------

    private void handleMigrateResult(InPacket inPacket) {
        final int requestId = inPacket.decodeInt();
        final boolean success = inPacket.decodeBoolean();
        final MigrationInfo migrationResult = success ? MigrationInfo.decode(inPacket) : null;
        channelServerNode.completeMigrationRequest(requestId, migrationResult);
    }

    private void handleTransferResult(InPacket inPacket) {
        final int requestId = inPacket.decodeInt();
        final boolean success = inPacket.decodeBoolean();
        final TransferInfo transferResult = success ? TransferInfo.decode(inPacket) : null;
        channelServerNode.completeTransferRequest(requestId, transferResult);
    }

    private void handleUserPacketReceive(InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final int packetLength = inPacket.decodeInt();
        final byte[] packetData = inPacket.decodeArray(packetLength);
        // Resolve target user
        final Optional<User> targetUserResult = channelServerNode.getUserByCharacterId(characterId);
        if (targetUserResult.isEmpty()) {
            log.error("Could not resolve target user for UserPacketReceive");
            return;
        }
        // Write to target client
        try (var locked = targetUserResult.get().acquire()) {
            locked.get().write(OutPacket.of(packetData));
        }
    }

    private void handleUserPacketBroadcast(InPacket inPacket) {
        final int size = inPacket.decodeInt();
        final Set<Integer> characterIds = new HashSet<>();
        for (int i = 0; i < size; i++) {
            characterIds.add(inPacket.decodeInt());
        }
        final int packetLength = inPacket.decodeInt();
        final byte[] packetData = inPacket.decodeArray(packetLength);
        final OutPacket outPacket = OutPacket.of(packetData);
        for (int characterId : characterIds) {
            final Optional<User> targetUserResult = channelServerNode.getUserByCharacterId(characterId);
            if (targetUserResult.isEmpty()) {
                continue;
            }
            targetUserResult.get().write(outPacket);
        }
    }

    private void handleUserQueryResult(InPacket inPacket) {
        final int requestId = inPacket.decodeInt();
        final int size = inPacket.decodeInt();
        final List<RemoteUser> remoteUsers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            remoteUsers.add(RemoteUser.decode(inPacket));
        }
        channelServerNode.completeUserQueryRequest(requestId, remoteUsers);
    }

    private void handleWorldSpeakerRequest(InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final boolean avatar = inPacket.decodeBoolean();
        final int packetLength = inPacket.decodeInt();
        final byte[] packetData = inPacket.decodeArray(packetLength);
        final OutPacket outPacket = OutPacket.of(packetData);
        channelServerNode.completeWorldSpeakerRequest(characterId, avatar, outPacket);
    }

    private void handleServerPacketBroadcast(InPacket inPacket) {
        final int packetLength = inPacket.decodeInt();
        final byte[] packetData = inPacket.decodeArray(packetLength);
        final OutPacket outPacket = OutPacket.of(packetData);
        channelServerNode.submitChannelPacketBroadcast(outPacket);
    }

    private void handleMessengerResult(InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final int messengerId = inPacket.decodeInt();
        // Resolve target user
        final Optional<User> targetUserResult = channelServerNode.getUserByCharacterId(characterId);
        if (targetUserResult.isEmpty()) {
            log.error("Could not resolve target user for MessengerResult");
            return;
        }
        try (var locked = targetUserResult.get().acquire()) {
            final User user = locked.get();
            user.setMessengerId(messengerId);
        }
    }

    private void handlePartyResult(InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final boolean hasParty = inPacket.decodeBoolean();
        final PartyInfo partyInfo = hasParty ? PartyInfo.decode(inPacket) : null;
        // Resolve target user
        final Optional<User> targetUserResult = channelServerNode.getUserByCharacterId(characterId);
        if (targetUserResult.isEmpty()) {
            log.error("Could not resolve target user for PartyResult");
            return;
        }
        try (var locked = targetUserResult.get().acquire()) {
            final User user = locked.get();
            log.debug("locked user: {}", user.getCharacterName());
            // Cancel party aura
            user.resetTemporaryStat(CharacterTemporaryStat.AURA_STAT);
            if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.Aura)) {
                BattleMage.cancelPartyAura(user, user.getSecondaryStat().getOption(CharacterTemporaryStat.Aura).rOption);
            }
            // Set party info and update members
            user.setPartyInfo(partyInfo);
            user.getField().getUserPool().forEachPartyMember(user, (member) -> {
                try (var lockedMember = member.acquire()) {
                    user.write(UserRemote.receiveHp(lockedMember.get()));
                    lockedMember.get().write(UserRemote.receiveHp(user));
                }
            });
            if (user.getTownPortal() != null && user.getTownPortal().getTownField() == user.getField()) {
                user.write(FieldPacket.townPortalRemoved(user, false));
            }
        }
    }

    private void handleGuildResult(InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final boolean hasGuild = inPacket.decodeBoolean();
        final GuildInfo guildInfo = hasGuild ? GuildInfo.decode(inPacket) : null;
        // Resolve target user
        final Optional<User> targetUserResult = channelServerNode.getUserByCharacterId(characterId);
        if (targetUserResult.isEmpty()) {
            log.error("Could not resolve target user for GuildResult");
            return;
        }
        try (var locked = targetUserResult.get().acquire()) {
            final User user = locked.get();
            // Set guild info and broadcast
            user.setGuildInfo(guildInfo);
            user.getField().broadcastPacket(UserRemote.guildNameChanged(user, guildInfo), user);
            user.getField().broadcastPacket(UserRemote.guildMarkChanged(user, guildInfo), user);
        }
    }
}
