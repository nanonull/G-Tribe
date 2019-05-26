package system.break_point_steps_core;

import com.google.inject.AbstractModule;
import conversion7.break_point_system.BreakPointStorage;
import conversion7.engine.utils.Utils;
import conversion7.game.services.WorldServices;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import shared.TestingStub;
import system.TestableClientCore;

import static org.fest.assertions.api.Assertions.assertThat;

/** Make sure steps are created using {@link AbstractSteps#getSteps(java.lang.Class)} */
public abstract class AbstractSteps extends AbstractModule implements MethodInterceptor {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final BreakPointStorage<StepsBreakKey> BREAK_POINTS = new BreakPointStorage<>();
    private static BreakPointStep openedStep;
    private static BreakPointStep lastClosedStep;

    static {
//        BREAK_POINTS.setWaitCreatedTimeout(Duration.FIVE_SECONDS);
//        BREAK_POINTS.setWaitPointLockedTimeout(Duration.FIVE_SECONDS);
    }

    public BreakPointStep getLastClosedStep() {
        return lastClosedStep;
    }

    protected BreakPointStep getOpenedStep() {
        return openedStep;
    }

    protected static void setOpenedStep(BreakPointStep step) {
        openedStep = step;
    }

    @Deprecated
    public static void setTestTargetApplication(TestTargetApplication application) {
        TestingStub.testTargetApplication = (TestableClientCore) application;
    }

    public <C extends AbstractSteps> C getSteps(Class<C> type) {
        return StepsFactory.getSteps(type);
    }

    public void coreStep(Runnable stepBody) {
        assertThat(openedStep).as("Core-step doesn't work inside another test-step ").isNull();
        BREAK_POINTS.lock(StepsBreakKey.CORE_STEP);
        if (TestingStub.testTargetApplication.hasCoreStepScheduled()) {
            throw new RuntimeException("Nested core step is not supported!");
        }
        TestingStub.testTargetApplication.scheduleCoreStep(stepBody);
        BREAK_POINTS.waitUnlocked(StepsBreakKey.CORE_STEP);
    }

    public BreakPointStep pollingStep(PollingBreakPointStep pollingBreakPointStep) {
        BreakPointStep breakPointStep = startStep();

        while (true) {
            pollingBreakPointStep.run();
            if (pollingBreakPointStep.isCompleted()) {
                break;
            }

            BREAK_POINTS.lock(StepsBreakKey.STEP_EXECUTION);
            resumeTargetCore();
            BREAK_POINTS.waitUnlocked(StepsBreakKey.STEP_EXECUTION);
        }

        endStep();
        return breakPointStep;
    }

    /** It guarantees that test code will be executed when core is locked */
    public BreakPointStep startStep() {
        BreakPointStep newStep = new BreakPointStep();
        LOG.info(" === START Step [{}] === ", newStep.getName());
        newStep.setParent(getOpenedStep());
        setOpenedStep(newStep);

        newStep.setStartedOnCoreFrame(TestingStub.buildTestTargetApplication().getCoreFrameId());
        return newStep;
    }

    protected void resumeTargetCore() {
        BREAK_POINTS.unlock(StepsBreakKey.CORE_EXECUTION);
    }

    /** It removes core is lock (if no parent step) */
    public void endStep() {
        if (openedStep == null) {
            throw new RuntimeException("There is no opened step!");
        }
        lastClosedStep = openedStep;
        LOG.info(" === END Step [{}] === ", lastClosedStep.getName());
        lastClosedStep.setCompletedOnCoreFrame(TestingStub.buildTestTargetApplication().getCoreFrameId());

        setOpenedStep(lastClosedStep.getParent());
        if (openedStep == null) {
            resumeTargetCore();
        }
    }

    @Override
    protected void configure() {
//        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Step.class), this);
    }

    /** Run step annotated with @Step */
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        startStep();
        Object proceed = methodInvocation.proceed();
        endStep();
        return proceed;
    }

    public void waitForNextCoreStep() {
        WorldServices.waitForNextCoreStep(null);
    }
}
