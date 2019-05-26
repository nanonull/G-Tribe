package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractHumanSquadAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;

public class ResurrectAction extends AbstractHumanSquadAction {
    public static final String DESC = "Resurrect ally unit on cell";
    private static final Logger LOG = Utils.getLoggerForClass();

    public ResurrectAction() {
        super(Group.DEFENCE);
    }

    public static boolean hasSquadForResurrect(Cell cell, AbstractSquad resBy) {
        return cell.lastDeadSquad != null
                && resBy.team.isAllyOf(cell.lastDeadSquad.team);
    }

    public static void resurect(AbstractSquad resBy, Cell onCell,
                                AbstractSquad toRes) {
        LOG.info("resurect: {}", toRes);
        onCell.lastDeadSquad = null;

        Team team = resBy.team;
        resBy.team.teamClassesManager.addUnitIfNewcomerInTeam(toRes.unit);
        WorldSquad newSquad = WorldSquad.create(toRes.unit, team, onCell);
        LOG.info("newSquad: {}", newSquad);
    }

    @Override
    public int getDistance() {
        return getSquad().getViewRadius(false);
    }

    @Override
    public Array<Cell> getAcceptableCells() {
        Array<Cell> cells = new Array<>();
        for (Cell cell : super.getAcceptableCells()) {
            if (hasSquadForResurrect(cell, getSquad())
                    && cell.hasFreeMainSlot()) {
                cells.add(cell);
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
        resurect(getSquad(), input, input.lastDeadSquad);
    }
}
