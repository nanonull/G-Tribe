package shared.tests.aaa;

import conversion7.engine.utils.Utils;
import org.slf4j.Logger;
import shared.tests.BaseTests;
import system.aaa.AbstractAAATest;

public abstract class BaseAAATest extends AbstractAAATest {

    protected static final Logger LOG = Utils.getLoggerForClass();

    @Override
    protected void beforeEachTest() {
        BaseTests.resetGlobalsToDefault();
    }

    @Override
    protected void afterTearDown() {
//        TestingStub.checkAndResetGameThreadCrash();
    }
}
