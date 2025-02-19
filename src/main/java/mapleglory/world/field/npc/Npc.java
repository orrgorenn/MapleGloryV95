package mapleglory.world.field.npc;

import mapleglory.packet.field.NpcPacket;
import mapleglory.provider.map.LifeInfo;
import mapleglory.provider.npc.NpcTemplate;
import mapleglory.server.packet.OutPacket;
import mapleglory.util.Encodable;
import mapleglory.world.field.ControlledObject;
import mapleglory.world.field.life.Life;
import mapleglory.world.user.User;

public final class Npc extends Life implements ControlledObject, Encodable {
    private final NpcTemplate template;
    private final int rx0;
    private final int rx1;
    private User controller;

    public Npc(NpcTemplate template, int x, int y, int rx0, int rx1, int fh, boolean isFlip) {
        this.template = template;
        this.rx0 = rx0;
        this.rx1 = rx1;

        // Life initialization
        setX(x);
        setY(y);
        setFoothold(fh);
        setMoveAction(isFlip ? 0 : 1);
    }

    public NpcTemplate getTemplate() {
        return template;
    }

    public int getTemplateId() {
        return template.getId();
    }

    public boolean isMove() {
        return template.isMove();
    }

    public int getTrunkGet() {
        return template.getTrunkGet();
    }

    public int getTrunkPut() {
        return template.getTrunkPut();
    }

    public String getScript() {
        return template.getScript();
    }

    public boolean hasScript() {
        return template.hasScript();
    }

    public boolean isTrunk() {
        return template.isTrunk();
    }

    public boolean isGuildRank() {
        return template.isGuildRank();
    }

    public boolean isImitate() {
        return template.isImitate();
    }

    @Override
    public User getController() {
        return controller;
    }

    @Override
    public void setController(User controller) {
        this.controller = controller;
    }

    @Override
    public OutPacket changeControllerPacket(boolean forController) {
        return NpcPacket.npcChangeController(this, forController);
    }

    @Override
    public String toString() {
        return String.format("Npc { %d, oid : %d, script : %s, controller : %s }", getTemplateId(), getId(), hasScript() ? getScript() : "-", getController() != null ? getController().getCharacterName() : "null");
    }

    @Override
    public void encode(OutPacket outPacket) {
        // CNpc::Init
        outPacket.encodeShort(getX()); // ptPos.x
        outPacket.encodeShort(getY()); // ptPos.y
        outPacket.encodeByte(getMoveAction()); // nMoveAction
        outPacket.encodeShort(getFoothold()); // Foothold
        outPacket.encodeShort(rx0); // rgHorz.low
        outPacket.encodeShort(rx1); // rgHorz.high
        outPacket.encodeByte(true); // bEnabled
    }

    public static Npc from(NpcTemplate template, LifeInfo lifeInfo) {
        return new Npc(
                template,
                lifeInfo.getX(),
                lifeInfo.getY(),
                lifeInfo.getRx0(),
                lifeInfo.getRx1(),
                lifeInfo.getFh(),
                lifeInfo.isFlip()
        );
    }
}
