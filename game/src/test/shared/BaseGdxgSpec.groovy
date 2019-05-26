package shared

import conversion7.engine.CameraController
import conversion7.engine.Gdxg
import conversion7.engine.utils.Utils
import conversion7.game.GdxgConstants
import conversion7.game.services.WorldServices
import conversion7.game.stages.world.view.AreaViewerAnimationsHelper
import org.slf4j.Logger
import shared.steps.BattleSteps
import shared.steps.WorldSteps
import spock.lang.Specification
import system.TestableClientCore
import system.break_point_steps_core.AbstractSteps
import system.break_point_steps_core.StepsBreakKey

import static system.break_point_steps_core.StepsFactory.getSteps

class BaseGdxgSpec extends Specification {
    private static final Logger LOG = Utils.getLoggerForClass(BaseGdxgSpec)
    static Integer freeMemoryLimit
    public static Integer secondsPerTestLimit
    static Calendar lastTestStartedAt

    @Lazy
    static WorldSteps worldSteps = getSteps(WorldSteps.class);
    @Lazy
    static BattleSteps battleSteps = getSteps(BattleSteps.class);
    TestableClientCore core

    static {
        def freeMemoryLimitValue = System.getProperty('freeMemoryLimit')
        if (freeMemoryLimitValue != null) {
            freeMemoryLimit = Integer.valueOf(freeMemoryLimitValue)
        }

        def secondsPerTestLimitValue = System.getProperty('secondsPerTestLimit')
        if (secondsPerTestLimitValue != null) {
            secondsPerTestLimit = Integer.valueOf(secondsPerTestLimitValue)
        }
    }

    /** invoked before test class */
    def setupSpec() {

    }

    /** invoked before every feature method.
     * This setup method is called even if setup @overridden in user test
     * (no need to call super.setup() from @overridden method) */
    def setup() {
        LOG.info("setup - totalMemory: ${Runtime.getRuntime().totalMemory()}")
        LOG.info("setup - freeMemory: ${Runtime.getRuntime().freeMemory()}")
        System.gc()
        checkEnoughMemory()
        core = TestingStub.justSetupAppSingletonWithNewTestWorld()
        core.setGdxAppExitOnErrorInRender(true)
        resetGlobalsToDefault()
        lastTestStartedAt = Calendar.getInstance()
    }

    def checkEnoughMemory() {
        if (freeMemoryLimit != null) {
            long maxMemory = Runtime.getRuntime().maxMemory()
            def totalMemory = Runtime.getRuntime().totalMemory()
            def memDiff = maxMemory - totalMemory
            if (memDiff < freeMemoryLimit) {
                LOG.warn("Exit due to free memory limit: $freeMemoryLimit. " +
                        "When actual: ${memDiff}")
                System.exit(42)
            }
        }
    }

    /** invoked after all feature methods have been invoked.*/
    def cleanupSpec() {

    }

    /** invoked after every feature method.*/
    def cleanup() {
        LOG.info("cleanup - totalMemory: ${Runtime.getRuntime().totalMemory()}")
        LOG.info("cleanup - freeMemory: ${Runtime.getRuntime().freeMemory()}")
        if (core == null) {
            LOG.warn("Exit due to core was not created(usual case when OutOfMemory)")
            System.exit(42)
        }
        if (core.isSystemExitOnErrorInRenderDone() || core.isGdxAppExitOnErrorInRenderDone()) {
            disposeCrashedCore()
        } else {
            TestingStub.handleCoreErrors()
        }
    }

    static void disposeCrashedCore() {
        TestingStub.testTargetApplication = null
        System.gc()
    }

    def static lockCore() {
        Gdxg.core.acquireCoreLock()
    }

    def static releaseCore() {
        Gdxg.core.releaseCoreLock()
    }

    def static releaseCoreAndWaitNextCoreStep() {
        long inFrame = Gdxg.core.frameId
        releaseCore()
        waitForNextCoreStep(inFrame)
    }

    public static void resetGlobalsToDefault() {
        GdxgConstants.DEVELOPER_MODE = true;
        GdxgConstants.AREA_OBJECT_AI = false;
        GdxgConstants.resetFakeResurrectionInBattleFlags();
        GdxgConstants.setAlwaysStealthOnCheck(false);
        GdxgConstants.setAlwaysDontStealthOnCheck(false);
        AbstractSteps.BREAK_POINTS.unlock(StepsBreakKey.CORE_EXECUTION);
        AreaViewerAnimationsHelper.showAnimation = false
        CameraController.CAMERA_MAX_HEIGHT = CameraController.CAMERA_START_HEIGHT * 2;
    }

    /**Not stable in debug mode when target code relates to artemis engine*/
    public static void waitForNextCoreStep() {
        WorldServices.waitForNextCoreStep()
    }

    public static void waitForNextCoreStep(long initialFrame) {
        WorldServices.waitForNextCoreStep(initialFrame)
    }

    void commonCoreStep(Closure closure) {
        lockCore()

        closure.run()

        releaseCore()
        waitForNextCoreStep()
    }

    void sleep() {
        println "=============================================="
        println ""
        println "                   SLEEP"
        println ""
        println "=============================================="
        Utils.infinitySleepThread()
    }

    void sleepThread() {
        Utils.infinitySleepThread()
    }
}
