package conversion7.game.stages.world.unit;

public enum UnitParameterType {
    /** base power = base strength */
    STRENGTH,
    @Deprecated
    AGILITY,
    @Deprecated
    VITALITY,
    @Deprecated
    HEALTH,
    @Deprecated
    MORAL,
    @Deprecated
    HEIGHT,
    HEALTH_DAMAGE_PER_STEP,
    @Deprecated
    STRENGTH_PERCENT_ADD(STRENGTH),
    @Deprecated
    AGILITY_PERCENT_ADD(AGILITY),
    @Deprecated
    VITALITY_PERCENT_ADD(VITALITY);

    private UnitParameterType paramModifier;

    /**
     * STRENGTH_PERCENT_ADD == 50 means:
     * 100 STR + 50% = 150 STR
     *
     * @param paramModifier refer to #multiplyOnPercent
     */
    UnitParameterType(UnitParameterType paramModifier) {
        this.paramModifier = paramModifier;
    }

    UnitParameterType() {
    }

    public boolean isParamModifier() {
        return paramModifier != null;
    }

    public boolean isParamModifierOf(UnitParameterType otherType) {
        return isParamModifier() && paramModifier.equals(otherType);
    }
}
