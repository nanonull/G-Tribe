package conversion7.game.stages.world.landscape;

import com.badlogic.gdx.utils.Array;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.World;
import org.testng.annotations.Test;
import shared.tests.BaseTests;

import static org.fest.assertions.api.Assertions.assertThat;

public class BrezenhamLineTest extends BaseTests {

    World world = new World(GdxgConstants.WORLD_SETTINGS_TEST);

    @Test
    public void testGetLineOfSight1() throws Exception {
        Cell cellFrom = world.getCell(0, 0);
        Cell cellTo = world.getCell(2, 2);
        assertThat(cellTo.getWorldPosInCells().x).isGreaterThan(0);
        assertThat(cellTo.getWorldPosInCells().y).isGreaterThan(0);

        Array<Cell> lineOfSight = BrezenhamLine.getCellsLine(cellTo, cellFrom);
        assertThat(lineOfSight.size).isEqualTo(3);
        assertThat(lineOfSight).contains(
                world.getCell(0, 0),
                world.getCell(1, 1),
                world.getCell(2, 2)
        );
    }

    @Test
    public void testGetLineOfSight2() throws Exception {
        Cell cellFrom = world.getCell(0, 0);
        Cell cellTo = world.getCell(-2, -2);
        assertThat(cellTo.getWorldPosInCells().x).isGreaterThan(0);
        assertThat(cellTo.getWorldPosInCells().y).isGreaterThan(0);

        Array<Cell> lineOfSight = BrezenhamLine.getCellsLine(cellTo, cellFrom);
        assertThat(lineOfSight.size).isEqualTo(3);
        assertThat(lineOfSight).contains(
                world.getCell(0, 0),
                world.getCell(-1, -1),
                world.getCell(-2, -2)
        );

    }
}