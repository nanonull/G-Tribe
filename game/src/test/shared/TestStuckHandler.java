package shared;

import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

import java.util.Calendar;

public class TestStuckHandler extends Thread {

    private static final Logger LOG = Utils.getLoggerForClass();
    public static final int MAX_SECONDS_PER_TEST = 30;

    public TestStuckHandler() {
        super(buildRunnable());
        setName(TestStuckHandler.class.getSimpleName() + "-thread");
        setDaemon(true);
    }

    private static Runnable buildRunnable() {
        return () -> {
            LOG.info("Start");
            Calendar stopTestsAt = Calendar.getInstance();
            while (true) {
                Utils.sleepThread(750);

                Calendar testStartedAt = BaseGdxgSpec.getLastTestStartedAt();
                if (testStartedAt == null) {
                    continue;
                }

                Calendar now = Calendar.getInstance();
                stopTestsAt.setTime(testStartedAt.getTime());
                stopTestsAt.add(Calendar.SECOND, BaseGdxgSpec.secondsPerTestLimit);
                if (now.getTime().getTime() >= stopTestsAt.getTime().getTime()) {
                    LOG.warn("Stop test application, considered as stuck - " +
                            "active test took more than " + BaseGdxgSpec.secondsPerTestLimit + " seconds");
                    System.exit(42);
                }
            }
        };
    }
}
