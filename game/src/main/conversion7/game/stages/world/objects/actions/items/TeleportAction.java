package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class TeleportAction extends AbstractWorldTargetableAction {
    public static final String DESC = "Teleportation to ally";

    public TeleportAction() {
        super(Group.DEFENCE);
    }

    public int getDistance() {
        return getMaxPossibleDistance();
    }


    @Override
    public Array<Cell> getAcceptableCells() {
        Array<Cell> cells = new Array<>();
        for (AbstractSquad squad : getSquad().team.getSquads()) {
            for (Cell cell : squad.getLastCell().getCellsAround()) {
                if (cell.hasFreeMainSlot()) {
                    cells.add(cell);
                }
            }
        }
        return cells;
    }


    @Override
    public String getActionWorldHint() {
        return getName();
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.GREEN;
    }

    @Override
    protected String buildDescription() {
        return getName() + "\n \n" + DESC;
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        MoveAction.move(getSquad(), input);
    }
}
