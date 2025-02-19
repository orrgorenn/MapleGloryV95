package mapleglory.world.user;

import mapleglory.server.packet.OutPacket;
import mapleglory.util.Encodable;
import mapleglory.world.item.Inventory;
import mapleglory.world.user.stat.CharacterStat;

public final class AvatarData implements Encodable {
    private static final Inventory EMPTY_INVENTORY = new Inventory(0);
    private final CharacterStat characterStat;
    private final AvatarLook avatarLook;

    public AvatarData(CharacterStat characterStat, AvatarLook avatarLook) {
        this.characterStat = characterStat;
        this.avatarLook = avatarLook;
    }

    @Override
    public void encode(OutPacket outPacket) {
        characterStat.encode(outPacket);
        avatarLook.encode(outPacket);
    }

    public int getCharacterId() {
        return characterStat.getId();
    }

    public String getCharacterName() {
        return characterStat.getName();
    }

    public int getLevel() {
        return characterStat.getLevel();
    }

    public static AvatarData from(CharacterStat characterStat, Inventory equipped, Inventory cashInventory) {
        return new AvatarData(characterStat, AvatarLook.from(characterStat, equipped, cashInventory));
    }

    public static AvatarData from(CharacterStat characterStat, Inventory equipped) {
        // When you don't care about the pets
        return AvatarData.from(characterStat, equipped, EMPTY_INVENTORY);
    }

    public static AvatarData from(CharacterData cd) {
        return AvatarData.from(cd.getCharacterStat(), cd.getInventoryManager().getEquipped(), cd.getInventoryManager().getCashInventory());
    }
}
