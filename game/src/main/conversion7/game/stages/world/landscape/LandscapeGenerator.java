package conversion7.game.stages.world.landscape;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Timer;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.area.Area;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class LandscapeGenerator {

    public static final Map<Biom.Type, Integer> DEFAULT_BIOM_TYPE_CHANCES = new HashMap<>();
    public static final String BIOM_WIP_AREA_ERROR = "Biom should be set in the current WIP area";
    private static final Logger LOG = Utils.getLoggerForClass();

    static {
        DEFAULT_BIOM_TYPE_CHANCES.put(Biom.Type.DIRT, PropertiesLoader.getIntProperty("BIOM_CHANCE.DIRT"));
        DEFAULT_BIOM_TYPE_CHANCES.put(Biom.Type.SAND, PropertiesLoader.getIntProperty("BIOM_CHANCE.SAND"));
        DEFAULT_BIOM_TYPE_CHANCES.put(Biom.Type.STONE, PropertiesLoader.getIntProperty("BIOM_CHANCE.STONE"));
        DEFAULT_BIOM_TYPE_CHANCES.put(Biom.Type.WATER, PropertiesLoader.getIntProperty("BIOM_CHANCE.WATER"));
        DEFAULT_BIOM_TYPE_CHANCES.put(Biom.Type.MOUNTAIN, PropertiesLoader.getIntProperty("BIOM_CHANCE.MOUNTAIN"));
    }

    Area[][] coveredAreas;
    private World world;
    private boolean completed = false;

    public LandscapeGenerator(World world) {
        this.world = world;
        coveredAreas = world.areas;
    }

    public synchronized boolean isCompleted() {
        return completed;
    }

    public synchronized void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void run() {
        LOG.info(getClass().getSimpleName() + " RUN");
        Timer generationTime = new Timer(LOG);
        placeBioms();
        if (LOG.isDebugEnabled()) writeBiomsMapToConsole();
        runBioms();
        generationTime.stop("generation time");
        setCompleted(true);
    }

    private void writeBiomsMapToConsole() {
        Array<Cell> biomCells = PoolManager.ARRAYS_POOL.obtain();
        for (Biom biom : world.bioms) {
            biomCells.add(biom.startCell);
        }
        world.drawMap(null, biomCells);
        PoolManager.ARRAYS_POOL.free(biomCells);
    }

    private void runBioms() {
        while (true) {
//            _runBiomsIteration++;
//            if (_runBiomsIteration % 100 == 0) {
//                LOG.info(getThreadName() + " _runBiomsIteration = " + _runBiomsIteration);
//            }

            boolean someBiomStillHaveWork = false;

            for (Biom biom : world.bioms) {
                if (!biom.finished && biom.step()) {
                    someBiomStillHaveWork = true;
                }
            }

            if (!someBiomStillHaveWork) {
                return;
            }
        }
    }


    /** Set 4 bioms on corners of area */
    private void placeBioms() {
        for (int x = 0; x < coveredAreas.length; x++) {
            Area[] coveredArea = coveredAreas[x];
            for (int y = 0; y < coveredAreas[0].length; y++) {
                Area area = coveredArea[y];
                area.placeBioms();
            }
        }
    }

}
