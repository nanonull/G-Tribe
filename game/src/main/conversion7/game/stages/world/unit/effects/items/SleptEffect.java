package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class SleptEffect extends AbstractUnitEffect {
    public static final int DURATION = 2;
    public static final int STUN_PERC = StunnedEffect.CAPT_ADD_ON_STUN_PERC;
    public static final String DESC = "Slept unit:" +
            "\n - can not act" +
            "\n - is easier to capture: +" + STUN_PERC + "%" +
            "\n \n" +
            "Effect is removed after unit gets hurt";

    public SleptEffect() {
        super(SleptEffect.class.getSimpleName(), Type.NEGATIVE);
    }

    @Override
    public String getHint() {
        return super.getHint() + " " + tickCounter + "/" + DURATION
                + "\n \n" + DESC;
    }

    @Override
    public String getShortIconName() {
        return "Sleep";
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
