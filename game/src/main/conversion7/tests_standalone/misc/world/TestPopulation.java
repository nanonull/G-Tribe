package conversion7.tests_standalone.misc.world;

import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public class TestPopulation {

    private static final Logger LOG = Utils.getLoggerForClass();

    /**
     * TODO minimum is got very stable ~150-300th step
     * maybe because mixed parameters always cut 0.X after dividing
     * UPD: and maybe it's correct case
     */
    public static void test_population() {
//        Unit tc1 = new TestClass1().init(true);
//        tc1.getBaseParams().setTestParams();
//        Unit tc2 = new TestClass2().init(false);
//        tc2.getBaseParams().setTestParams();
//
//
//        int min = tc1.getBaseParams().get(UnitParameterType.HEIGHT);
//        int max = tc1.getBaseParams().get(UnitParameterType.HEIGHT);
//
//        int step = 0;
//        while (true) {
//            LOG.info("# generation " + step);
//            UnitFertilizer2.tryFertilize(tc1, tc2, 100);
//            // male(<) female child
//            tc1 = tc2;
//            tc2 = tc1.getEffectManager().getEffect(ChildbearingEffect.class).getChild();
//
//            if (tc2.getBaseParams().get(UnitParameterType.HEIGHT) > max) {
//                max = tc2.getBaseParams().get(UnitParameterType.HEIGHT);
//            }
//            LOG.info(" max = " + max);
//
//            if (tc2.getBaseParams().get(UnitParameterType.HEIGHT) < min) {
//                min = tc2.getBaseParams().get(UnitParameterType.HEIGHT);
//            }
//            LOG.info(" min = " + min);
//
//            if (step % 100 == 0) {
//                LOG.info(" 100 steps!");
//                LOG.info(" Parameters._negativeDispersion " + UnitParameters._negativeDispersion);
//                LOG.info(" Parameters._positiveDispersion " + UnitParameters._positiveDispersion);
//                LOG.info(" ");
//            }
//
//            step++;
//        }

    }

}
