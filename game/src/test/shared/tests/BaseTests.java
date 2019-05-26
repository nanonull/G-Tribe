package shared.tests;

import conversion7.engine.ClientApplication;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import org.slf4j.Logger;
import org.testng.annotations.BeforeClass;
import shared.BaseGdxgSpec;
import shared.steps.WorldSteps;
import system.TestableClientCore;
import system.break_point_steps_core.AbstractSteps;
import system.break_point_steps_core.AbstractTests;

import java.io.File;

// Deprecated - tests-classes will extend spock's Specification - BaseGdxgSpec
@Deprecated
public abstract class BaseTests extends AbstractTests {
    private static final Logger LOG = Utils.getLoggerForClass();
    private static boolean clientInitialized;
    protected WorldSteps worldSteps = getSteps(WorldSteps.class);
    private TestableClientCore testableClientCore;

    public TestableClientCore getOrCreateTestableClientCore() {
        if (testableClientCore == null) {
            testableClientCore = new TestableClientCore();
        }
        return testableClientCore;
    }

    // use BaseGdxgSpec.resetGlobalsToDefault
    @Deprecated
    public static void resetGlobalsToDefault() {
        BaseGdxgSpec.resetGlobalsToDefault();
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        if (!clientInitialized) {
            LOG.info("\n" +
                    "==========================================================================================\n" +
                    "\n" +
                    "    START 'Prepare Client for Tests'\n" +
                    "\n" +
                    "==========================================================================================");
            LOG.info("applicationRoot: " + new File("").getAbsolutePath());

            GdxgConstants.DEVELOPER_MODE = true;
            GdxgConstants.AREA_OBJECT_AI = false;

            AbstractSteps.setTestTargetApplication(getOrCreateTestableClientCore());
            ClientApplication.startLibgdxCoreApp(testableClientCore);
//            TestingStub.core = testableClientCore;

            boolean doCrashCoreOnError = true;
            testableClientCore.setSystemExitOnErrorInRender(doCrashCoreOnError);
//            TestingStub.checkAndResetGameThreadCrash();

            LOG.info("\n" +
                    "==========================================================================================\n" +
                    "\n" +
                    "    'Prepare Client for Tests' COMPLETED\n" +
                    "\n" +
                    "==========================================================================================\n");
            clientInitialized = true;
        }
    }

}
