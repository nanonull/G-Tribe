package conversion7.game.stages.world;

import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.unit.Unit;
import org.slf4j.Logger;

public class Climate {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final int TEMPERATURE_CENTER = 5;
    public static final int TEMPERATURE_RADIUS = 30;
    public static final int TEMPERATURE_MIN = TEMPERATURE_CENTER - TEMPERATURE_RADIUS;
    public static final int TEMPERATURE_MAX = TEMPERATURE_CENTER + TEMPERATURE_RADIUS;
    public static final int TEMPERATURE_AMPLITUDE = TEMPERATURE_RADIUS * 2;
    public static final int TEMPERATURE_PER_AREA = TEMPERATURE_AMPLITUDE * 2 / World.HEIGHT_IN_AREAS;
    public static final float TEMPERATURE_AFFECT_FOOD_MAXIMAL_MULTIPLIER = 0.95f;
    public static final int AFFECT_FOOD_FROM_TEMPERATURE = Unit.HEALTHY_TEMPERATURE_MIN + 10;
    public static final int DESERT_MORE_CHANCE_AFTER_TEMPERATURE = TEMPERATURE_MAX
            - Math.round(TEMPERATURE_AMPLITUDE * 0.1f);

    static {
        LOG.info("DESERT_MORE_CHANCE_AFTER_TEMPERATURE " + DESERT_MORE_CHANCE_AFTER_TEMPERATURE);
    }

}
