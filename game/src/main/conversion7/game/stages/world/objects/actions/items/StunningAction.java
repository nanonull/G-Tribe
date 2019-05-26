package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.UnitAge;
import conversion7.game.stages.world.unit.effects.items.StunActionCooldownEffect;
import conversion7.game.stages.world.unit.effects.items.StunnedEffect;

public class StunningAction extends AbstractWorldTargetableAction {

    public static final String DESC = "Stunned unit can't act";

    public StunningAction() {
        super(Group.ATTACK);
    }

    @Override
    public String getShortName() {
        return "Stun";
    }

    @Override
    public String getActionWorldHint() {
        return "to stun";
    }

    public int getDistance() {
        return 1;
    }

    public static void addStunOn(AbstractSquad targetSquad) {
        targetSquad.getEffectManager().getOrCreate(StunnedEffect.class).resetTickCounter();
        targetSquad.getActionsController().invalidate();
        targetSquad.validate();
    }

    public static boolean testAge(UnitAge unitAge) {
        return unitAge.getLevel() >= UnitAge.ADULT.getLevel();
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.SCARLET;
    }

    @Override
    public String buildDescription() {
        return getName() + "\n \n" + DESC;
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        AbstractSquad targetSquad = input.squad;
        addStunOn(targetSquad);
        getSquad().getEffectManager().addEffect(new StunActionCooldownEffect());
    }
}
