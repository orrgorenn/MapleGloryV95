package mapleglory.server.field;

import mapleglory.packet.field.FieldPacket;
import mapleglory.server.node.ChannelServerNode;
import mapleglory.util.Lockable;
import mapleglory.world.user.User;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public final class Instance implements Lockable<Instance> {
    private final Lock lock = new ReentrantLock();
    private final int instanceId;
    private final int returnMap;
    private final Map<Integer, User> userMap;
    private final Map<String, String> variables;
    private final ChannelServerNode channelServerNode;
    private final Instant expireTime;

    private InstanceFieldStorage fieldStorage;

    public Instance(int instanceId, int returnMap, ChannelServerNode channelServerNode, Instant expireTime) {
        this.instanceId = instanceId;
        this.returnMap = returnMap;
        this.userMap = new ConcurrentHashMap<>();
        this.variables = new ConcurrentHashMap<>();
        this.channelServerNode = channelServerNode;
        this.expireTime = expireTime;
    }

    public ChannelServerNode getChannelServerNode() {
        return channelServerNode;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public int getReturnMap() {
        return returnMap;
    }

    public Instant getExpireTime() {
        return expireTime;
    }

    public InstanceFieldStorage getFieldStorage() {
        return fieldStorage;
    }

    public void setFieldStorage(InstanceFieldStorage fieldStorage) {
        this.fieldStorage = fieldStorage;
    }

    public void addUser(User user) {
        userMap.put(user.getId(), user);
        // Show clock
        final int remain = (int) (getExpireTime().getEpochSecond() - Instant.now().getEpochSecond());
        user.write(FieldPacket.clock(remain));
    }

    public void removeUser(User user) {
        userMap.remove(user.getId(), user);
    }

    public Set<User> getUsers() {
        return userMap.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    public Optional<String> getVariable(String key) {
        return Optional.ofNullable(variables.get(key));
    }

    public void setVariable(String key, String value) {
        variables.put(key, value);
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }
}
