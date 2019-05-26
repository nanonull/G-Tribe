package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class HealActionCooldownEffect extends AbstractUnitEffect {
    public static final int DURATION = 4;

    public HealActionCooldownEffect() {
        super(HealActionCooldownEffect.class.getSimpleName(), Type.POSITIVE_NEGATIVE);
    }

    @Override
    public String getHint() {
        return super.getHint() + " " + tickCounter + "/" + DURATION
                + "\n \nHeal action will be available when effect counter expires";
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
