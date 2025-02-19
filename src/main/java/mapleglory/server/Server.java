package mapleglory.server;

import mapleglory.database.DatabaseConnection;
import mapleglory.database.DatabaseManager;
import mapleglory.provider.*;
import mapleglory.script.common.ScriptDispatcher;
import mapleglory.server.cashshop.CashShop;
import mapleglory.server.command.CommandProcessor;
import mapleglory.server.node.CentralServerNode;
import mapleglory.server.node.ChannelServerNode;
import mapleglory.server.node.LoginServerNode;
import mapleglory.server.node.ServerExecutor;
import mapleglory.server.rank.RankManager;
import mapleglory.util.crypto.MapleCrypto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;

public final class Server {
    private static final Logger log = LogManager.getLogger(Server.class);
    private static CentralServerNode centralServerNode;

    public static void main(String[] args) throws Exception {
        Server.initialize();
    }

    private static void initialize() throws Exception {
        // Initialize providers
        Instant start = Instant.now();
        ItemProvider.initialize();      // Character.wz + Item.wz
        SkillProvider.initialize();     // Skill.wz + Morph.wz
        MapProvider.initialize();       // Map.wz
        MobProvider.initialize();       // Mob.wz
        NpcProvider.initialize();       // Npc.wz
        ReactorProvider.initialize();   // Reactor.wz
        QuestProvider.initialize();     // Quest.wz
        StringProvider.initialize();    // String.wz
        EtcProvider.initialize();       // Etc.wz
        ShopProvider.initialize();      // data/shop
        RewardProvider.initialize();    // data/reward
        CashShop.initialize();          // data/cash
        System.gc();
        log.info("Loaded providers in {} milliseconds", Duration.between(start, Instant.now()).toMillis());

        // Initialize server classes
        MapleCrypto.initialize();
        ServerExecutor.initialize();
        CommandProcessor.initialize();

        // Initialize database
        start = Instant.now();
        DatabaseManager.initialize();
        log.info("Loaded database (cassandra) connection in {} milliseconds", Duration.between(start, Instant.now()).toMillis());

        // Initialize database
        start = Instant.now();
        if (!DatabaseConnection.initializeConnectionPool()) {
            throw new IllegalStateException("Failed to initiate a connection to the database");
        }
        log.info("Loaded database (mysql) connection in {} milliseconds", Duration.between(start, Instant.now()).toMillis());

        // Initialize ranks
        start = Instant.now();
        RankManager.initialize();
        log.info("Loaded ranks in {} milliseconds", Duration.between(start, Instant.now()).toMillis());

        // Initialize scripts
        start = Instant.now();
        ScriptDispatcher.initialize();
        log.info("Loaded scripts in {} milliseconds", Duration.between(start, Instant.now()).toMillis());

        // Initialize nodes
        centralServerNode = new CentralServerNode();
        ServerExecutor.submitService(() -> {
            try {
                centralServerNode.initialize();
            } catch (Exception e) {
                log.error("Failed to initialize central server node", e);
                System.exit(1);
            }
        });
        for (int channelId = 0; channelId < ServerConfig.CHANNELS_PER_WORLD; channelId++) {
            final ChannelServerNode channelServerNode = new ChannelServerNode(channelId, ServerConstants.CHANNEL_PORT + channelId);
            ServerExecutor.submitService(() -> {
                try {
                    channelServerNode.initialize();
                } catch (Exception e) {
                    log.error("Failed to initialize channel server node {}", channelServerNode.getChannelId() + 1, e);
                    System.exit(1);
                }
            });
        }
        ServerExecutor.submitService(() -> {
            final LoginServerNode loginServerNode = new LoginServerNode();
            try {
                loginServerNode.initialize();
            } catch (Exception e) {
                log.error("Failed to initialize login server node", e);
                System.exit(1);
            }
        });

        // Setup shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Server.shutdown();
            } catch (Exception e) {
                log.error("Exception caught while shutting down Server", e);
                throw new RuntimeException(e);
            }
        }));
    }

    private static void shutdown() throws Exception {
        log.info("Shutting down Server");
        centralServerNode.shutdown();
        ScriptDispatcher.shutdown();
        RankManager.shutdown();
        ServerExecutor.shutdown();
        DatabaseManager.shutdown();
        LogManager.shutdown();
    }
}
