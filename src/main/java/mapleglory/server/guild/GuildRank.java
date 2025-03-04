package mapleglory.server.guild;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GuildRank {
    NONE(0),
    MASTER(1),
    SUBMASTER(2),
    MEMBER1(3),
    MEMBER2(4),
    MEMBER3(5);

    private final int value;

    GuildRank(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static GuildRank fromStringOrInt(Object input) {
        if (input instanceof Integer) {
            return getByValue((Integer) input);
        } else if (input instanceof String) {
            try {
                return GuildRank.valueOf((String) input);
            } catch (IllegalArgumentException e) {
                return NONE; // Default to NONE if input string does not match any enum
            }
        }
        return NONE;
    }

    public static GuildRank getByValue(int value) {
        for (GuildRank rank : values()) {
            if (rank.value == value) {
                return rank;
            }
        }
        return NONE;
    }
}