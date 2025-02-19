package mapleglory.world.field;

import mapleglory.server.packet.OutPacket;
import mapleglory.world.user.User;

public interface ControlledObject extends FieldObject {
    User getController();

    void setController(User controller);

    OutPacket changeControllerPacket(boolean forController);
}
