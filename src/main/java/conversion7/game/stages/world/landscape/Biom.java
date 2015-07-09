package conversion7.game.stages.world.landscape;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.Area;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.creator.WorldCreator;
import org.slf4j.Logger;

public class Biom {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int RADIUS_LIMIT_BY_AREA_SIZE = MathUtils.simpleMin(Area.WIDTH_IN_CELLS, Area.HEIGHT_IN_CELLS);
    private static final BiomBuilder BIOM_BUILDER = new BiomBuilder();

    public enum Type {
        DIRT(),
        SAND(),
        STONE(),
        WATER(),
        MOUNTAIN()
    }

    public Cell startPoint;
    public Type biomType;

    public int dirtMultiplier;
    public int sandMultiplier;
    public int stoneMultiplier;

    private int radius;

    Array<Cell> cellsToBeGeneratedOnCircle = new Array<>(false, 16);
    boolean finished = false;
    boolean hadWorkOnCircle;

    public void setDirtMultiplier(int dirtMultiplier) {
        this.dirtMultiplier = dirtMultiplier;
    }

    public void setSandMultiplier(int sandMultiplier) {
        this.sandMultiplier = sandMultiplier;
    }

    public void setStoneMultiplier(int stoneMultiplier) {
        this.stoneMultiplier = stoneMultiplier;
    }

    public Biom(Cell startPoint) {
        this.startPoint = startPoint;
        this.biomType = generateBiomType();
        WorldCreator.landscapeGenerator.bioms.add(this);

        if (biomType.equals(Type.DIRT)) {
            startPoint.getLandscapeController().setDefaultDirtCell();
        } else if (biomType.equals(Type.SAND)) {
            startPoint.getLandscapeController().setDefaultSandCell();
            setDirtMultiplier(-100);
        } else if (biomType.equals(Type.STONE)) {
            startPoint.getLandscapeController().setDefaultStoneCell();
            setDirtMultiplier(-100);
            setSandMultiplier(-75);
        } else if (biomType.equals(Type.WATER)) {
            startPoint.getLandscapeController().setWaterCell();
        } else if (biomType.equals(Type.MOUNTAIN)) {
            startPoint.getLandscapeController().setMountainCell();
            setDirtMultiplier(-100);
            setSandMultiplier(-75);
        } else {
            throw new GdxRuntimeException("unknown biomType: " + biomType);
        }

        requestCellsOnNextCircle(); // first circle
    }

    private Type generateBiomType() {
        BIOM_BUILDER.resetToDefaults();
        BIOM_BUILDER.addDesertChance(startPoint.getTemperature());
        return BIOM_BUILDER.geWinnerBiomType();
    }

    private void requestCellsOnNextCircle() {
        hadWorkOnCircle = false;
        radius++;
        Array<Cell> cellsToBeRequested = startPoint.getCellsAroundOnRadius(radius);
        if (LOG.isDebugEnabled()) World.drawMap(startPoint, cellsToBeRequested);

        for (Cell cell : cellsToBeRequested) {
            // TODO add all not-generated cells on radius; check hasBeenGotForGenerating at once before generation
            if (!cell.getLandscapeController().hasBeenGotForGenerating) {
                hadWorkOnCircle = true;
                cellsToBeGeneratedOnCircle.add(cell);
            }
        }
        PoolManager.ARRAYS_POOL.free(cellsToBeRequested);
        cellsToBeGeneratedOnCircle.shuffle();
    }


    /** Returns True if biomStillHaveWork */
    public boolean step() {

        if (cellsToBeGeneratedOnCircle.size > 0) {
            Cell cellToBeProceeded = cellsToBeGeneratedOnCircle.removeIndex(0);
//            conversion7.engine.utils.Timer timer = new Timer();
            cellToBeProceeded.getLandscapeController().generateLandscape(this);
//            TestLandGenerator.increaseStatForCellGenerating(timer.stop());
            return true;
        }

        if (!hadWorkOnCircle || radius == RADIUS_LIMIT_BY_AREA_SIZE) {
            finished = true;
            if (LOG.isDebugEnabled()) LOG.debug(this + " Finished");
            return false;
        } else {
            requestCellsOnNextCircle();
            return true;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName().toUpperCase()).append(": ")
                .append("startPoint = ").append(startPoint);
        return sb.toString();
    }
}
