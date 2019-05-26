package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.Unit;
import org.slf4j.Logger;
import org.testng.Assert;

public class MoveAction extends AbstractWorldTargetableAction {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static final String DESC = "Just move. Just do it.";

    public MoveAction() {
        super(Group.COMMON);
    }

    public int getDistance() {
        return 1;
    }

    @Override
    public String getShortName() {
        return "Move";
    }

    @Override
    public String getActionWorldHint() {
        return "move on";
    }

    public static void move(AbstractSquad squad, Cell onCell) {
        Unit unitWillMove = squad.unit;
        Assert.assertTrue(unitWillMove.squad.canMove());
        if (onCell.getSquad() == null) {
            LOG.info("Squad will be moved \n {}", squad);
            squad.moveOn(onCell);
        } else {
            Assert.fail("could not move on seized cell");
        }
    }

    @Override
    public String buildDescription() {
        return getName() + "\n \n" + DESC;
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        move(getSquad(), input);
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        if (cellAround.hasEnoughResourcesForUnit()) {
            return Color.CYAN;
        } else {
            return Color.ORANGE;
        }
    }
}
