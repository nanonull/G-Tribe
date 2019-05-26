package conversion7.game.stages.world;

import com.badlogic.gdx.utils.Array;

public enum WorldTrend {
    EXP_PLUS("Gathering exp x" + Mappings.EXP_PLUS_MLT, true),
    EXP_MINUS("Gathering exp x" + Mappings.EXP_MINUS_MLT, false),
    ANIMAL_POWER_MINUS("Animal power on spawn x" + Mappings.ANIMAL_POWER_MINUS_MLT, true),
    ANIMAL_POWER_PLUS("Animal power on spawn x" + Mappings.ANIMAL_POWER_PLUS_MLT, false),
    ANIMAL_HIDE("Animal hide", false),;
    private final String desc;
    private final boolean good;

    WorldTrend(String desc, boolean good) {
        this.desc = desc;
        this.good = good;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isGood() {
        return good;
    }

    public static class Mappings {
        public static final Array<WorldTrend> GOOD_TRENDS = new Array<>();
        public static final Array<WorldTrend> BAD_TRENDS = new Array<>();
        public static final float EXP_PLUS_MLT = 2;
        public static final float EXP_MINUS_MLT = 0.5f;
        public static final float ANIMAL_POWER_MINUS_MLT = 0.8f;
        public static final float ANIMAL_POWER_PLUS_MLT = 1.5f;

        static {
            for (WorldTrend worldTrend : WorldTrend.values()) {
                if (worldTrend.good) {
                    Mappings.GOOD_TRENDS.add(worldTrend);
                } else {
                    Mappings.BAD_TRENDS.add(worldTrend);
                }
            }
        }
    }
}
