package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.objects.actions.items.DiscordAction;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class DiscordEffect extends AbstractUnitEffect {
    public static final float MLT = 1.5f;
    public static final String DESC = "Unit gets more damage when hurt: x" + MLT;

    public DiscordEffect() {
        super(DiscordEffect.class.getSimpleName(), Type.NEGATIVE);
        setTickLogicEvery(DiscordAction.EFFECT_LENGTH);
    }

    @Override
    public String getShortIconName() {
        return "Discord";
    }

    @Override
    protected void tickLogic() {
        remove();
    }

    @Override
    public String getHint() {
        return super.getHint() + "\n \n" + DESC;
    }
}
