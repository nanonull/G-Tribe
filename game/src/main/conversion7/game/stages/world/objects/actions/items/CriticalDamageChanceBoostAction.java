package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.unit.effects.items.CriticalDamageChanceBoostEffect;

public class CriticalDamageChanceBoostAction extends AbstractWorldTargetableAction {
    private static final int CHANCE_BOOST = 50;

    public CriticalDamageChanceBoostAction() {
        super(Group.ATTACK);
    }

    public int getDistance() {
        return 1;
    }

    @Override
    public String getActionWorldHint() {
        return "boost critical damage chance for unit";
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.CYAN;
    }

    @Override
    protected String buildDescription() {
        return getName() + "\n \nBoost chance of critical damage for unit: " + CHANCE_BOOST + "%";
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        input.squad.getEffectManager().addEffect(new CriticalDamageChanceBoostEffect(
                CriticalDamageChanceBoostEffect.PERCENT_BOOST_1,
                CriticalDamageChanceBoostEffect.EXPIRES_IN_1));
    }
}
