package mapleglory.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import mapleglory.database.table.AccountTable;
import mapleglory.database.table.CharacterTable;
import mapleglory.server.ServerConfig;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

import static java.util.concurrent.TimeUnit.SECONDS;

public class DatabaseConnection {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConnection.class);
    private static HikariDataSource dataSource;
    private static Jdbi jdbi;

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("Unable to get connection - connection pool is uninitialized");
        }

        return dataSource.getConnection();
    }

    public static Handle getHandle() {
        if (jdbi == null) {
            throw new IllegalStateException("Unable to get handle - connection pool is uninitialized");
        }

        return jdbi.open();
    }

    private static String getDbUrl() {
        return String.format(ServerConfig.DB_URL_FORMAT, ServerConfig.DB_HOST);
    }

    private static HikariConfig getConfig() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(getDbUrl());
        config.setUsername(ServerConfig.DB_USER);
        config.setPassword(ServerConfig.DB_PASS);

        final int initFailTimeoutSeconds = ServerConfig.INIT_CONNECTION_POOL_TIMEOUT;
        config.setInitializationFailTimeout(SECONDS.toMillis(initFailTimeoutSeconds));
        config.setConnectionTimeout(SECONDS.toMillis(30)); // Hikari default
        config.setMaximumPoolSize(10);

        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 25);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

        return config;
    }

    public static boolean initializeConnectionPool() {
        if (dataSource != null) {
            return true;
        }

        final HikariConfig config = getConfig();
        log.info("Initializing database connection pool. Connecting to:'{}' with user:'{}'", config.getJdbcUrl(),
                config.getUsername());
        Instant initStart = Instant.now();
        try {
            dataSource = new HikariDataSource(config);
            jdbi = Jdbi.create(dataSource);
            long initDuration = Duration.between(initStart, Instant.now()).toMillis();
            log.info("Connection pool initialized in {} ms", initDuration);
            return true;
        } catch (Exception e) {
            long timeout = Duration.between(initStart, Instant.now()).getSeconds();
            log.error("Failed to initialize database connection pool. Gave up after {} seconds. {}", timeout, e.getMessage());
        }

        return false;
    }
}