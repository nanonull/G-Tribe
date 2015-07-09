package conversion7.game.stages.world.unit.effects;

public enum EffectTypeId {
    COLD("Cold"),
    HUNGER("Hunger"),
    THIRST("Thirst");

    private final String value;

    EffectTypeId(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
