package conversion7.game.stages.world.climate;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.unit.Unit;
import org.slf4j.Logger;

import java.util.HashSet;

public class Climate {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final int TEMPERATURE_CENTER = 5;
    public static final int TEMPERATURE_RADIUS = 30;
    public static final int TEMPERATURE_MIN = TEMPERATURE_CENTER - TEMPERATURE_RADIUS;
    public static final int TEMPERATURE_MAX = TEMPERATURE_CENTER + TEMPERATURE_RADIUS;
    public static final int TEMPERATURE_AMPLITUDE = TEMPERATURE_RADIUS * 2;
    public static final int TEMPERATURE_PER_AREA = TEMPERATURE_AMPLITUDE * 2 / GdxgConstants.HEIGHT_IN_AREAS;
    public static final float TEMPERATURE_PER_CELL = (float) TEMPERATURE_PER_AREA / (float) Area.HEIGHT_IN_CELLS;
    public static final float TEMPERATURE_AFFECT_FOOD_MAXIMAL_MULTIPLIER = 0.95f;
    public static final int AFFECT_FOOD_FROM_TEMPERATURE = Unit.HEALTHY_TEMPERATURE_MIN + 10;
    public static final int DESERT_MORE_CHANCE_AFTER_TEMPERATURE = TEMPERATURE_MAX
            - Math.round(TEMPERATURE_AMPLITUDE * 0.1f);

    public static final Array<Integer> EXISTING_TEMPERATURES = new Array<>();

    static {
        LOG.info("DESERT_MORE_CHANCE_AFTER_TEMPERATURE {}", DESERT_MORE_CHANCE_AFTER_TEMPERATURE);
    }

    public static void calcExistingTemperatures(World world) {
        HashSet<Integer> temps = new HashSet<>();
        for (int y = 0; y < world.heightInCells; y++) {
            Cell cell = world.getCell(0, y);
            temps.add(cell.getTemperature());
        }

        for (Integer integer : Utils.asSortedList(temps)) {
            EXISTING_TEMPERATURES.add(integer);
        }
    }

}
