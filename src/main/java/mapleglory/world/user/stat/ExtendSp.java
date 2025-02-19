package mapleglory.world.user.stat;

import mapleglory.server.packet.OutPacket;
import mapleglory.util.Encodable;

import java.util.Map;

public final class ExtendSp implements Encodable {
    public static final int DEFAULT_JOB_LEVEL = 0; // for non-ExtendSp jobs
    private final Map<Integer, Integer> map; // nJobLevel -> nSP

    public ExtendSp(Map<Integer, Integer> map) {
        this.map = map;
    }

    public Map<Integer, Integer> getMap() {
        return map;
    }

    public int getNonExtendSp() {
        return map.getOrDefault(DEFAULT_JOB_LEVEL, 0);
    }

    public void setNonExtendSp(int sp) {
        setSp(DEFAULT_JOB_LEVEL, sp);
    }

    public void addNonExtendSp(int sp) {
        addSp(DEFAULT_JOB_LEVEL, sp);
    }

    public boolean removeNonExtendSp(int sp) {
        return removeSp(DEFAULT_JOB_LEVEL, sp);
    }

    public void setSp(int jobLevel, int sp) {
        map.put(jobLevel, sp);
    }

    public void addSp(int jobLevel, int sp) {
        map.put(jobLevel, map.getOrDefault(jobLevel, 0) + sp);
    }

    public boolean removeSp(int jobLevel, int sp) {
        final int currentSp = map.getOrDefault(jobLevel, 0);
        if (currentSp < sp) {
            return false;
        }
        map.put(jobLevel, currentSp - sp);
        return true;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(map.size());
        for (var entry : map.entrySet()) {
            outPacket.encodeByte(entry.getKey()); // nJobLevel
            outPacket.encodeByte(entry.getValue()); // nSP
        }
    }

    public static ExtendSp from(Map<Integer, Integer> map) {
        return new ExtendSp(map);
    }
}
