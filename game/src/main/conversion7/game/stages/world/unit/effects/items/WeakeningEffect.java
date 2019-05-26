package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class WeakeningEffect extends AbstractUnitEffect {
    private static final int DURATION = 2;
    public static final int DMG_MINUS = -1;
    public static final int AP_MINUS = -1;

    public WeakeningEffect() {
        super(WeakeningEffect.class.getSimpleName(), Type.NEGATIVE);
    }

    @Override
    public String getShortIconName() {
        return "Weak";
    }

    @Override
    public String getHint() {
        return super.getHint()
                + "\n \nBase damage " + DMG_MINUS
                + "\nMove action points " + AP_MINUS
                ;
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCounter >= DURATION) {
            remove();
            getOwner().getActionsController().invalidate();
        }
    }
}
