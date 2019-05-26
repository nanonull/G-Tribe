package conversion7.game.stages.world.landscape;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.objects.MountainDebris;
import org.slf4j.Logger;

public class Biom {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int RADIUS_LIMIT_BY_AREA_SIZE = MathUtils.simpleMin(Area.WIDTH_IN_CELLS, Area.HEIGHT_IN_CELLS);
    private static final BiomBuilder BIOM_BUILDER = new BiomBuilder();
    public Cell startCell;
    public Type type;
    public int dirtMultiplier;
    public int sandMultiplier;
    public int stoneMultiplier;
    public boolean startPlaceForPlayer;
    public boolean startPlaceForAiHuman;
    public boolean startPlaceForAnimals;
    public boolean faunaGeneratedOn;
    public float distanceToTargetCell;
    Array<Cell> cellsToBeGeneratedOnCircle = new Array<>(false, 16);
    boolean finished = false;
    boolean hadWorkOnCircle;
    private LandscapeBiomsMapLevel1.BiomMapData biomMapData;
    private int radius;

    public Biom(Cell startCell, LandscapeBiomsMapLevel1.BiomMapData biomMapData) {
        this.startCell = startCell;
        this.biomMapData = biomMapData;
        startCell.biomOrigin = this;
        startCell.getArea().world.bioms.add(this);

        if (biomMapData.randomBiom) {
            this.type = generateBiomType();
        } else if (biomMapData.randomMoveable) {
            this.type = generateMoveableBiomType();
        } else if (biomMapData.dirt) {
            this.type = Type.DIRT;
        } else if (biomMapData.sand) {
            this.type = Type.SAND;
        } else if (biomMapData.stone) {
            this.type = Type.STONE;
        } else if (biomMapData.water) {
            this.type = Type.WATER;
        } else if (biomMapData.mountains) {
            this.type = Type.MOUNTAIN;
        } else {
            throw new GdxRuntimeException("Unknown biom landscape!");
        }

        if (type.equals(Type.DIRT)) {
            startCell.getLandscapeController().setDefaultDirtCell();
        } else if (type.equals(Type.SAND)) {
            startCell.getLandscapeController().setDefaultSandCell();
            setDirtMultiplier(-100);
        } else if (type.equals(Type.STONE)) {
            startCell.getLandscapeController().setDefaultStoneCell();
            setDirtMultiplier(-100);
            setSandMultiplier(-75);
        } else if (type.equals(Type.WATER)) {
            startCell.getLandscapeController().setWaterCell();
        } else if (type.equals(Type.MOUNTAIN)) {
            startCell.getLandscapeController().setMountainCell();
            setDirtMultiplier(-100);
            setSandMultiplier(-75);
        } else {
            throw new GdxRuntimeException("unknown biomType: " + type);
        }

        requestCellsOnNextCircle(); // first circle
    }

    public LandscapeBiomsMapLevel1.BiomMapData getBiomMapData() {
        return biomMapData;
    }

    private Type generateMoveableBiomType() {
        switch (MathUtils.random(0, 2)) {
            case 1:
                return Type.SAND;
            case 2:
                return Type.STONE;
            case 0:
            default:
                return Type.DIRT;
        }
    }

    public boolean isGoodPlaceForTeam() {
        return type == Type.DIRT;
    }

    public void setDirtMultiplier(int dirtMultiplier) {
        this.dirtMultiplier = dirtMultiplier;
    }

    public void setSandMultiplier(int sandMultiplier) {
        this.sandMultiplier = sandMultiplier;
    }

    public void setStoneMultiplier(int stoneMultiplier) {
        this.stoneMultiplier = stoneMultiplier;
    }

    public void setType(Type type) {
        this.type = type;
    }

    private Type generateBiomType() {
        BIOM_BUILDER.resetToDefaults();
        BIOM_BUILDER.addDesertChance(startCell.getTemperature());
        return BIOM_BUILDER.getWinnerBiomType();
    }

    private void requestCellsOnNextCircle() {
        hadWorkOnCircle = false;
        radius++;
        Array<Cell> cellsToBeRequested = startCell.getCellsAroundOnRadius(radius);
        if (LOG.isDebugEnabled()) startCell.getArea().world.drawMap(startCell, cellsToBeRequested);

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
                .append("startCell = ").append(startCell);
        return sb.toString();
    }

    public void generateDetails() {
        Array<Cell> cellsAround = startCell.getCellsAround(1, Area.BIOM_R, new Array<>());
        for (Cell cell : cellsAround) {
            if (type == Type.MOUNTAIN) {
                if (cell.getLandscape().hasMountain()) {
                    tryCreateMountDebrisAround(cell);
                }
            }
        }

    }

    private void tryCreateMountDebrisAround(Cell cell) {
        for (Cell adjCell : cell.getCellsAround()) {
            if (adjCell.canContainObject(MountainDebris.class)) {
                if (MathUtils.testPercentChance(20)) {
                    new MountainDebris(adjCell);
                } else {
                    break;
                }
            }

        }
    }

    public void createMountDebrisAround() {
        for (Cell adjCell : startCell.getCellsAround()) {
            if (adjCell.canContainObject(MountainDebris.class)) {
                new MountainDebris(adjCell);
                break;
            }

        }
    }

    public void calculateDistanceTo(Cell cell) {
        distanceToTargetCell = startCell.distanceTo(cell);
    }

    public enum Type {
        DIRT(),
        SAND(),
        STONE(),
        WATER(),
        MOUNTAIN()
    }
}
