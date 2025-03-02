package mapleglory.world.user;

import mapleglory.server.packet.InPacket;
import mapleglory.server.packet.OutPacket;
import mapleglory.util.Encodable;

public class PersonalInfo implements Encodable {
    public static final PersonalInfo EMPTY = new PersonalInfo(0, 0, 0, 0);
    private final int location;
    private final int birthday;
    private final int todo;
    private final int found;

    public PersonalInfo(int location, int birthday, int todo, int found) {
        this.location = location;
        this.birthday = birthday;
        this.todo = todo;
        this.found = found;
    }

    public PersonalInfo getPeronalInfo() {
        return new PersonalInfo(this.location, this.birthday, this.todo, this.found);
    }

    public int getLocation() {
        return location;
    }

    public int getBirthday() {
        return birthday;
    }

    public int getFound() {
        return found;
    }

    public int getTodo() {
        return todo;
    }

    @Override
    public void encode(OutPacket outPacket) { }

    public static PersonalInfo decode(InPacket inPacket) {
        final int location = inPacket.decodeInt();
        final int birthday = inPacket.decodeInt();
        final int todo = inPacket.decodeInt();
        final int found = inPacket.decodeInt();

        return new PersonalInfo(location, birthday, todo, found);
    }
}
