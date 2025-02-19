package mapleglory.server.field;

import mapleglory.world.field.Field;

import java.util.Optional;

public interface FieldStorage {
    Optional<Field> getFieldById(int mapId);

    void clear();
}
