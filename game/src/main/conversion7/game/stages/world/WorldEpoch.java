package conversion7.game.stages.world;

public enum WorldEpoch {
    OLIGOCEN(-20000000), MIOCEN(-5000000), PLIOCEN(-2500000), EARLY_STONE_AGE(-300000), MID_STONE_AGE(-35000), LATE_STONE_AGE(-10000), MESOLITH(-5000), NEOLITH(-3500), BRONZE_AGE(-800), IRON_AGE(1000);

    private int endsAt;

    WorldEpoch(int endsAt) {
        this.endsAt = endsAt;
    }

    public int getEndsAt() {
        return endsAt;
    }

    public static int getMaxId() {
        return values().length - 1;
    }

    public static WorldEpoch getByID(int epochId) {
        WorldEpoch[] values = values();
        int maxId = getMaxId();
        if (epochId > maxId) {
            epochId = maxId;
        }
        return values[epochId];
    }
}
