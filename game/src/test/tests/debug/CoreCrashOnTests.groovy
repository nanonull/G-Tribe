package tests.debug

import conversion7.engine.artemis.engine.time.SchedulingSystem
import conversion7.engine.utils.Utils
import shared.BaseGdxgSpec

public class CoreCrashOnTests extends BaseGdxgSpec {

    public void 'test 1 COMMON'() {
        given:
        SchedulingSystem.schedule("scheduleCameraFocusOnPlayerSquad", 250, {
            throw new RuntimeException("FAIL1");
        });
        waitForNextCoreStep()
        Utils.sleepThread(1000)
        assert core.isGdxAppExitOnErrorInRenderDone()
        assert core.applicationErrorsOnCurrentTick.size == 1
    }

    public void 'test 2 COMMON'() {
        given:
        lockCore()
        releaseCore()
        waitForNextCoreStep()
        Utils.sleepThread(1000)
        assert core.applicationErrorsOnCurrentTick.size == 0
        assert !core.isGdxAppExitOnErrorInRenderDone()
    }

}
