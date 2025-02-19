package mapleglory.server.node;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import mapleglory.packet.CentralPacket;
import mapleglory.server.ServerConstants;
import mapleglory.server.guild.Guild;
import mapleglory.server.guild.GuildMember;
import mapleglory.server.guild.GuildRank;
import mapleglory.server.guild.GuildStorage;
import mapleglory.server.messenger.Messenger;
import mapleglory.server.messenger.MessengerStorage;
import mapleglory.server.messenger.MessengerUser;
import mapleglory.server.migration.MigrationInfo;
import mapleglory.server.migration.MigrationStorage;
import mapleglory.server.netty.CentralPacketDecoder;
import mapleglory.server.netty.CentralPacketEncoder;
import mapleglory.server.netty.CentralServerHandler;
import mapleglory.server.netty.NettyContext;
import mapleglory.server.party.Party;
import mapleglory.server.party.PartyStorage;
import mapleglory.server.user.RemoteUser;
import mapleglory.server.user.UserStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class CentralServerNode extends Node {
    private static final Logger log = LogManager.getLogger(CentralServerNode.class);
    private final ServerStorage serverStorage = new ServerStorage();
    private final MigrationStorage migrationStorage = new MigrationStorage();
    private final UserStorage userStorage = new UserStorage();
    private final MessengerStorage messengerStorage = new MessengerStorage();
    private final PartyStorage partyStorage = new PartyStorage();
    private final GuildStorage guildStorage = new GuildStorage();

    private final CompletableFuture<?> initializeFuture = new CompletableFuture<>();
    private final CompletableFuture<?> shutdownFuture = new CompletableFuture<>();
    private ChannelFuture centralServerFuture;


    // CHANNEL METHODS -------------------------------------------------------------------------------------------------

    public synchronized void addServerNode(RemoteServerNode serverNode) {
        serverStorage.addServerNode(serverNode);
        if (serverStorage.isFull()) {
            initializeFuture.complete(null);
        }
    }

    public synchronized void removeServerNode(int channelId) {
        serverStorage.removeServerNode(channelId);
        if (serverStorage.isEmpty()) {
            shutdownFuture.complete(null);
        }
    }

    public Optional<RemoteServerNode> getChannelServerNodeById(int channelId) {
        return serverStorage.getChannelServerNodeById(channelId);
    }

    public List<RemoteServerNode> getChannelServerNodes() {
        return serverStorage.getChannelServerNodes();
    }


    // MIGRATION METHODS -----------------------------------------------------------------------------------------------

    public boolean isOnline(int accountId) {
        return migrationStorage.isMigrating(accountId) || userStorage.getByAccountId(accountId).isPresent();
    }

    public boolean isMigrating(int accountId) {
        return migrationStorage.isMigrating(accountId);
    }

    public boolean submitMigrationRequest(MigrationInfo migrationInfo) {
        return migrationStorage.submitMigrationRequest(migrationInfo);
    }

    public Optional<MigrationInfo> completeMigrationRequest(int channelId, int accountId, int characterId, byte[] machineId, byte[] clientKey) {
        return migrationStorage.completeMigrationRequest(channelId, accountId, characterId, machineId, clientKey);
    }


    // USER METHODS ----------------------------------------------------------------------------------------------------

    public List<RemoteUser> getUsers() {
        return userStorage.getUsers();
    }

    public Optional<RemoteUser> getUserByCharacterId(int characterId) {
        return userStorage.getByCharacterId(characterId);
    }

    public Optional<RemoteUser> getUserByCharacterName(String characterName) {
        return userStorage.getByCharacterName(characterName);
    }

    public void addUser(RemoteUser remoteUser) {
        userStorage.putUser(remoteUser);
        getChannelServerNodeById(remoteUser.getChannelId()).ifPresent(RemoteServerNode::incrementUserCount);
    }

    public void updateUser(RemoteUser remoteUser) {
        userStorage.putUser(remoteUser);
    }

    public void removeUser(RemoteUser remoteUser) {
        userStorage.removeUser(remoteUser);
        getChannelServerNodeById(remoteUser.getChannelId()).ifPresent(RemoteServerNode::decrementUserCount);
    }


    // MESSENGER METHODS -----------------------------------------------------------------------------------------------

    public Messenger createNewMessenger(RemoteUser remoteUser, MessengerUser messengerUser) {
        final Messenger messenger = new Messenger(messengerStorage.getNewMessengerId());
        messenger.addUser(remoteUser, messengerUser);
        messengerStorage.addMessenger(messenger);
        return messenger;
    }

    public boolean removeMessenger(Messenger messenger) {
        return messengerStorage.removeMessenger(messenger);
    }

    public Optional<Messenger> getMessengerById(int messengerId) {
        if (messengerId == 0) {
            return Optional.empty();
        }
        return messengerStorage.getMessengerById(messengerId);
    }


    // PARTY METHODS ---------------------------------------------------------------------------------------------------

    public Party createNewParty(int partyId, RemoteUser remoteUser) {
        final Party party = new Party(partyId, remoteUser);
        partyStorage.addParty(party);
        return party;
    }

    public boolean removeParty(Party party) {
        return partyStorage.removeParty(party);
    }

    public Optional<Party> getPartyById(int partyId) {
        if (partyId == 0) {
            return Optional.empty();
        }
        return partyStorage.getPartyById(partyId);
    }


    // GUILD METHODS ---------------------------------------------------------------------------------------------------

    public Optional<Guild> createNewGuild(int guildId, String guildName, RemoteUser remoteUser) {
        final Guild guild = new Guild(guildId, guildName);
        final GuildMember member = GuildMember.from(remoteUser);
        member.setGuildRank(GuildRank.MASTER);
        if (!guild.addMember(member)) {
            throw new IllegalStateException("Could not add master to guild");
        }
        if (!guildStorage.addGuild(guild)) {
            return Optional.empty();
        }
        return Optional.of(guild);
    }

    public boolean removeGuild(Guild guild) {
        return guildStorage.removeGuild(guild);
    }

    public Optional<Guild> getGuildById(int guildId) {
        if (guildId == 0) {
            return Optional.empty();
        }
        return guildStorage.getGuildById(guildId);
    }


    // OVERRIDES -------------------------------------------------------------------------------------------------------

    @Override
    public void initialize() throws InterruptedException {
        // Start central server
        final CentralServerNode self = this;
        centralServerFuture = startServer(new ChannelInitializer<>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new CentralPacketDecoder(), new CentralServerHandler(self), new CentralPacketEncoder());
                ch.attr(NettyContext.CONTEXT_KEY).set(new NettyContext());
                ch.attr(RemoteServerNode.NODE_KEY).set(new RemoteServerNode(ch));
                ch.writeAndFlush(CentralPacket.initializeRequest());
            }
        }, ServerConstants.CENTRAL_PORT);
        centralServerFuture.sync();
        log.info("Central server listening on port {}", ServerConstants.CENTRAL_PORT);

        // Wait for child node connections
        final Instant start = Instant.now();
        initializeFuture.join();
        log.info("All servers connected in {} milliseconds", Duration.between(start, Instant.now()).toMillis());

        // Complete initialization for login server node
        final RemoteServerNode loginServerNode = serverStorage.getLoginServerNode().orElseThrow();
        loginServerNode.write(CentralPacket.initializeComplete(serverStorage.getChannelServerNodes()));
    }

    @Override
    public void shutdown() throws InterruptedException {
        // Shutdown login server node
        final Instant start = Instant.now();
        serverStorage.getLoginServerNode().ifPresent((serverNode) -> serverNode.write(CentralPacket.shutdownRequest()));

        // Shutdown channel server nodes
        for (RemoteServerNode serverNode : serverStorage.getChannelServerNodes()) {
            serverNode.write(CentralPacket.shutdownRequest());
        }
        shutdownFuture.join();
        log.info("All servers disconnected in {} milliseconds", Duration.between(start, Instant.now()).toMillis());

        // Close central server
        centralServerFuture.channel().close().sync();
        log.info("Central server closed");
    }
}
