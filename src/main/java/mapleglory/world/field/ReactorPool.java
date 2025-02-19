package mapleglory.world.field;

import mapleglory.packet.field.FieldPacket;
import mapleglory.script.common.ScriptDispatcher;
import mapleglory.script.party.HenesysPQ;
import mapleglory.world.GameConstants;
import mapleglory.world.field.reactor.Reactor;
import mapleglory.world.user.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ReactorPool extends FieldObjectPool<Reactor> {
    private final ConcurrentHashMap<Reactor, Instant> hitReactors = new ConcurrentHashMap<>(); // reactor, next respawn time

    public ReactorPool(Field field) {
        super(field);
    }

    public Optional<Reactor> getByTemplateId(int templateId) {
        return getBy((reactor) -> reactor.getTemplateId() == templateId);
    }

    public void addReactor(Reactor reactor) {
        reactor.setField(field);
        reactor.setId(field.getNewObjectId());
        addObject(reactor);
        field.broadcastPacket(FieldPacket.reactorEnterField(reactor));
    }

    public synchronized void hitReactor(User user, Reactor reactor, int delay) {
        // Register reactor expiry
        if (reactor.getReactorTime() > 0) {
            hitReactors.put(reactor, Instant.now().plus(reactor.getReactorTime(), ChronoUnit.SECONDS));
        }
        if (reactor.getTimeOut() > 0) {
            hitReactors.put(reactor, Instant.now().plus(reactor.getTimeOut(), ChronoUnit.MILLIS));
        }
        // Broadcast reactor changing state
        field.broadcastPacket(FieldPacket.reactorChangeState(reactor, delay, 0, GameConstants.REACTOR_END_DELAY));
        // Dispatch reactor script
        if (reactor.isLastState() && reactor.hasAction()) {
            ScriptDispatcher.startReactorScript(user, reactor, reactor.getAction());
        }
        // Special handling for reactors without scripts
        if (HenesysPQ.PRIMROSE_REACTORS.contains(reactor.getTemplateId())) {
            final Optional<Reactor> moonReactorResult = getByTemplateId(HenesysPQ.MOON_REACTOR);
            if (moonReactorResult.isPresent()) {
                try (var lockedReactor = moonReactorResult.get().acquire()) {
                    final Reactor moonReactor = lockedReactor.get();
                    if (!moonReactor.isLastState()) {
                        moonReactor.setState(moonReactor.getState() + 1);
                        hitReactor(user, moonReactor, 0);
                    }
                }
            }
        }
    }

    public void expireReactors(Instant now) {
        final var iter = hitReactors.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<Reactor, Instant> entry = iter.next();
            try (var lockedReactor = entry.getKey().acquire()) {
                final Reactor reactor = lockedReactor.get();
                // Check reactor time and reset reactor
                if (now.isBefore(entry.getValue())) {
                    continue;
                }
                iter.remove();
                reactor.reset(reactor.getX(), reactor.getY(), 0);
                field.broadcastPacket(FieldPacket.reactorChangeState(reactor, 0, 0, 0));
            }
        }
    }
}
