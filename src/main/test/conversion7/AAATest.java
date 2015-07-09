package conversion7;

import conversion7.engine.utils.Utils;
import conversion7.game.utils.collections.IterationRegistrators;
import org.slf4j.Logger;

public abstract class AAATest {

    protected static final Logger LOG = Utils.getLoggerForClass();

    private String methodName;
    private int actCounter;
    private int assertCounter;

    public abstract void body();

    public void tearDown() {

    }

    public void run() {
        methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        LOG.info("###################################################################");
        LOG.info("TEST started: " + methodName);
        LOG.info("======================================================== ARRANGE ");
        try {
            beforeEachGameTest();
            body();
        } finally {
            try {
                tearDown();

            } finally {
                AbstractTests.checkAndResetGameThreadCrash();
            }
        }
        LOG.info("test COMPLETED: " + methodName);
    }

    private void beforeEachGameTest() {
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.reset();
    }

    public void actSection() {
        actCounter++;
        LOG.info("======================================================== ACT " + actCounter);
    }

    public void assertSection() {
        assertCounter++;
        LOG.info("======================================================== ASSERT " + assertCounter);
    }
}
