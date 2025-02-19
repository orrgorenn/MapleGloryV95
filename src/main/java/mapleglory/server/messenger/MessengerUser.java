package mapleglory.server.messenger;

import mapleglory.server.packet.InPacket;
import mapleglory.server.packet.OutPacket;
import mapleglory.util.Encodable;
import mapleglory.world.user.AvatarLook;
import mapleglory.world.user.User;

public final class MessengerUser implements Encodable {
    private final AvatarLook avatarLook;
    private final String characterName;
    private final int channelId;

    public MessengerUser(AvatarLook avatarLook, String characterName, int channelId) {
        this.avatarLook = avatarLook;
        this.characterName = characterName;
        this.channelId = channelId;
    }

    public AvatarLook getAvatarLook() {
        return avatarLook;
    }

    public String getCharacterName() {
        return characterName;
    }

    public int getChannelId() {
        return channelId;
    }

    @Override
    public void encode(OutPacket outPacket) {
        avatarLook.encode(outPacket);
        outPacket.encodeString(characterName);
        outPacket.encodeByte(channelId);
    }

    public static MessengerUser decode(InPacket inPacket) {
        final AvatarLook avatarLook = AvatarLook.decode(inPacket);
        final String characterName = inPacket.decodeString();
        final int channelId = inPacket.decodeByte();
        return new MessengerUser(avatarLook, characterName, channelId);
    }

    public static MessengerUser from(User user) {
        return new MessengerUser(user.getCharacterData().getAvatarLook(), user.getCharacterName(), user.getChannelId());
    }
}
