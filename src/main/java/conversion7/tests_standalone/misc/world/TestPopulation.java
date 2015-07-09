package conversion7.tests_standalone.misc.world;

import conversion7.engine.utils.Utils;
import conversion7.game.classes.test.TestClass1;
import conversion7.game.classes.test.TestClass2;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitFertilizer;
import conversion7.game.stages.world.unit.UnitParameters;
import conversion7.game.stages.world.unit.effects.items.Childbearing;
import org.slf4j.Logger;

public class TestPopulation {

    private static final Logger LOG = Utils.getLoggerForClass();

    /**
     * TODO minimum is got very stable ~150-300th step
     * maybe because mixed parameters always cut 0.X after dividing
     * UPD: and maybe it's correct case
     */
    public static void test_population() {
        Unit tc1 = new TestClass1().create(true);
        tc1.getParams().setTestParams();
        Unit tc2 = new TestClass2().create(false);
        tc2.getParams().setTestParams();


        int min = tc1.getParams().getHeight();
        int max = tc1.getParams().getHeight();

        int step = 0;
        while (true) {
            LOG.info("# generation " + step);
            UnitFertilizer.fertilize(tc1, tc2, 100);
            // male(<) female child
            tc1 = tc2;
            tc2 = tc1.getEffectManager().getEffectCasted(Childbearing.class).getChild();

            if (tc2.getParams().getHeight() > max) {
                max = tc2.getParams().getHeight();
            }
            LOG.info(" max = " + max);

            if (tc2.getParams().getHeight() < min) {
                min = tc2.getParams().getHeight();
            }
            LOG.info(" min = " + min);

            if (step % 100 == 0) {
                LOG.info(" 100 steps!");
                LOG.info(" Parameters._negativeDispersion " + UnitParameters._negativeDispersion);
                LOG.info(" Parameters._positiveDispersion " + UnitParameters._positiveDispersion);
                LOG.info(" ");
            }

            step++;
        }

    }

}
