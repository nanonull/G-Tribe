package conversion7.game.stages.world.landscape;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Timer;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.Area;
import conversion7.game.stages.world.World;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class LandscapeGenerator implements Runnable {

    private static final Logger LOG = Utils.getLoggerForClass();

    private static final Map<Biom.Type, Integer> DEFAULT_BIOM_TYPE_CHANCES = new HashMap<>();
    private static final String BIOM_WIP_AREA_ERROR = "Biom should be set in the current WIP area";

    static {
        DEFAULT_BIOM_TYPE_CHANCES.put(Biom.Type.DIRT, PropertiesLoader.getIntProperty("BIOM_CHANCE.DIRT"));
        DEFAULT_BIOM_TYPE_CHANCES.put(Biom.Type.SAND, PropertiesLoader.getIntProperty("BIOM_CHANCE.SAND"));
        DEFAULT_BIOM_TYPE_CHANCES.put(Biom.Type.STONE, PropertiesLoader.getIntProperty("BIOM_CHANCE.STONE"));
        DEFAULT_BIOM_TYPE_CHANCES.put(Biom.Type.WATER, PropertiesLoader.getIntProperty("BIOM_CHANCE.WATER"));
        DEFAULT_BIOM_TYPE_CHANCES.put(Biom.Type.MOUNTAIN, PropertiesLoader.getIntProperty("BIOM_CHANCE.MOUNTAIN"));
    }

    Area[][] coveredAreas = World.areas;
    public Array<Biom> bioms = new Array<>();

    private boolean completed = false;

    @Override
    public void run() {
        LOG.info(getClass().getSimpleName() + " RUN");
        Timer generationTime = new Timer(LOG);
        setBioms();
        if (LOG.isDebugEnabled()) writeBiomsMapToConsole();
        runBioms();
        generationTime.stop("generation time");
        setCompleted(true);
    }

    private void writeBiomsMapToConsole() {
        Array<Cell> biomCells = PoolManager.ARRAYS_POOL.obtain();
        for (Biom biom : bioms) {
            biomCells.add(biom.startPoint);
        }
        World.drawMap(null, biomCells);
        PoolManager.ARRAYS_POOL.free(biomCells);
    }

    public synchronized void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public synchronized boolean isCompleted() {
        return completed;
    }


    private void runBioms() {
        while (true) {
//            _runBiomsIteration++;
//            if (_runBiomsIteration % 100 == 0) {
//                LOG.info(getThreadName() + " _runBiomsIteration = " + _runBiomsIteration);
//            }

            boolean someBiomStillHaveWork = false;

            for (Biom biom : bioms) {
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
    private void setBioms() {
        for (Area[] coveredArea : coveredAreas) {
            for (int y = 0; y < coveredAreas[0].length; y++) {
                Area area = coveredArea[y];
                Map<Biom.Type, Integer> biomTypeChances = new HashMap<>();
                biomTypeChances.putAll(DEFAULT_BIOM_TYPE_CHANCES);
                int rndX;
                int rndY;
                Cell biomOrigin;

                rndX = Utils.RANDOM.nextInt((int) Area.WIDTH_IN_CELLS_HALF);
                rndY = Utils.RANDOM.nextInt((int) Area.HEIGHT_IN_CELLS_HALF);
                biomOrigin = area.getCell(rndX, rndY);
                assertThat(biomOrigin.getArea()).as(BIOM_WIP_AREA_ERROR).isEqualTo(area);
                area.bioms.add(new Biom(biomOrigin));

                rndX = Utils.RANDOM.nextInt((int) Area.WIDTH_IN_CELLS_HALF);
                rndY = Utils.RANDOM.nextInt((int) Area.HEIGHT_IN_CELLS_HALF) + (int) Area.HEIGHT_IN_CELLS_HALF;
                biomOrigin = area.getCell(rndX, rndY);
                assertThat(biomOrigin.getArea()).as(BIOM_WIP_AREA_ERROR).isEqualTo(area);
                area.bioms.add(new Biom(biomOrigin));

                rndX = Utils.RANDOM.nextInt((int) Area.WIDTH_IN_CELLS_HALF) + (int) Area.WIDTH_IN_CELLS_HALF;
                rndY = Utils.RANDOM.nextInt((int) Area.HEIGHT_IN_CELLS_HALF);
                biomOrigin = area.getCell(rndX, rndY);
                assertThat(biomOrigin.getArea()).as(BIOM_WIP_AREA_ERROR).isEqualTo(area);
                area.bioms.add(new Biom(biomOrigin));

                rndX = Utils.RANDOM.nextInt((int) Area.WIDTH_IN_CELLS_HALF) + (int) Area.WIDTH_IN_CELLS_HALF;
                rndY = Utils.RANDOM.nextInt((int) Area.HEIGHT_IN_CELLS_HALF) + (int) Area.HEIGHT_IN_CELLS_HALF;
                biomOrigin = area.getCell(rndX, rndY);
                assertThat(biomOrigin.getArea()).as(BIOM_WIP_AREA_ERROR).isEqualTo(area);
                area.bioms.add(new Biom(biomOrigin));
            }
        }
    }

}
