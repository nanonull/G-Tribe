package conversion7.game.unit_classes.humans.theOldest;

import conversion7.game.unit_classes.EVOLUTION_STAGE;
import conversion7.game.unit_classes.TimeFrame;
import conversion7.game.unit_classes.humans.BaseHumanClass;

public class Ardipithecus extends BaseHumanClass {

    // TODO provide for all classes (in standard ?)... static is not so good here, it seams pattern found in team.skills.statics

    public static EVOLUTION_STAGE evolutionStage;
    public static int level;

    /** When this class could be born. Peak of chance on 2/3 of timeframe */
    public static TimeFrame timeFrame;

    static {
        evolutionStage = EVOLUTION_STAGE.THE_OLDEST;
        level = 5;
        timeFrame = new TimeFrame(0);
    }


}
