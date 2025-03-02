package mapleglory.world.user.data;

public enum FindFriendType {
    // FindFriend
    FindFriend(5),
    FindMoreFriends(7),
    Option3(10);

    private final byte value;
    FindFriendType(int value) {
        this.value = (byte) value;
    }

    public final byte getValue() {
        return value;
    }

    public static FindFriendType getByValue(int value) {
        for (FindFriendType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
