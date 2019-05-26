package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.landscape.BrezenhamLine;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractHumanSquadAction;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.skills.SkillType;
import conversion7.game.unit_classes.UnitClassConstants;

public class BallistaSelfShotAction extends AbstractHumanSquadAction {

    public static final Array<SkillType> SKILLS_REQ = new Array<>();
    public static final int FALL_DMG = UnitClassConstants.BASE_POWER / 3;

    static {
        SKILLS_REQ.add(SkillType.BRAIN);
    }

    private Cell fellOnCell;

    public BallistaSelfShotAction() {
        super(Group.ATTACK);
    }

    @Override
    public String getActionWorldHint() {
        return "to fly on";
    }

    @Override
    public int getDistance() {
        return getSquad().getViewRadius(false) * 2;
    }

    @Override
    public String getShortName() {
        return "BSelf";
    }


    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.SCARLET;
    }

    @Override
    public String buildDescription() {
        return getName() + "\nFly to make damage to your and enemy body" +
                "\nFall damage " + FALL_DMG +
                "\nSkills required: " + SKILLS_REQ;
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        throwOn(input);
    }

    private void throwOn(Cell targCell) {
        Array<Cell> cellsLine = BrezenhamLine.getCellsLine(targCell, getSquad().cell);
        Cell myCell = cellsLine.removeIndex(cellsLine.size - 1);
        for (Cell cell : cellsLine) {
            if (cell.canBeSeized()) {
                fallOnCell(cell);
                break;
            } else {
                Cell couldBeSeizedNeighborCell = cell.getCouldBeSeizedNeighborCell();
                if (couldBeSeizedNeighborCell != null) {
                    if (cell.hasSquad()) {
                        cell.squad.hurtBy(FALL_DMG, getSquad());
                        cell.addFloatLabel("Fall damage", Color.ORANGE);
                    }
                    fallOnCell(couldBeSeizedNeighborCell);
                    break;
                }
            }
        }

        if (fellOnCell == null) {
            myCell.addFloatLabel("Throw was blocked", Color.ORANGE);
        }
    }

    private void fallOnCell(Cell cell) {
        fellOnCell = cell;
        fellOnCell.addFloatLabel("Fall on", Color.ORANGE);
        WorldSquad squad = getSquad();
        if (!squad.hurtBy(FALL_DMG, null) && squad.isAlive()) {
            squad.power.freeNextMove = true;
            squad.moveOn(cell);
        }
    }
}
