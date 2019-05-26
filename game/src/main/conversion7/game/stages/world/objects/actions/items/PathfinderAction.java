package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.quest.PathfinderDialog;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.ui.utils.UiUtils;

@Deprecated
public class PathfinderAction extends AbstractSquadAction {
    public Array<Array<Cell>> cells;
    private String environmentDescription;

    public PathfinderAction() {
        super(/*ActionEvaluation.PATHFINDER*/ Group.COMMON);
    }

    public String getEnvironmentDescription() {
        return environmentDescription;
    }

    @Override
    public void begin() {
        updateExecutorParameters();
        fillCellsAround();
        fillDescription();
        showUi();
    }

    private void showUi() {
        new PathfinderDialog(this).start();
    }

    private void fillCellsAround() {
        Cell ownerCell = getSquad().getLastCell();
        Array<Cell> cellsRow;
        cells = new Array<>();

        cellsRow = new Array<>();
        cells.add(cellsRow);
        cellsRow.add(ownerCell.getCell(-1, -1));
        cellsRow.add(ownerCell.getCell(0, -1));
        cellsRow.add(ownerCell.getCell(+1, -1));
        cellsRow = new Array<>();
        cells.add(cellsRow);
        cellsRow.add(ownerCell.getCell(-1, 0));
        cellsRow.add(ownerCell);
        cellsRow.add(ownerCell.getCell(+1, 0));
        cellsRow = new Array<>();
        cells.add(cellsRow);
        cellsRow.add(ownerCell.getCell(-1, +1));
        cellsRow.add(ownerCell.getCell(0, +1));
        cellsRow.add(ownerCell.getCell(+1, +1));
    }

    private void fillDescription() {
        StringBuilder descriptionBuilder = new StringBuilder();
        descriptionBuilder.append("You are at cell [0:0]\n \n")
                .append("         Map:")
                .append("       [+1:+1] \n")
                .append("    [0,+1] [+1:0]\n")
                .append("[-1:+1] [0:0] [+1:-1]\n")
                .append("    [-1:0] [0:-1]\n")
                .append("       [-1:-1]\n")
                .append(" \n");

        descriptionBuilder.append("Cells details:\n \n");
        for (int y = cells.size - 1; y >= 0; y--) {
            Array<Cell> cellsRow = cells.get(y);
            for (int x = 0; x < cellsRow.size; x++) {
                Cell cell = cellsRow.get(x);
                descriptionBuilder.append("Cell[").append(UiUtils.getNumberWithSign(x - 1)).append(":")
                        .append(UiUtils.getNumberWithSign(y - 1)).append("]: ");
                if (cell.hasSquad()) {
                    descriptionBuilder.append(cell.squad.unit.getGameClassName())
                            .append(" unit of '").append(cell.squad.team.getName()).append("' team");
                } else {
                    descriptionBuilder.append("nothing special");
                }
                descriptionBuilder.append("\n \n");
            }
        }
        environmentDescription = descriptionBuilder.toString();
    }

    @Override
    protected String buildDescription() {
        return getName()
                + "\n \nExplore more details about the environment (on cells around).";
    }
}
