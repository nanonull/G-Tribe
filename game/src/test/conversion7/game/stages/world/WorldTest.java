package conversion7.game.stages.world;

import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.landscape.Cell;
import groovy.util.GroovyTestCase;
import org.slf4j.Logger;
import org.testng.annotations.Test;
import shared.TestingStub;
import shared.asserts.WorldAsserts;

import static org.fest.assertions.api.Assertions.assertThat;

public class WorldTest extends GroovyTestCase {

    private static final Logger LOG = Utils.getLoggerForClass();

    World world;

    @Override
    protected void setUp() throws Exception {
        TestingStub.justSetupAppSingletonWithNewTestWorld();
        world = Gdxg.core.world;
    }

    @Test
    public void testGetCellByGameCoordinate() throws Exception {
        Area areaLast = world.getArea(GdxgConstants.WIDTH_IN_AREAS - 1, GdxgConstants.HEIGHT_IN_AREAS - 1);
        Cell cell = world.getCellByGameCoordinate(-0.1f, -0.1f);
        assertThat(cell.getArea()).isEqualTo(areaLast);
        assertThat(cell.x).isEqualTo(Area.WIDTH_IN_CELLS - 1);
        assertThat(cell.y).isEqualTo(Area.HEIGHT_IN_CELLS - 1);
        assertThat(cell.getWorldPosInCells().x).isEqualTo(world.widthInCells - 1);
        assertThat(cell.getWorldPosInCells().y).isEqualTo(world.heightInCells - 1);
    }

    @Test
    public void testGetCell() throws Exception {
        Area area11 = world.getArea(1, 1);
        Cell cell = world.getCell(Area.WIDTH_IN_CELLS, Area.HEIGHT_IN_CELLS);
        assertThat(cell.getArea()).isEqualTo(area11);
        assertThat(cell.x).isEqualTo(0);
        assertThat(cell.y).isEqualTo(0);
        assertThat(cell.getWorldPosInCells().x).isEqualTo(Area.WIDTH_IN_CELLS);
        assertThat(cell.getWorldPosInCells().y).isEqualTo(Area.HEIGHT_IN_CELLS);
    }

    @Test
    public void testGetLoopedWorldCoord() throws Exception {
        for (int x = 0; x > -Gdxg.core.world.widthInCells * 2 - 5; x--) {
            if (x == 0) {
                LOG.info("---");
            }
            LOG.info("x " + x);
            World.getLoopedCoord(x, Gdxg.core.world.widthInCells);
        }

        // positive coords
        assertThat(World.getLoopedCoord(0, Gdxg.core.world.widthInCells))
                .isEqualTo(0);
        assertThat(World.getLoopedCoord(1, Gdxg.core.world.widthInCells))
                .isEqualTo(1);
        assertThat(World.getLoopedCoord(Gdxg.core.world.widthInCells - 1, Gdxg.core.world.widthInCells))
                .isEqualTo(Gdxg.core.world.widthInCells - 1);
        assertThat(World.getLoopedCoord(Gdxg.core.world.widthInCells, Gdxg.core.world.widthInCells))
                .isEqualTo(0);
        assertThat(World.getLoopedCoord(Gdxg.core.world.widthInCells * 2 - 1, Gdxg.core.world.widthInCells))
                .isEqualTo(Gdxg.core.world.widthInCells - 1);

        // negative coords
        assertThat(World.getLoopedCoord(-0.5f, Gdxg.core.world.widthInCells))
                .isEqualTo(Gdxg.core.world.widthInCells - 1);
        assertThat(World.getLoopedCoord(-1, Gdxg.core.world.widthInCells))
                .isEqualTo(Gdxg.core.world.widthInCells - 1);

        assertThat(World.getLoopedCoord(-Gdxg.core.world.widthInCells, Gdxg.core.world.widthInCells))
                .isEqualTo(0);
        assertThat(World.getLoopedCoord(-Gdxg.core.world.widthInCells + 1, Gdxg.core.world.widthInCells))
                .isEqualTo(1);
        assertThat(World.getLoopedCoord(-Gdxg.core.world.widthInCells - 1, Gdxg.core.world.widthInCells))
                .isEqualTo(Gdxg.core.world.widthInCells - 1);
        assertThat(World.getLoopedCoord(-Gdxg.core.world.widthInCells * 2, Gdxg.core.world.widthInCells))
                .isEqualTo(0);
    }

    @Test(invocationCount = 1)
    public void testWorldGetArea() {
        Area area;
        area = Gdxg.core.world.getArea(0, 0);
        WorldAsserts.assertAreaHasAreaCoords(area, 0, 0);

        area = Gdxg.core.world.getArea(GdxgConstants.WIDTH_IN_AREAS, GdxgConstants.HEIGHT_IN_AREAS);
        WorldAsserts.assertAreaHasAreaCoords(area, 0, 0);

        area = Gdxg.core.world.getArea(GdxgConstants.WIDTH_IN_AREAS * 2 - 1, GdxgConstants.HEIGHT_IN_AREAS * 2 - 1);
        WorldAsserts.assertAreaHasAreaCoords(area, GdxgConstants.WIDTH_IN_AREAS - 1, GdxgConstants.HEIGHT_IN_AREAS - 1);

        area = Gdxg.core.world.getArea(GdxgConstants.WIDTH_IN_AREAS - 1, GdxgConstants.HEIGHT_IN_AREAS - 1);
        WorldAsserts.assertAreaHasAreaCoords(area, GdxgConstants.WIDTH_IN_AREAS - 1, GdxgConstants.HEIGHT_IN_AREAS - 1);

        area = Gdxg.core.world.getArea(-1, -1);
        WorldAsserts.assertAreaHasAreaCoords(area, GdxgConstants.WIDTH_IN_AREAS - 1, GdxgConstants.HEIGHT_IN_AREAS - 1);

        area = Gdxg.core.world.getArea(-GdxgConstants.WIDTH_IN_AREAS, -GdxgConstants.HEIGHT_IN_AREAS);
        WorldAsserts.assertAreaHasAreaCoords(area, 0, 0);
    }
}