package mapleglory.world.field;

import mapleglory.packet.field.FieldPacket;
import mapleglory.provider.QuestProvider;
import mapleglory.provider.map.Foothold;
import mapleglory.provider.quest.QuestInfo;
import mapleglory.util.Rect;
import mapleglory.world.GameConstants;
import mapleglory.world.field.drop.Drop;
import mapleglory.world.field.drop.DropEnterType;
import mapleglory.world.field.drop.DropLeaveType;
import mapleglory.world.quest.QuestRecord;
import mapleglory.world.quest.QuestState;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class DropPool extends FieldObjectPool<Drop> {
    public DropPool(Field field) {
        super(field);
    }

    public void addDrop(Drop drop, DropEnterType enterType, int x, int y, int delay) {
        // Clamp x position to map bounds
        final Rect rootBounds = field.getMapInfo().getRootBounds();
        final int boundLeft = rootBounds.getLeft() + GameConstants.DROP_BOUND_OFFSET;
        final int boundRight = rootBounds.getRight() - GameConstants.DROP_BOUND_OFFSET;
        if (boundLeft <= boundRight) {
            x = Math.clamp(x, boundLeft, boundRight);
        }
        // Assign foothold
        final Optional<Foothold> footholdResult = field.getFootholdBelow(x, y);
        if (footholdResult.isPresent()) {
            drop.setX(x);
            drop.setY(footholdResult.get().getYFromX(x));
        } else {
            drop.setX(x);
            drop.setY(y);
        }
        drop.setField(field);
        drop.setId(field.getNewObjectId());
        // Handle drop reactors
        if (enterType != DropEnterType.FADING_OUT) {
            addObject(drop);
            field.getReactorPool().forEach((reactor) -> reactor.handleDrop(drop));
        }
        // Handle quest drops
        if (drop.isQuest()) {
            field.getUserPool().forEach((user) -> {
                try (var locked = user.acquire()) {
                    final Optional<QuestRecord> questRecordResult = locked.get().getQuestManager().getQuestRecord(drop.getQuestId());
                    if (questRecordResult.isEmpty() || questRecordResult.get().getState() != QuestState.PERFORM) {
                        return;
                    }
                    final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(drop.getQuestId());
                    if (questInfoResult.isPresent() && questInfoResult.get().hasRequiredItem(user, drop.getItem().getItemId())) {
                        return;
                    }
                }
                user.write(FieldPacket.dropEnterField(drop, enterType, delay));
            });
        } else {
            field.broadcastPacket(FieldPacket.dropEnterField(drop, enterType, delay));
        }
    }

    public void addDrops(List<Drop> drops, DropEnterType enterType, int centerX, int centerY, int initialDelay, int addDelay) {
        // Split and shuffle drops
        final List<Drop> normalDrops = new ArrayList<>();
        final List<Drop> questDrops = new ArrayList<>();
        for (Drop drop : drops) {
            if (drop.isQuest()) {
                questDrops.add(drop);
            } else {
                normalDrops.add(drop);
            }
        }
        Collections.shuffle(normalDrops);
        // Add quest drops on the outer edges to avoid displacing normal drops
        for (int i = 0; i < questDrops.size(); i++) {
            if (i % 2 == 0) {
                normalDrops.addFirst(questDrops.get(i));
            } else {
                normalDrops.addLast(questDrops.get(i));
            }
        }
        // Add normal drops
        int dropX = centerX - (normalDrops.size() * GameConstants.DROP_SPREAD / 2);
        int delay = initialDelay;
        for (Drop drop : normalDrops) {
            addDrop(drop, enterType, dropX, centerY, delay);
            dropX += GameConstants.DROP_SPREAD;
            delay += addDelay;
        }
    }

    public synchronized boolean removeDrop(Drop drop, DropLeaveType leaveType, int pickUpId, int petIndex, int delay) {
        if (!removeObject(drop)) {
            return false;
        }
        field.broadcastPacket(FieldPacket.dropLeaveField(drop, leaveType, pickUpId, petIndex, delay));
        return true;
    }

    public void expireDrops(Instant now) {
        final var iter = objects.values().iterator();
        while (iter.hasNext()) {
            final Drop drop = iter.next();
            // Check drop expire time and remove drop
            if (now.isBefore(drop.getExpireTime())) {
                continue;
            }
            iter.remove();
            field.broadcastPacket(FieldPacket.dropLeaveField(drop, DropLeaveType.TIMEOUT, 0, 0, 0));
        }
    }
}
