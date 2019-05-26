package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class ScaredEffect extends AbstractUnitEffect {

    public static final float CAPTURE_MLT = 2f;
    public static final String SCARE_TO_CONTROL_HINT =
            "Scared unit is easier to control and capture: chance x" + CAPTURE_MLT;
    public static final String SCARE_FULL_HINT = SCARE_TO_CONTROL_HINT +
            "\nScared unit can't use attack abilities";

    private int durationSteps;

    public ScaredEffect() {
        super(ScaredEffect.class.getSimpleName(), Type.NEGATIVE);
        this.durationSteps = 3;
    }


    @Override
    public String getShortIconName() {
        return "Scare";
    }

    @Override
    public String getHint() {
        return super.getHint() + " lasts for " + tickCounter + "/" + durationSteps
                + "\n \n"
                + SCARE_FULL_HINT;
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCounter == durationSteps) {
            remove();
        }
    }
}
