package conversion7.game.stages.world.unit;

public enum UnitAge {
    YOUNG(0, 3), ADULT(1, 7), MATURE(2, 15), OLD(3, 20);

    private int level;
    private int endsAtAgeStep;

    UnitAge(int level, int endsAtAgeStep) {
        this.level = level;
        this.endsAtAgeStep = endsAtAgeStep;
    }

    public int getLevel() {
        return level;
    }

    public int getEndsAtAgeStep() {
        return endsAtAgeStep;
    }

    public static String getLevelLabelUi(int level) {
        UnitAge found = null;
        for (UnitAge age : values()) {
            if (age.level == level) {
                found = age;
                break;
            }
        }
        return found.name() + " (" + (level + 1) + ")";
    }

    public static String getName(int level) {
        UnitAge found = null;
        for (UnitAge age : values()) {
            if (age.level == level) {
                found = age;
                break;
            }
        }
        return found.name();
    }

    public static UnitAge get(int level) {
        for (UnitAge age : values()) {
            if (age.level == level) {
                return age;
            }
        }
        return null;
    }
}
