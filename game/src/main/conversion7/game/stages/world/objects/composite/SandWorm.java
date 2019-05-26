package conversion7.game.stages.world.objects.composite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.ai.global.CompositeAiEvaluator;
import conversion7.game.ai.global.SandWormAiEvalualor;
import conversion7.game.stages.world.FindPath;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.Landscape;
import conversion7.game.stages.world.landscape.PathData;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import org.slf4j.Logger;

import java.util.Iterator;

public class SandWorm extends CompositeAreaObject {

    private static final Logger LOG = Utils.getLoggerForClass();
    /** Each object will have different branches for sure */
    public static CompositeAiEvaluator<SandWorm> aiEval = new SandWormAiEvalualor();
    public boolean deactivationInProgress;
    protected int activeParts;

    public SandWorm() {
        setName(SandWorm.class.getSimpleName());
        addPart();
        addPart();
        addPart();
    }

    public static SandWorm create(Team team) {
        SandWorm sandWorm = new SandWorm();
        team.addComposite(sandWorm);
        sandWorm.initParts();
        return sandWorm;
    }

    public static void wakeUpSandWormIfCan(Cell cellAround) {
        if (cellAround.getLandscape().getSand() > Landscape.DESERT_SAND
                && cellAround.getArea().canHaveWorm()) {
            SandWorm sandWorm = cellAround.getArea().getSandWorm();
            if (sandWorm != null && MathUtils.testPercentChance(25) && sandWorm.canWentFromDeep()) {
                Array<Cell> cellsAround = cellAround.getCellsAround(0, 2, new Array<>());
                cellsAround.shuffle();
                for (Cell cell : cellsAround) {
                    if (cell.canBeSeized()) {
                        sandWorm.placeAt(cell);
                        return;
                    }
                }
            }
        }
    }

    private static void removePartFromWorld(AreaObject object) {
        if (object.cell != null) {
            object.removeObjectFromCell();
            object.seizeCell(null);
        }
        object.getParentCompositeObject().validateView();
    }

    public AreaObject getHead() {
        return getParts().get(0);
    }

    @Override
    public CompositeAiEvaluator getAiEvaluator() {
        return aiEval;
    }

    @Override
    public int getMaxAiAttemptsPerTurn() {
        return AbstractSquad.START_MOVE_AP + 1;
    }

    @Override
    public boolean isActive() {
        return activeParts > 0;
    }

    public void validateView() {
        if (isActive()) {
            for (int pi = 0; pi < activeParts; pi++) {
                AreaObject part = getParts().get(pi);
                part.validateView();
            }
        }
    }

    /** Returns true if target was reached or further path is unavailable */
    public boolean moveOneStepTo(Cell target) {
        Array<PathData> path = FindPath.getPath(getHead().cell, target);
        if (path == null) {
            return true;
        }

        boolean lastStep = false;
        if (path.size == 1) {
            lastStep = true;
        }
        moveOn(path.get(0).cell);
        PoolManager.ARRAYS_POOL.free(path);

        return lastStep;
    }

    public void moveOn(Cell cell) {
        if (!isPartActive(getHead())) {
            LOG.error("head is not active");
            return;
        }

        if (!alive) {
            LOG.error("dead");
            return;
        }
        // move head
        // move bodycell if active
        Cell nextCell = cell;
        Cell prevCell = null;
        for (int pi = 0; pi < activeParts; pi++) {
            AreaObject part = getParts().get(pi);
            prevCell = part.cell;

            part.seizeCell(nextCell);
            if (prevCell != null) {
                part.moveBody(prevCell, nextCell);
            }
            nextCell = prevCell;
        }

        // activate next body cell
        if (activeParts < getParts().size) {
            AreaObject newActivePart = getParts().get(activeParts);
            newActivePart.returnToWorld(nextCell);
            activeParts++;
        }

        validateView();
    }

    @Override
    public void init() {

    }

    @Override
    public void placeAt(Cell startCell) {
        if (!canWentFromDeep()) {
            LOG.error("!canWentFromDeep");
            return;
        }
        AreaObject head = getHead();
        head.returnToWorld(startCell);
        activeParts = 1;
        super.placeAt(startCell);
        startCell.addFloatLabel(SandWorm.class.getSimpleName() + " woke up", Color.ORANGE);
    }

    public boolean canWentFromDeep() {
        return activeParts == 0 && !deactivationInProgress;
    }


    @Override
    public void deactivate() {
        deactivationInProgress = true;
        getHead().getLastCell().addFloatLabel("Worm goes deep", Color.ORANGE);
    }

    public void hideNextPart() {
        int deactivated = 0;
        for (AreaObject object : getParts()) {
            if (isPartActive(object)) {
                removePartFromWorld(object);
                return;
            } else {
                activeParts--;
                deactivated++;
            }
        }

        if (activeParts == 0) {
            deactivationInProgress = false;
        }
    }


    public void addPart() {
        SandWormPart sandWormPart;
        if (getParts().size == 0) {
            sandWormPart = new SandWormHead();
            sandWormPart.addDeathListener(object -> {
                killWorm();
            });
        } else {
            sandWormPart = new SandWormPart();
            sandWormPart.addDeathListener(object -> {
                killPart(sandWormPart);
            });
        }
        getParts().add(sandWormPart);
        sandWormPart.setParentCompositeObject(this);
        sandWormPart.setTeam(team);
    }

    private void killPart(SandWormPart sandWormPart) {
        AreaObject killedBy = getHead().killedBy;
        if (killedBy != null && killedBy.team != null) {
            if (killedBy.isSquad()) {
                killedBy.toSquad().updateExperience(Unit.BASE_EXP_FOR_LEVEL, "Killed worm part");
            } else {
                killedBy.team.updateEvolutionPointsOn(1, "Killed worm part");
            }
        }

        int deadIndex = getParts().indexOf(sandWormPart, true);
        Iterator<AreaObject> iterator = getParts().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            iterator.next();
            if (i >= deadIndex) {
                iterator.remove();
            }
            i++;
        }

        activeParts = deadIndex;
    }

    private void killWorm() {
        AreaObject killedBy = getHead().killedBy;
        if (killedBy != null && killedBy.team != null) {
            if (killedBy.isSquad()) {
                killedBy.toSquad().updateExperience(Unit.BASE_EXP_FOR_LEVEL * 3, "Killed worm head");
            } else {
                killedBy.team.updateEvolutionPointsOn(1, "Killed worm head");
            }
        }
        alive = false;
        team.defeatComposite(this);
    }

    public void initParts() {
        addPart();
        addPart();
        addPart();
    }
}
