package conversion7.game.stages.world.landscape;

import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.World;
import org.testng.annotations.Test;
import shared.tests.BaseTests;

import java.math.BigDecimal;

import static org.fest.assertions.api.Assertions.assertThat;

public class CellTest extends BaseTests {

    World world = new World(GdxgConstants.WORLD_SETTINGS_TEST);

    @Test
    public void testDistanceTo() {
        assertThat(world.getCell(0, 0).distanceTo(world.getCell(1, 0))).isEqualTo(1);
        assertThat(
                new BigDecimal(
                        world.getCell(0, 0).distanceTo(world.getCell(1, 1))).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue()
        ).isEqualTo(1.4f);
    }

}