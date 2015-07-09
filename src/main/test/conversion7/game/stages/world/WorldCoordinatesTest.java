package conversion7.game.stages.world;

import conversion7.AAATest;
import conversion7.AbstractTests;
import conversion7.test_steps.asserts.WorldAsserts;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class WorldCoordinatesTest extends AbstractTests {

    @Test
    public void testGetLoopedWorldCoord() throws Exception {
        new AAATest() {
            @Override
            public void body() {
                for (int x = 0; x > -World.WIDTH_IN_CELLS * 2 - 5; x--) {
                    if (x == 0) {
                        LOG.info("---");
                    }
                    LOG.info("x " + x);
                    World.getLoopedCoord(x, World.WIDTH_IN_CELLS);
                }

                // positive coords
                assertThat(World.getLoopedCoord(0, World.WIDTH_IN_CELLS))
                        .isEqualTo(0);
                assertThat(World.getLoopedCoord(1, World.WIDTH_IN_CELLS))
                        .isEqualTo(1);
                assertThat(World.getLoopedCoord(World.WIDTH_IN_CELLS - 1, World.WIDTH_IN_CELLS))
                        .isEqualTo(World.WIDTH_IN_CELLS - 1);
                assertThat(World.getLoopedCoord(World.WIDTH_IN_CELLS, World.WIDTH_IN_CELLS))
                        .isEqualTo(0);
                assertThat(World.getLoopedCoord(World.WIDTH_IN_CELLS * 2 - 1, World.WIDTH_IN_CELLS))
                        .isEqualTo(World.WIDTH_IN_CELLS - 1);

                // negative coords
                assertThat(World.getLoopedCoord(-0.5f, World.WIDTH_IN_CELLS))
                        .isEqualTo(World.WIDTH_IN_CELLS - 1);
                assertThat(World.getLoopedCoord(-1, World.WIDTH_IN_CELLS))
                        .isEqualTo(World.WIDTH_IN_CELLS - 1);

                assertThat(World.getLoopedCoord(-World.WIDTH_IN_CELLS, World.WIDTH_IN_CELLS))
                        .isEqualTo(0);
                assertThat(World.getLoopedCoord(-World.WIDTH_IN_CELLS + 1, World.WIDTH_IN_CELLS))
                        .isEqualTo(1);
                assertThat(World.getLoopedCoord(-World.WIDTH_IN_CELLS - 1, World.WIDTH_IN_CELLS))
                        .isEqualTo(World.WIDTH_IN_CELLS - 1);
                assertThat(World.getLoopedCoord(-World.WIDTH_IN_CELLS * 2, World.WIDTH_IN_CELLS))
                        .isEqualTo(0);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void testWorldGetArea() {
        new AAATest() {
            @Override
            public void body() {
                Area area;
                area = World.getArea(0, 0);
                WorldAsserts.assertAreaHasAreaCoords(area, 0, 0);

                area = World.getArea(World.WIDTH_IN_AREAS, World.HEIGHT_IN_AREAS);
                WorldAsserts.assertAreaHasAreaCoords(area, 0, 0);

                area = World.getArea(World.WIDTH_IN_AREAS * 2 - 1, World.HEIGHT_IN_AREAS * 2 - 1);
                WorldAsserts.assertAreaHasAreaCoords(area, World.WIDTH_IN_AREAS - 1, World.HEIGHT_IN_AREAS - 1);

                area = World.getArea(World.WIDTH_IN_AREAS - 1, World.HEIGHT_IN_AREAS - 1);
                WorldAsserts.assertAreaHasAreaCoords(area, World.WIDTH_IN_AREAS - 1, World.HEIGHT_IN_AREAS - 1);

                area = World.getArea(-1, -1);
                WorldAsserts.assertAreaHasAreaCoords(area, World.WIDTH_IN_AREAS - 1, World.HEIGHT_IN_AREAS - 1);

                area = World.getArea(-World.WIDTH_IN_AREAS, -World.HEIGHT_IN_AREAS);
                WorldAsserts.assertAreaHasAreaCoords(area, 0, 0);
            }
        }.run();
    }
}