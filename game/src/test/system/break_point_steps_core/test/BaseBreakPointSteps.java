package system.break_point_steps_core.test;

import conversion7.engine.utils.Utils;
import org.slf4j.Logger;
import org.testng.Assert;
import shared.TestingStub;
import system.break_point_steps_core.AbstractSteps;
import system.break_point_steps_core.PollingBreakPointStep;
import system.break_point_steps_core.Step;

public class BaseBreakPointSteps extends AbstractSteps {

    private static final Logger LOG = Utils.getLoggerForClass();
    public static int simpleStepResult = 0;

    @Step
    public void simpleStepAnnotated() {
        LOG.info("step1 doing");
    }

    public void simpleStepEvenNonAnnotated() {
        LOG.info("step1 doing");
    }

    @Step
    public void superStepWith2Simple() {
        LOG.info("superStepWith2Simple START");
        simpleStep();
        simpleStep2();
        LOG.info("superStepWith2Simple DONE ");
    }

    @Step
    public void simpleStep() {
        LOG.info("simpleStep!");
        LOG.info("Core said: ok, go!");
        Assert.assertFalse(TestingStub.buildTestTargetApplication().isInRender(), "Core said me OK in render!");

        LOG.info("simpleStep doing");
        LOG.info(" exec async !");
        Utils.sleepThread(1000);
        BaseBreakPointSteps.simpleStepResult++;
        LOG.info("SomeSemaphoreSteps.simpleStepResult {}", BaseBreakPointSteps.simpleStepResult);
        LOG.info("simpleStep done");
    }

    @Step
    public void simpleStep2() {
        LOG.info("simpleStep2");
    }

    public void waitForCoreFrames(int expFrameId) {
        LOG.info("waitForCoreFrames {}", expFrameId);
        pollingStep(new PollingBreakPointStep() {
                        int frameCounter;

                        @Override
                        public void run() {
                            LOG.info("waitForCoreFrames polling iteration START");
                            frameCounter++;
                            if (frameCounter == expFrameId) {
                                stepCompleted();
                            }
                            LOG.info("waitForCoreFrames polling iteration DONE ");
                        }
                    }
        );
    }

    @Step
    public void assertStepExecutedNotInCoreRender() {
        Assert.assertFalse(TestingStub.buildTestTargetApplication().isInRender(), "Fix your core application render bounds!");
    }
}
