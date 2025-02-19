package mapleglory.server.event;

import mapleglory.packet.field.FieldPacket;
import mapleglory.provider.MobProvider;
import mapleglory.provider.map.PortalInfo;
import mapleglory.provider.mob.MobTemplate;
import mapleglory.server.field.FieldStorage;
import mapleglory.server.packet.OutPacket;
import mapleglory.world.field.Field;
import mapleglory.world.field.mob.Mob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

public abstract class Event {
    protected static final Logger log = LogManager.getLogger(Event.class);
    protected final FieldStorage fieldStorage;
    protected EventState currentState;
    protected ScheduledFuture<?> eventFuture;

    public Event(FieldStorage fieldStorage) {
        this.fieldStorage = fieldStorage;
    }

    public abstract EventType getType();

    public abstract void initialize();

    public abstract void nextState();

    public final EventState getState() {
        return currentState;
    }

    public final void shutdown() {
        eventFuture.cancel(true);
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    protected final int getNearestMinute() {
        long seconds = (System.currentTimeMillis() / 1000) % 3600;
        return (int) Math.round(seconds / 60.0) % 60;
    }

    protected final void warp(int sourceFieldId, int destinationFieldId, String portalName) {
        // Resolve destination field
        final Optional<Field> destinationFieldResult = fieldStorage.getFieldById(destinationFieldId);
        if (destinationFieldResult.isEmpty()) {
            log.error("Could not resolve destination field ID : {}", destinationFieldId);
            return;
        }
        final Field destinationField = destinationFieldResult.get();
        // Resolve portal
        final Optional<PortalInfo> destinationPortalResult = destinationField.getPortalByName(portalName);
        if (destinationPortalResult.isEmpty()) {
            log.error("Could not resolve portal {} for field ID : {}", portalName, destinationFieldId);
            return;
        }
        final PortalInfo destinationPortal = destinationPortalResult.get();
        // Warp users in source field
        final Optional<Field> sourceFieldResult = fieldStorage.getFieldById(sourceFieldId);
        if (sourceFieldResult.isEmpty()) {
            log.error("Could not resolve source field ID : {}", sourceFieldId);
            return;
        }
        sourceFieldResult.get().getUserPool().forEach((user) -> {
            try (var locked = user.acquire()) {
                locked.get().warp(destinationField, destinationPortal, false, false);
            }
        });
    }

    protected final void spawnMob(int fieldId, int mobTemplateId, int x, int y) {
        // Resolve field
        final Optional<Field> fieldResult = fieldStorage.getFieldById(fieldId);
        if (fieldResult.isEmpty()) {
            log.error("Could not resolve field ID : {}", fieldId);
            return;
        }
        final Field field = fieldResult.get();
        // Create mob
        final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(mobTemplateId);
        if (mobTemplateResult.isEmpty()) {
            log.error("Could not resolve mob template ID : {}", mobTemplateId);
            return;
        }
        final Mob mob = new Mob(mobTemplateResult.get(), null, x, y, 0);
        field.getMobPool().addMob(mob);
    }

    protected final void setReactorState(int fieldId, int reactorTemplateId, int newState) {
        // Resolve field
        final Optional<Field> fieldResult = fieldStorage.getFieldById(fieldId);
        if (fieldResult.isEmpty()) {
            log.error("Could not resolve field ID : {}", fieldId);
            return;
        }
        final Field field = fieldResult.get();
        // Resolve and change reactor state
        field.getReactorPool().forEach((reactor) -> {
            if (reactor.getTemplateId() == reactorTemplateId) {
                try (var lockedReactor = reactor.acquire()) {
                    reactor.setState(newState);
                    field.broadcastPacket(FieldPacket.reactorChangeState(reactor, 0, 0, 0));
                }
            }
        });
    }

    protected final void broadcastPacket(int fieldId, OutPacket outPacket) {
        final Optional<Field> fieldResult = fieldStorage.getFieldById(fieldId);
        if (fieldResult.isEmpty()) {
            log.error("Could not resolve broadcast field ID : {}", fieldId);
            return;
        }
        fieldResult.get().getUserPool().forEach((user) -> {
            user.write(outPacket);
        });
    }

    protected final void reset(int fieldId) {
        final Optional<Field> fieldResult = fieldStorage.getFieldById(fieldId);
        if (fieldResult.isEmpty()) {
            log.error("Could not resolve field ID : {}", fieldId);
            return;
        }
        fieldResult.get().reset();
    }
}
