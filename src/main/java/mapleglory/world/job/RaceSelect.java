package mapleglory.world.job;

import java.util.Arrays;
import java.util.Optional;

public enum RaceSelect {
    // RACE_SELECT
    RESISTANCE(0, Job.CITIZEN),
    NORMAL(1, Job.BEGINNER), // explorer
    CYGNUS(2, Job.NOBLESSE),
    ARAN(3, Job.ARAN_BEGINNER),
    EVAN(4, Job.EVAN_BEGINNER);

    private final int race;
    private final Job job;

    RaceSelect(int race, Job job) {
        this.race = race;
        this.job = job;
    }

    public final Job getJob() {
        return job;
    }

    public static Optional<RaceSelect> getByRace(int race) {
        return Arrays.stream(values()).filter(j -> j.race == race).findFirst();
    }
}
