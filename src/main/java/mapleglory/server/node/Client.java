package mapleglory.server.node;

import io.netty.channel.socket.SocketChannel;
import mapleglory.database.DatabaseManager;
import mapleglory.packet.ClientPacket;
import mapleglory.server.netty.NettyClient;
import mapleglory.world.user.Account;
import mapleglory.world.user.User;

public final class Client extends NettyClient {
    private Account account;
    private User user;
    private byte[] machineId;
    private byte[] clientKey;

    public Client(ServerNode serverNode, SocketChannel socketChannel) {
        super(serverNode, socketChannel);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public byte[] getMachineId() {
        return machineId;
    }

    public void setMachineId(byte[] machineId) {
        this.machineId = machineId;
    }

    public byte[] getClientKey() {
        return clientKey;
    }

    public void setClientKey(byte[] clientKey) {
        this.clientKey = clientKey;
    }

    public synchronized void setLoggedStatus(boolean status) {
        DatabaseManager.accountAccessor().setLoggedStatus(account, status);
    }

    public void sendPing() {
        write(ClientPacket.aliveReq());
    }

    @Override
    public synchronized void close() {
        super.close();
        if (user == null) {
            if (account != null) {
                try (var lockedAccount = account.acquire()) {
                    setLoggedStatus(false);
                    DatabaseManager.accountAccessor().saveAccount(account);
                }
            }
        } else if (!user.isInTransfer()) {
            try (var locked = user.acquire()) {
                try (var lockedAccount = account.acquire()) {
                    user.logout(true);
                    DatabaseManager.accountAccessor().saveAccount(account);
                    DatabaseManager.characterAccessor().saveCharacter(user.getCharacterData());
                }
            }
        }
        getServerNode().removeClient(this);
        account = null;
        user = null;
    }
}
