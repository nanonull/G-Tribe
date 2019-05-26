package shared.steps;

import conversion7.aop.TestSteps;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;
import system.break_point_steps_core.AbstractSteps;

@TestSteps
public class BaseSteps extends AbstractSteps {
    private static final Logger LOG = Utils.getLoggerForClass();

    @Override
    protected void resumeTargetCore() {
//        TestingStub.checkAndResetGameThreadCrash();
        super.resumeTargetCore();
    }
}
