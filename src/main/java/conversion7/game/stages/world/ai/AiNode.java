package conversion7.game.stages.world.ai;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.ai.events.node.AbstractAiNodeEvent;
import conversion7.game.stages.world.ai.events.team.StrangerNearNodeEvent;
import conversion7.game.stages.world.ai.tasks.group.AttackTaskGroup;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;

import static java.lang.String.format;

public class AiNode implements ArtificialIntelligence {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final int DEFAULT_RADIUS = 4;
    public static final int DEFAULT_ITERATION_LENGTH = 10;

    int defenceDevelopmentStep = 0;
    int settlementDevelopmentStep = 0;
    int armyDevelopmentStep = 0;
    int checkStrangersNearStep = 0;

    public int id;
    public Cell origin;
    private Team team;
    public Array<Cell> cellsInNodeArea = PoolManager.ARRAYS_POOL.obtain();
    private Array<AbstractAiNodeEvent> events = new Array<>();

    public AttackTaskGroup defenceTask;

    public AiNode(Cell origin, Team team) {
        this.id = Utils.getNextId();
        this.origin = origin;
        this.team = team;
        cellsInNodeArea = origin.getCellsAroundToRadiusInclusively(DEFAULT_RADIUS);
        cellsInNodeArea.add(origin);
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName())
                .append(" of ").append(team)
                .append(" at ").append(origin).toString();
    }

    public void addEvent(AbstractAiNodeEvent newEvent) {
        newEvent.setNode(this);
        events.add(newEvent);
    }

    private void proceedEvents() {
        if (events.size > 0) {
            for (AbstractAiNodeEvent event : events) {
                event.execute();
            }
            events.clear();
        }
    }

    @Override
    public void ai() {
        proceedEvents();

        defence();

        if (team.couldCreateSettlement()) {
            settlementDevelopment();
        }

        armyDevelopment();

        checkStrangersToClose();
    }

    public void defence() {
        if (defenceTask != null) {
            defenceTask.execute();
        }
    }

    public void settlementDevelopment() {

    }

    public void armyDevelopment() {

    }

    public void checkStrangersToClose() {
        checkStrangersNearStep += DEFAULT_ITERATION_LENGTH;
        Array<AreaObject> strangers = PoolManager.ARRAYS_POOL.obtain();
        if (checkStrangersNearStep >= DEFAULT_ITERATION_LENGTH) {
            checkStrangersNearStep -= DEFAULT_ITERATION_LENGTH;
            for (Cell cell : cellsInNodeArea) {
                if (cell.isSeized() && !cell.isSeizedByTeam(this.team)) {
                    LOG.info(format("team %d is too close to team's %d Node", cell.getSeizedBy().getTeam().getTeamId(), team.getTeamId()));
                    strangers.add(cell.getSeizedBy());
                }
            }
        }

        if (strangers.size > 0) {
            // TODO test if 2 strangers will be in area and one will be killed and then another one
            team.getAiTeamController().addEvent(new StrangerNearNodeEvent(strangers, this));
        } else { // no stranger
            PoolManager.ARRAYS_POOL.free(strangers);
            if (defenceTask != null) { // clear task if exist
                defenceTask.complete();
                defenceTask = null;
            }
        }
    }

    public Cell getRandomFreeCellInNodeArea() {
        Array<Cell> possibleCells = PoolManager.ARRAYS_POOL.obtain();
        for (Cell cell : cellsInNodeArea) {
            if (cell.couldBeSeized()) {
                possibleCells.add(cell);
            }
        }

        Cell foundCell;
        if (possibleCells.size == 0) {
            foundCell = null;
        } else {
            foundCell = possibleCells.get(Utils.RANDOM.nextInt(possibleCells.size));
        }
        PoolManager.ARRAYS_POOL.free(possibleCells);
        return foundCell;
    }

    /** Collect armies, towns or both for this team */
    public <T extends AreaObject> Array<T> getOwnObjectsInNodeArea(Class<T> clazzFilter) {
        Array<T> objects = PoolManager.ARRAYS_POOL.obtain();
        for (Cell cell : cellsInNodeArea) {
            if (cell.isSeized() && cell.isSeizedByTeam(team)
                    && clazzFilter.isInstance(cell.getSeizedBy())) {
                objects.add((T) cell.getSeizedBy());
            }
        }
        return objects;
    }

    public boolean couldSendSupport() {
        return defenceTask == null;
    }
}
