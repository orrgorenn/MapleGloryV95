package mapleglory.server.node;

import mapleglory.world.user.Account;
import mapleglory.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

public final class ClientStorage {
    private static final Logger log = LogManager.getLogger(ClientStorage.class);
    private final Lock lock = new ReentrantLock();
    private final Map<Integer, Client> mapByAccountId = new HashMap<>();
    private final Map<Integer, Client> mapByCharacterId = new HashMap<>();

    public boolean isConnected(Account account) {
        log.debug("Locking: {}", account.getUsername());

        try {
            if (!lock.tryLock(5, TimeUnit.SECONDS)) {
                log.error("Lock acquisition failed, returning false.");
                return false;
            }

            log.debug("Looking in keys: {}", mapByAccountId);
            final int accountId = account.getId();
            log.debug("Account ID: {}", accountId);
            log.debug("Current map keys: {}", mapByAccountId.keySet());

            return mapByAccountId.containsKey(accountId);
        } catch (Exception e) {
            log.error("Error when looking: {}", e.getMessage());
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean isConnected(User user) {
        lock.lock();
        try {
            return mapByCharacterId.containsKey(user.getCharacterId());
        } finally {
            lock.unlock();
        }
    }

    public Optional<User> getUserByCharacterId(int characterId) {
        lock.lock();
        try {
            final Client client = mapByCharacterId.get(characterId);
            if (client == null || client.getUser() == null) {
                return Optional.empty();
            }
            return Optional.of(client.getUser());
        } finally {
            lock.unlock();
        }
    }

    public void addClient(Client client) {
        lock.lock();
        try {
            if (client.getAccount() instanceof Account account) {
                mapByAccountId.put(account.getId(), client);
            }
            if (client.getUser() instanceof User user) {
                mapByCharacterId.put(user.getCharacterId(), client);
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeClient(Client client) {
        lock.lock();
        try {
            if (client.getAccount() instanceof Account account) {
                mapByAccountId.remove(account.getId());
            }
            if (client.getUser() instanceof User user) {
                mapByCharacterId.remove(user.getCharacterId());
            }
        } finally {
            lock.unlock();
        }
    }

    public Set<Client> getConnectedClients() {
        lock.lock();
        try {
            return mapByAccountId.values().stream()
                    .collect(Collectors.toUnmodifiableSet());
        } finally {
            lock.unlock();
        }
    }

    public Set<User> getConnectedUsers() {
        lock.lock();
        try {
            return mapByCharacterId.values().stream()
                    .map(Client::getUser)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet());
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return mapByAccountId.isEmpty();
        } finally {
            lock.unlock();
        }
    }
}
