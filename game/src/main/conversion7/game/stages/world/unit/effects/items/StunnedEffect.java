package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class StunnedEffect extends AbstractUnitEffect {
    public static final int DURATION = 2;
    public static final int CAPT_ADD_ON_STUN_PERC = 10;
    public static final String DESC = "Stunned unit:" +
            "\n - can not act" +
            "\n - is easier to capture: +" + CAPT_ADD_ON_STUN_PERC + "%";

    public StunnedEffect() {
        super(StunnedEffect.class.getSimpleName(), Type.NEGATIVE);
    }

    @Override
    public String getHint() {
        return super.getHint() + " " + tickCounter + "/" + DURATION
                + "\n \n" + DESC;
    }

    @Override
    public String getShortIconName() {
        return "Stun";
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCounter == DURATION) {
            remove();
            getOwner().getActionsController().invalidate();
        }
    }
}
