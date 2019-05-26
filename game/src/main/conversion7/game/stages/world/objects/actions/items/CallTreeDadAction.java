package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.unit_classes.UnitClassConstants;
import conversion7.game.unit_classes.animals.TreeDad;

public class CallTreeDadAction extends AbstractWorldTargetableAction {

    public static final String DESC = "Call tree unit on your side with power: "
            + UnitClassConstants.CLASS_STANDARDS.get(TreeDad.class).getBasePower();

    public CallTreeDadAction() {
        super(Group.DEFENCE);
    }

    public int getDistance() {
        return 1;
    }

    @Override
    public String getActionWorldHint() {
        return "call tree on";
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.GREEN;
    }

    @Override
    public String buildDescription() {
        return getName() + "\n \n" + DESC;
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        WorldSquad.create(TreeDad.class, getSquad().team, input);
    }

}
