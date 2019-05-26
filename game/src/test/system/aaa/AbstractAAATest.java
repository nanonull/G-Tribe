package system.aaa;

import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

@Deprecated
public abstract class AbstractAAATest {

    protected static final Logger LOG = Utils.getLoggerForClass();

    private String testName;
    private int actCounter;
    private int assertCounter;

    public String getTestName() {
        return testName;
    }

    public void run() {
        testName = Thread.currentThread().getStackTrace()[2].getMethodName();
        LOG.info("###################################################################");
        LOG.info("TEST started: " + testName);
        try {
            beforeEachTest();
            LOG.info("======================================================== ARRANGE ");
            body();
        } finally {
            try {
                tearDown();
            } finally {
                afterTearDown();
            }
        }
        LOG.info("test COMPLETED: " + testName);
    }

    protected void beforeEachTest() {

    }

    public abstract void body();

    public void tearDown() {

    }

    protected void afterTearDown() {

    }

    public void actSection() {
        actCounter++;
        LOG.info("======================================================== ACT " + actCounter);
    }

    public void actSection(String msg) {
        actCounter++;
        LOG.info("======================================================== ACT " + actCounter + " >> " + msg);
    }

    public void assertSection() {
        assertCounter++;
        LOG.info("======================================================== ASSERT " + assertCounter);
    }

    public void assertSection(String msg) {
        assertCounter++;
        LOG.info("======================================================== ASSERT " + assertCounter + " >> " + msg);
    }

    public void actAndAssertSection() {
        actAndAssertSection(null);
    }

    public void actAndAssertSection(String msg) {
        actCounter++;
        assertCounter++;
        LOG.info("======================================================== ACT & ASSERT "
                + Math.max(actCounter, assertCounter));
        if (msg != null) {
            LOG.info("======================================================== ACT & ASSERT NAME: " + msg);
        }
    }

}
