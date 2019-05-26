package tests.debug;

import org.testng.annotations.Test;
import shared.tests.BaseTests;
import shared.tests.aaa.BaseAAATest;

public class DebugTestsThread extends BaseTests {

    @Test(invocationCount = 1, singleThreaded = true)
    public void test_1() {
        new BaseAAATest() {
            @Override
            public void body() {
                LOG.info("test_1 Run on: {}", Thread.currentThread().getName());
            }
        }.run();
    }

    @Test(invocationCount = 1, singleThreaded = true)
    public void test_2() {
        new BaseAAATest() {
            @Override
            public void body() {
                LOG.info("test_2 Run on: {}", Thread.currentThread().getName());
            }
        }.run();
    }

}
