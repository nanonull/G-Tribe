package conversion7.game.stages.world;

import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.landscape.Cell;
import org.testng.annotations.Test;
import shared.tests.BaseTests;

import static org.fest.assertions.api.Assertions.assertThat;

public class AreaTest extends BaseTests {
    World world = new World(GdxgConstants.WORLD_SETTINGS_TEST);

    @Test
    public void testGetCell() throws Exception {
        Area area00 = world.getArea(0, 0);
        Area area11 = world.getArea(1, 1);
        Cell cell = area00.getCell(Area.WIDTH_IN_CELLS, Area.HEIGHT_IN_CELLS);
        assertThat(cell.getArea()).isEqualTo(area11);
        assertThat(cell.x).isEqualTo(0);
        assertThat(cell.y).isEqualTo(0);
        assertThat(cell.getWorldPosInCells().x).isEqualTo(Area.WIDTH_IN_CELLS);
        assertThat(cell.getWorldPosInCells().y).isEqualTo(Area.HEIGHT_IN_CELLS);
    }
}