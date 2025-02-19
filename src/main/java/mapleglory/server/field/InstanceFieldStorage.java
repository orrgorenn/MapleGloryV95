package mapleglory.server.field;

import mapleglory.provider.map.MapInfo;
import mapleglory.world.field.Field;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InstanceFieldStorage implements FieldStorage {
    private final ConcurrentHashMap<Integer, Field> fieldMap = new ConcurrentHashMap<>(); // map id -> field
    private final Instance instance;

    public InstanceFieldStorage(Instance instance) {
        this.instance = instance;
    }

    public Instance getInstance() {
        return instance;
    }

    @Override
    public Optional<Field> getFieldById(int mapId) {
        return Optional.ofNullable(fieldMap.get(mapId));
    }

    @Override
    public void clear() {
        final var iter = fieldMap.values().iterator();
        while (iter.hasNext()) {
            final Field field = iter.next();
            field.getFieldEventFuture().cancel(true);
            iter.remove();
        }
    }

    public static InstanceFieldStorage from(Instance instance, List<MapInfo> mapInfos) {
        final InstanceFieldStorage fieldStorage = new InstanceFieldStorage(instance);
        for (MapInfo mapInfo : mapInfos) {
            fieldStorage.fieldMap.put(mapInfo.getMapId(), Field.from(fieldStorage, mapInfo));
        }
        return fieldStorage;
    }
}
