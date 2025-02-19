package mapleglory.handler.field;

import mapleglory.handler.Handler;
import mapleglory.packet.field.ContiMovePacket;
import mapleglory.packet.field.FieldPacket;
import mapleglory.packet.world.MessagePacket;
import mapleglory.packet.world.WvsContext;
import mapleglory.provider.QuestProvider;
import mapleglory.provider.quest.QuestInfo;
import mapleglory.server.event.*;
import mapleglory.server.header.InHeader;
import mapleglory.server.packet.InPacket;
import mapleglory.world.GameConstants;
import mapleglory.world.field.Field;
import mapleglory.world.field.drop.Drop;
import mapleglory.world.field.drop.DropLeaveType;
import mapleglory.world.field.drop.DropOwnType;
import mapleglory.world.field.reactor.Reactor;
import mapleglory.world.item.InventoryManager;
import mapleglory.world.item.InventoryOperation;
import mapleglory.world.quest.QuestRecord;
import mapleglory.world.quest.QuestState;
import mapleglory.world.user.User;
import mapleglory.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public final class FieldHandler {
    private static final Logger log = LogManager.getLogger(FieldHandler.class);

    @Handler(InHeader.CANCEL_INVITE_PARTY_MATCH)
    public static void handleCancelInvitePartyMatch(User user, InPacket inPacket) {
    }


    // BEGIN_DROPPOOL --------------------------------------------------------------------------------------------------

    @Handler(InHeader.DropPickUpRequest)
    public static void handleDropPickUpRequest(User user, InPacket inPacket) {
        final byte fieldKey = inPacket.decodeByte();
        if (user.getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        inPacket.decodeInt(); // update_time
        inPacket.decodeShort(); // pt->x
        inPacket.decodeShort(); // pt->y
        final int objectId = inPacket.decodeInt(); // dwDropID
        inPacket.decodeInt(); // dwCliCrc

        // Find drop in field
        final Field field = user.getField();
        final Optional<Drop> dropResult = field.getDropPool().getById(objectId);
        if (dropResult.isEmpty()) {
            user.dispose();
            return;
        }
        final Drop drop = dropResult.get();

        // Verify user can pick up drop
        if (!drop.canPickUp(user)) {
            log.error("Tried to pick up drop not owned by user");
            user.dispose();
            return;
        }

        try (var locked = user.acquire()) {
            // Check if drop can be added to inventory
            final InventoryManager im = user.getInventoryManager();
            if (drop.isMoney()) {
                final long newMoney = ((long) im.getMoney()) + drop.getMoney();
                if (newMoney > GameConstants.MONEY_MAX) {
                    user.write(MessagePacket.unavailableForPickUp());
                    user.dispose();
                    return;
                }
            } else {
                // Inventory full
                if (!im.canAddItem(drop.getItem())) {
                    user.write(MessagePacket.cannotGetAnymoreItems());
                    user.dispose();
                    return;
                }
                // Quest item handling
                if (drop.isQuest()) {
                    final Optional<QuestRecord> questRecordResult = user.getQuestManager().getQuestRecord(drop.getQuestId());
                    if (questRecordResult.isEmpty() || questRecordResult.get().getState() != QuestState.PERFORM) {
                        user.write(MessagePacket.unavailableForPickUp());
                        user.dispose();
                        return;
                    }
                    final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(drop.getQuestId());
                    if (questInfoResult.isPresent() && questInfoResult.get().hasRequiredItem(user, drop.getItem().getItemId())) {
                        user.write(MessagePacket.cannotGetAnymoreItems());
                        user.dispose();
                        return;
                    }
                }
            }

            // Try removing drop from field
            if (!field.getDropPool().removeDrop(drop, DropLeaveType.PICKED_UP_BY_USER, user.getCharacterId(), 0, 0)) {
                user.dispose();
                return;
            }

            // Add drop to inventory
            if (drop.isMoney()) {
                int money = drop.getMoney();
                if (drop.getOwnType() == DropOwnType.PARTYOWN) {
                    final List<User> partyMembers = user.getField().getUserPool().getPartyMembers(user.getPartyId());
                    if (!partyMembers.isEmpty()) {
                        final int split = money / partyMembers.size();
                        for (User member : partyMembers) {
                            if (member.getCharacterId() == user.getCharacterId()) {
                                continue;
                            }
                            try (var lockedMember = member.acquire()) {
                                if (member.getInventoryManager().addMoney(split)) {
                                    money -= split;
                                    member.write(WvsContext.statChanged(Stat.MONEY, member.getInventoryManager().getMoney(), false));
                                    member.write(MessagePacket.pickUpMoney(split, false));
                                }
                            }
                        }
                    }
                }
                if (money <= 0 || !im.addMoney(money)) {
                    throw new IllegalStateException("Could not add money to inventory");
                }
                user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
                user.write(MessagePacket.pickUpMoney(money, false));
            } else {
                final Optional<List<InventoryOperation>> addItemResult = im.addItem(drop.getItem());
                if (addItemResult.isEmpty()) {
                    throw new IllegalStateException("Could not add item to inventory");
                }
                user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
                user.write(MessagePacket.pickUpItem(drop.getItem()));
            }
        }
    }


    // BEGIN_REACTORPOOL -----------------------------------------------------------------------------------------------

    @Handler(InHeader.ReactorHit)
    public static void handleReactorHit(User user, InPacket inPacket) {
        final int objectId = inPacket.decodeInt(); // dwID
        inPacket.decodeInt(); // skillReactor?
        inPacket.decodeInt(); // dwHitOption
        final short delay = inPacket.decodeShort(); // tDelay
        final int skillId = inPacket.decodeInt(); // skillId, 0 for basic attack

        final Field field = user.getField();
        final Optional<Reactor> reactorResult = field.getReactorPool().getById(objectId);
        if (reactorResult.isEmpty()) {
            log.error("Received ReactorHit for invalid object with ID : {}", objectId);
            return;
        }
        try (var lockedReactor = reactorResult.get().acquire()) {
            // Hit reactor
            final Reactor reactor = lockedReactor.get();
            if (reactor.isNotHitable()) {
                log.error("{} : tried to hit reactor that is not hitable", reactor);
                return;
            }
            if (!reactor.tryHit(skillId)) {
                log.error("{} : could not hit reactor with skill ID {}", reactor, skillId);
                return;
            }
            field.getReactorPool().hitReactor(user, reactor, delay);
        }
    }

    @Handler(InHeader.ReactorTouch)
    public static void handleReactorTouch(User user, InPacket inPacket) {
        final int objectId = inPacket.decodeInt(); // dwID
        final boolean inside = inPacket.decodeBoolean(); // PtInRect

        final Field field = user.getField();
        final Optional<Reactor> reactorResult = field.getReactorPool().getById(objectId);
        if (reactorResult.isEmpty()) {
            log.error("Received ReactorTouch for invalid object with ID : {}", objectId);
            return;
        }
        try (var lockedReactor = reactorResult.get().acquire()) {
            // Hit reactor
            final Reactor reactor = lockedReactor.get();
            if (!reactor.isActivateByTouch()) {
                log.error("{} : tried to hit reactor that is not activated by touch", reactor);
                return;
            }
            // There are no reactors activated by touch in v95
            log.error(String.format("Unexpected reactor touch received for %s", reactor));
        }
    }

    @Handler(InHeader.RequireFieldObstacleStatus)
    public static void handleRequireFieldObstacleStatus(User user, InPacket inPacket) {
    }


    // CONTISTATE ------------------------------------------------------------------------------------------------------

    @Handler(InHeader.CONTISTATE)
    public static void handleContiState(User user, InPacket inPacket) {
        final int fieldId = inPacket.decodeInt();
        inPacket.decodeByte(); // nShipKind
        // Resolve event type
        final EventType eventType;
        switch (fieldId) {
            case ContiMoveVictoria.ORBIS_STATION_VICTORIA_BOUND, ContiMoveVictoria.STATION_TO_ORBIS,
                    ContiMoveVictoria.DURING_THE_RIDE_VICTORIA_BOUND, ContiMoveVictoria.DURING_THE_RIDE_TO_ORBIS -> {
                eventType = EventType.CM_VICTORIA;
            }
            case ContiMoveLudibrium.ORBIS_STATION_LUDIBRIUM, ContiMoveLudibrium.LUDIBRIUM_STATION_ORBIS -> {
                eventType = EventType.CM_LUDIBRIUM;
            }
            case ContiMoveLeafre.ORBIS_STATION_TO_LEAFRE, ContiMoveLeafre.LEAFRE_STATION -> {
                eventType = EventType.CM_LEAFRE;
            }
            case ContiMoveAriant.ORBIS_STATION_TO_ARIANT, ContiMoveAriant.ARIANT_STATION_PLATFORM -> {
                eventType = EventType.CM_ARIANT;
            }
            default -> {
                log.error("Received CONTISTATE for unhandled field ID : {}", fieldId);
                return;
            }
        }
        // Resolve event state
        final Optional<EventState> eventStateResult = user.getConnectedServer().getEventState(eventType);
        if (eventStateResult.isEmpty()) {
            log.error("Could not resolve event state for event type : {}", eventType);
            return;
        }
        // Update client
        final EventState eventState = eventStateResult.get();
        if (eventState == EventState.CONTIMOVE_BOARDING || eventState == EventState.CONTIMOVE_WAITING) {
            user.write(ContiMovePacket.enterShipMove());
        } else if (eventState == EventState.CONTIMOVE_MOBGEN) {
            user.write(ContiMovePacket.mobGen());
        }
    }


    // BEGIN_ITEMUPGRADE -----------------------------------------------------------------------------------------------

    @Handler(InHeader.ItemUpgradeComplete)
    public static void handleItemUpgradeComplete(User user, InPacket inPacket) {
        inPacket.decodeInt(); // nReturnResult
        final int result = inPacket.decodeInt(); // nResult
        user.write(FieldPacket.itemUpgradeResultDone(result));
    }
}
