package mapleglory.world.field;

import mapleglory.server.dialog.miniroom.MiniRoom;
import mapleglory.server.dialog.miniroom.MiniRoomType;
import mapleglory.util.Rect;

public final class MiniRoomPool extends FieldObjectPool<MiniRoom> {
    public MiniRoomPool(Field field) {
        super(field);
    }

    public boolean canAddMiniRoom(MiniRoomType miniRoomType, int x, int y) {
        final Rect rect = getRectByMiniRoomType(miniRoomType).translate(x, y);
        return getInsideRect(rect).stream().anyMatch((miniRoom) -> {
            switch (miniRoom.getType()) {
                case OmokRoom, MemoryGameRoom -> {
                    return miniRoomType == MiniRoomType.OmokRoom || miniRoomType == MiniRoomType.MemoryGameRoom;
                }
                case PersonalShop, EntrustedShop -> {
                    return miniRoomType == MiniRoomType.PersonalShop || miniRoomType == MiniRoomType.EntrustedShop;
                }
            }
            return false;
        });
    }

    public void addMiniRoom(MiniRoom miniRoom) {
        miniRoom.setField(field);
        miniRoom.setId(field.getNewObjectId());
        addObject(miniRoom);
    }

    public void removeMiniRoom(MiniRoom miniRoom) {
        removeObject(miniRoom);
    }

    private static Rect getRectByMiniRoomType(MiniRoomType miniRoomType) {
        return switch (miniRoomType) {
            case OmokRoom, MemoryGameRoom -> Rect.of(-90, -60, 90, 60);
            case PersonalShop, EntrustedShop -> Rect.of(-120, -80, 80, 120);
            default -> Rect.of(0, 0, 0, 0);
        };
    }
}
