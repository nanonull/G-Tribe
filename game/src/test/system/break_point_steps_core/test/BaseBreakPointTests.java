package system.break_point_steps_core.test;

import conversion7.engine.utils.Utils;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import shared.TestingStub;
import shared.tests.BaseTests;
import system.break_point_steps_core.BreakPointStep;
import system.break_point_steps_core.PollingBreakPointStep;

import static org.fest.assertions.api.Assertions.assertThat;

public class BaseBreakPointTests extends BaseTests {

    private static final Logger LOG = Utils.getLoggerForClass();

    BaseBreakPointSteps baseBreakPointSteps = getSteps(BaseBreakPointSteps.class);

    @Test
    public void testSimpleAnnotatedStep() {
        baseBreakPointSteps.simpleStepAnnotated();
    }

    @Test
    public void testSimpleNonAnnotatedStep() {
        baseBreakPointSteps.simpleStepEvenNonAnnotated();
    }

    @Test
    public void testStartStopStepSyntax() {
        baseBreakPointSteps.startStep();
        Assert.assertFalse(getOrCreateTestableClientCore().isInRender());
        baseBreakPointSteps.endStep();
    }

    @Test
    public void testSimpleStep() {
        int inSimpleStepResult = BaseBreakPointSteps.simpleStepResult;
        baseBreakPointSteps.simpleStep();
        Assert.assertEquals(BaseBreakPointSteps.simpleStepResult, inSimpleStepResult + 1);
        LOG.info("Done");
    }

    @Test
    public void test2SimpleSingleSteps() {
        int inSimpleStepResult = BaseBreakPointSteps.simpleStepResult;
        baseBreakPointSteps.simpleStep();
        Assert.assertEquals(BaseBreakPointSteps.simpleStepResult, inSimpleStepResult + 1);

        baseBreakPointSteps.simpleStep();
        Assert.assertEquals(BaseBreakPointSteps.simpleStepResult, inSimpleStepResult + 2);

    }

    @Test
    public void testStepExecutedNotInCoreRender() {
        baseBreakPointSteps.assertStepExecutedNotInCoreRender();
    }

    @Test
    public void testPollingCoreFramesLocked() {
        PollingBreakPointStep pollingBreakPointStep = new PollingBreakPointStep() {
            @Override
            public void run() {
                // empty
            }
        };
        assertThat(pollingBreakPointStep).isInstanceOf(PollingBreakPointStep.class);

        int waitFrames = 5;
        long inFrameId = TestingStub.buildTestTargetApplication().getFramesInLock();
        LOG.info("inFrameId {}", inFrameId);
        baseBreakPointSteps.waitForCoreFrames(waitFrames);

        Assert.assertEquals(TestingStub.buildTestTargetApplication().getFramesInLock(), inFrameId + waitFrames);
    }

    @Test
    public void testPollingRenderFrame() {
        int waitFrames = 5;
        long inFrameId = TestingStub.buildTestTargetApplication().getCoreFrameId();
        LOG.info("inFrameId {}", inFrameId);
        baseBreakPointSteps.waitForCoreFrames(waitFrames);

        Assert.assertEquals(TestingStub.buildTestTargetApplication().getCoreFrameId(), inFrameId + waitFrames);

    }

    @Test
    public void testCoreFramesSimple() {
        baseBreakPointSteps.simpleStep();
        BreakPointStep breakPointStep = baseBreakPointSteps.getLastClosedStep();
        Utils.sleepThread(1000);
        Assert.assertEquals(breakPointStep.getCompletedOnCoreFrame(), breakPointStep.getStartedOnCoreFrame());
    }

    @Test
    public void testCoreFramesInSuperStepWith2Simple() {
        int preStepCounter = BreakPointStep.stepCounter;
        baseBreakPointSteps.superStepWith2Simple();
        BreakPointStep breakPointStep = baseBreakPointSteps.getLastClosedStep();
        Utils.sleepThread(1000);

        int stepsExecuted = 3;
        Assert.assertEquals(BreakPointStep.stepCounter, preStepCounter + stepsExecuted);
        Assert.assertEquals(breakPointStep.getCompletedOnCoreFrame(), breakPointStep.getStartedOnCoreFrame());
    }

    @Test
    public void test2SimplePollingSteps() {
        int waitFrames = 2;
        long inFrameId = TestingStub.buildTestTargetApplication().getFramesInLock();
        LOG.info("inFrameId {}", inFrameId);
        baseBreakPointSteps.waitForCoreFrames(waitFrames);

        Assert.assertEquals(TestingStub.buildTestTargetApplication().getFramesInLock(), inFrameId + waitFrames);

        inFrameId = TestingStub.buildTestTargetApplication().getFramesInLock();
        LOG.info("inFrameId {}", inFrameId);
        baseBreakPointSteps.waitForCoreFrames(waitFrames);

        Assert.assertEquals(TestingStub.buildTestTargetApplication().getFramesInLock(), inFrameId + waitFrames);
    }
}
