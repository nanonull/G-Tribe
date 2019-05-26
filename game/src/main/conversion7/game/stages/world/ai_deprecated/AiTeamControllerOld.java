package conversion7.game.stages.world.ai_deprecated;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.WorldThreadLocalSort;
import conversion7.game.stages.world.ai_deprecated.events.team.AbstractAiTeamEvent;
import conversion7.game.stages.world.ai_deprecated.tasks.single.FindNodeTaskOld;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.CellTotalValueWithNeighborsComparator;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;

import static java.lang.String.format;

public class AiTeamControllerOld {

    public static final float MINIMUM_DISTANCE_BETWEEN_NODES = AiNode.DEFAULT_RADIUS * 2;
    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int OBJECTS_PER_NODE_LIMIT = 10;
    private static final float GOOD_CELL_FOR_NODE_VALUE_LIMIT = 150;
    public static final float GOOD_CELL_FOR_NODE_WITH_AROUND_VALUES_LIMIT = GOOD_CELL_FOR_NODE_VALUE_LIMIT * 8;
    public Team team;
    /** For Ai teams */
    public Array<AiNode> nodes = new Array<>();
    private Array<AbstractAiTeamEvent> events = new Array<>();
    private boolean switchedOnFindNodeAiTask;

    public AiTeamControllerOld(Team team) {
        this.team = team;
    }

    public void addEvent(AbstractAiTeamEvent newEvent) {
        newEvent.setController(this);
        events.add(newEvent);
    }

    public void ai() {
        if (LOG.isDebugEnabled()) LOG.debug("start AI team " + team.getTeamId());
        proceedEvents();

        stepTeamGoals();
        stepNodesGoals();
        if (GdxgConstants.AREA_OBJECT_AI) {
            stepObjectsGoals();
        }
    }

    private void proceedEvents() {
        if (events.size > 0) {
            for (AbstractAiTeamEvent event : events) {
                event.execute();
            }
            events.clear();
        }
    }

    private void stepTeamGoals() {
        if (needsMoreNodes()) {
            if (LOG.isDebugEnabled()) LOG.debug("needsMoreNodes");
            Cell cell = null;
            if (!switchedOnFindNodeAiTask) {
                cell = findGoodNodeFromVisible();
            }

            if (cell == null) {
                for (AbstractSquad army : team.getSquads()) {
                    army.addTaskToWipSet(new FindNodeTaskOld(army));
                }
                switchedOnFindNodeAiTask = true;
            } else {
                if (LOG.isDebugEnabled()) LOG.debug(format("team %d found good node at %s", team.getTeamId(), cell));
                addAiNode(cell);
            }
        } else {
            switchedOnFindNodeAiTask = false;
        }
    }

    private void stepNodesGoals() {
        for (AiNode node : nodes) {
            node.ai();
        }
    }

    protected void stepObjectsGoals() {
        for (AbstractSquad army : team.getSquads()) {
            if (!army.isRemovedFromWorld()) {
                army.ai();
            }
        }
    }

    public boolean needsMoreNodes() {
        return (nodes.size == 0) || ((team.getObjectsAmount() / (float) nodes.size) > OBJECTS_PER_NODE_LIMIT);
    }

    private Cell findGoodNodeFromVisible() {
        if (LOG.isDebugEnabled()) LOG.debug("findGoodNodeFromVisible");
        Cell foundCell = null;
        Array<Cell> allVisibleAndHealthyCells = PoolManager.ARRAYS_POOL.obtain();
        for (AbstractSquad army : team.getSquads()) {
            if (army.isRemovedFromWorld()) {
                continue;
            }
            for (Cell cell : army.getVisibleCellsAround()) {
                if (!allVisibleAndHealthyCells.contains(cell, true)
                        && cell.hasFreeMainSlot()) {
                    allVisibleAndHealthyCells.add(cell);
                }
            }
        }

        // check if any cell from visible has comfort conditions to be a Node
        WorldThreadLocalSort.instance().sort(allVisibleAndHealthyCells, CellTotalValueWithNeighborsComparator.instance());
        for (Cell visibleCell : allVisibleAndHealthyCells) {
            if (visibleCell.isGoodToBeNode()
                    && visibleCell.isFarEnoughFromTeamNodes(this)) {
                foundCell = visibleCell;
                break;
            }
        }

        PoolManager.ARRAYS_POOL.free(allVisibleAndHealthyCells);
        return foundCell;
    }

    public void addAiNode(Cell nodeOrigin) {
        if (LOG.isDebugEnabled()) LOG.debug("add new node at " + nodeOrigin);
        if (!nodeOrigin.hasHealthyTemperature()) {
            Utils.printErrorWithCurrentStacktrace(String.format("nodeOrigin.temperature (%d) < Unit.HEALTHY_TEMPERATURE_MIN", nodeOrigin.getTemperature()));
        }
        nodes.add(new AiNode(nodeOrigin, team));
    }

}
