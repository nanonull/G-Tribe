package conversion7.engine.artemis.engine.time

import conversion7.engine.Gdxg
import conversion7.engine.utils.Utils
import org.slf4j.Logger
import shared.GdxgDefaultTestingArtemisBuilder
import shared.TestingStub

class PollingSystemTest extends GroovyTestCase {
    private static final Logger LOG = Utils.getLoggerForClass(PollingSystemTest)

    @Override
    protected void setUp() throws Exception {
        TestingStub.justSetupAppSingletonWithNewTestWorld()
    }

    void 'test one-tick polling'() {
        Gdxg.core.freeze = true
        Utils.sleepThread(500)
        def frameId1 = Gdxg.core.frameId

        def schedule = PollingSystem.schedule(0, {
            LOG.info("polling on frameId: {}", Gdxg.core.frameId)
            assert frameId1 == Gdxg.core.frameId
            return true
        })

        Gdxg.core.freeze = false
        PollingSystem.waitPollingCompleted(schedule)
    }

    void 'test 2-ticks polling'() {

        Gdxg.core.freeze = true
        Utils.sleepThread(500)
        def frameId1 = Gdxg.core.frameId

        def schedule = PollingSystem.schedule(0, {
            LOG.info("polling on frameId: {}", Gdxg.core.frameId)
            assert Gdxg.core.frameId <= (frameId1 + 1): "must be initial frame or next frame"
            // exit on 2nd tick
            return frameId1 != Gdxg.core.frameId
        })

        Gdxg.core.freeze = false
        PollingSystem.waitPollingCompleted(schedule)
    }

    void 'test polling interval'() {
        Gdxg.core.registerArtemisOdbEngine(new GdxgDefaultTestingArtemisBuilder(Gdxg.core).build())

        Gdxg.core.freeze = true
        Utils.sleepThread(500)
        def frameId1 = Gdxg.core.frameId
        def deltaCollectorEntity = Gdxg.core.nextEntityId()
        CoreDeltaCollectorSystem.components.create(deltaCollectorEntity)

        int delay = 1000
        int delaySec = 1

        def schedule = PollingSystem.schedule(delay, {
            LOG.info("polling on frameId: {}", Gdxg.core.frameId)
            assert Gdxg.core.frameId != frameId1
            assert CoreDeltaCollectorSystem.components.get(deltaCollectorEntity).delta >= delaySec
            return true
        })

        Gdxg.core.freeze = false
        PollingSystem.waitPollingCompleted(schedule)
    }


}