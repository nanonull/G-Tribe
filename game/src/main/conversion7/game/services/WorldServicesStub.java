package conversion7.game.services;

import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.unit_classes.humans.theOldest.SahelanthropusTchadensis;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitFertilizer2;
import org.slf4j.Logger;

@Deprecated
public class WorldServicesStub {

    private static final Logger LOG = Utils.getLoggerForClass();

    @Deprecated
    public static Unit createSomeHumanUnit() {
        return UnitFertilizer2.createStandardUnit(SahelanthropusTchadensis.class, MathUtils.RANDOM.nextBoolean());
    }

}
