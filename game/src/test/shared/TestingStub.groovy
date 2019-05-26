package shared

import com.artemis.AspectSubscriptionManager
import com.artemis.EntitySubscription
import conversion7.engine.ClientApplication
import conversion7.engine.Gdxg
import conversion7.engine.artemis.engine.time.BeforeGameEngineTickSystem
import conversion7.engine.utils.Utils
import conversion7.game.GdxgConstants
import conversion7.game.stages.world.view.AreaViewerAnimationsHelper
import org.slf4j.Logger
import system.TestableClientCore

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

class TestingStub {
    private static final Logger LOG = Utils.getLoggerForClass()
    public static final String TEST_THREAD_NAME = "TEST_THREAD"
    static final boolean TEST_MEMORY_LEAKS = "y".equals(System.getProperty("TEST_MEMORY_LEAKS"))

    static List<WeakReference> refs = []
    // to ensure test fails:
    static List strRefs = []
    static WeakReference worldRef
    static WeakReference worldCellRef
    static WeakReference cellSoilRef

    public static TestableClientCore testTargetApplication
    static TestStuckHandler testStuckHandler

    static TestableClientCore justSetupAppSingletonWithNewTestWorld() {
        Thread.currentThread().setName(TEST_THREAD_NAME)
        TestableClientCore core = buildTestTargetApplication()

        AreaViewerAnimationsHelper.showAnimation = false
        def worldSettings = "y" == System.getProperty("WORLD_SETTINGS_TEST_PRETTY") ?
                GdxgConstants.WORLD_SETTINGS_TEST_PRETTY : GdxgConstants.WORLD_SETTINGS_TEST
        ClientApplication.startGameEngine(worldSettings,
                new GdxgDefaultTestingArtemisBuilder(core))

        if (TEST_MEMORY_LEAKS) {
            testMemoryLeaks()
        }

        core.acquireCoreLock()
        AspectSubscriptionManager asm = core.artemis.getAspectSubscriptionManager()
        EntitySubscription subscription = asm.get(TestingSubscriptionListener.TARGET_ASPECT)
        subscription.addSubscriptionListener(new TestingSubscriptionListener())

        BeforeGameEngineTickSystem.schedule(0, {
            LOG.debug("BeforeGameEngineTickSystem says now is frameId {}", core.frameId)
            return false
        })

        System.gc()
        core.releaseCoreLock()
        return core
    }

    static TestableClientCore buildTestTargetApplication() {
        if (testTargetApplication == null) {
            testTargetApplication = createTestCore()
        } else {
            testTargetApplication.reset()
        }
        return testTargetApplication
    }

    static TestableClientCore createTestCore() {
        LOG.info("\n" +
                "==========================================================================================\n" +
                "\n" +
                "    START 'Prepare Client for Tests'\n" +
                "\n" +
                "==========================================================================================")
        LOG.info("applicationRoot: " + new File("").getAbsolutePath())

        GdxgConstants.DEVELOPER_MODE = true
        GdxgConstants.AREA_OBJECT_AI = false

        testTargetApplication = new TestableClientCore()
        ClientApplication.startLibgdxCoreApp(testTargetApplication)
        handleCoreErrors()
        startStuckHandler()

        LOG.info("\n" +
                "==========================================================================================\n" +
                "\n" +
                "    'Prepare Client for Tests' COMPLETED\n" +
                "\n" +
                "==========================================================================================\n")
        return testTargetApplication
    }

    static def startStuckHandler() {
        if (testStuckHandler == null && BaseGdxgSpec.secondsPerTestLimit != null) {
            testStuckHandler = new TestStuckHandler()
            testStuckHandler.start()
        }
    }

    static void handleCoreErrors() {
        testTargetApplication.flushErrors()
        crashTestsOnCoreErrors(testTargetApplication)
    }

    static void crashTestsOnCoreErrors(TestableClientCore core) {
        if (core.applicationErrorsTotal.size > 0) {
            try {
                throw new AssertionError(core.applicationErrorsTotal.size + " error(s) in core app during test!");
            } finally {
                core.applicationErrorsTotal.clear();
            }
        }
    }

    static def testMemoryLeaks() {
        def world = Gdxg.core.world
        worldRef = buildWeakReference(world);
        worldCellRef = buildWeakReference(world.getCell(0, 0));
        def soil = world.getCell(0, 0).landscape.terrainVertexData.soil
//        strRefs.add(soil)
        cellSoilRef = buildWeakReference(soil);

        println "Test references from previous iterations:"
        for (WeakReference reference : refs) {
            println "$reference"
            assert !reference.isEnqueued()
        }
        refs.add(worldRef)
        refs.add(worldCellRef)
        refs.add(cellSoilRef)
    }

    static WeakReference buildWeakReference(Object o) {
        ReferenceQueue queue = new ReferenceQueue()
        return new WeakReference(o, queue)
    }
}
