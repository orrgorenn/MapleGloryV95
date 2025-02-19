package mapleglory.world.field;

import mapleglory.packet.field.FieldPacket;
import mapleglory.server.packet.OutPacket;
import mapleglory.world.user.User;

import java.time.Instant;
import java.util.Optional;

public final class TownPortalPool extends FieldObjectPool<TownPortal> {
    public TownPortalPool(Field field) {
        super(field);
    }

    public void addTownPortal(TownPortal townPortal) {
        // Destroy existing portal
        final Optional<TownPortal> existingPortal = getById(townPortal.getId());
        existingPortal.ifPresent(TownPortal::destroy);
        // Add portal and update clients
        addObject(townPortal);
        if (townPortal.getTownField() == field) {
            // Town portal does not require create packet
            return;
        }
        final Optional<User> ownerInField = field.getUserPool().getById(townPortal.getOwner().getCharacterId());
        final OutPacket outPacket = FieldPacket.townPortalCreated(townPortal.getOwner(), townPortal.getX(), townPortal.getY(), true);
        ownerInField.ifPresent(owner -> owner.write(outPacket));
        field.getUserPool().forEachPartyMember(townPortal.getOwner(), (member) -> {
            member.write(outPacket);
        });
    }

    public void removeTownPortal(TownPortal townPortal) {
        // Remove portal and update clients
        removeObject(townPortal);
        final OutPacket outPacket = FieldPacket.townPortalRemoved(townPortal.getOwner(), true);
        final Optional<User> ownerInField = field.getUserPool().getById(townPortal.getOwner().getCharacterId());
        ownerInField.ifPresent(owner -> owner.write(outPacket));
        field.getUserPool().forEachPartyMember(townPortal.getOwner(), (member) -> {
            member.write(outPacket);
        });
    }

    public Optional<TownPortal> createFieldPortal(User user, int skillId, int x, int y, Instant expireTime) {
        // Resolve town field
        final Optional<Field> returnMapResult = field.getFieldStorage().getFieldById(field.getReturnMap());
        if (returnMapResult.isEmpty() || returnMapResult.get() == field) {
            return Optional.empty();
        }
        // Create portal in town field
        final Optional<TownPortal> townPortalResult = returnMapResult.get().getTownPortalPool().createTownPortal(user, skillId, field, x, y, expireTime);
        if (townPortalResult.isEmpty()) {
            return Optional.empty();
        }
        // Add portal in current field
        final TownPortal townPortal = townPortalResult.get();
        addTownPortal(townPortal);
        return townPortalResult;
    }

    private Optional<TownPortal> createTownPortal(User user, int skillId, Field targetField, int x, int y, Instant expireTime) {
        // Create portal
        final TownPortal townPortal = TownPortal.from(user, skillId, field, targetField, x, y, expireTime);
        addTownPortal(townPortal);
        return Optional.of(townPortal);
    }
}
