package mapleglory.server.dialog.miniroom;

import mapleglory.server.dialog.Dialog;
import mapleglory.server.packet.InPacket;
import mapleglory.server.packet.OutPacket;
import mapleglory.util.Locked;
import mapleglory.world.field.FieldObjectImpl;
import mapleglory.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public abstract class MiniRoom extends FieldObjectImpl implements Dialog {
    protected static final Logger log = LogManager.getLogger(MiniRoom.class);

    public abstract void handlePacket(Locked<User> locked, MiniRoomProtocol mrp, InPacket inPacket);

    public abstract MiniRoomType getType();

    public abstract boolean checkPassword(String password);

    public abstract int getMaxUsers();

    public abstract boolean addUser(User user);

    public abstract Map<Integer, User> getUsers();

    public final int getPosition(User user) {
        for (var entry : getUsers().entrySet()) {
            if (entry.getValue().getCharacterId() == user.getCharacterId()) {
                return entry.getKey();
            }
        }
        return -1;
    }

    public final void broadcastPacket(OutPacket outPacket) {
        for (User user : getUsers().values()) {
            user.write(outPacket);
        }
    }

    public void close() {
        if (getField() != null) {
            getField().getMiniRoomPool().removeMiniRoom(this);
        }
    }
}
