package conversion7.tests_standalone.misc;

import conversion7.engine.utils.Timer;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Results:
 * 1) if log.level > DEBUG than speed is ~equal;
 * 2) if log.level >= DEBUG than speed is ~equal, but IDE console output has huge lags;
 * <p/>
 * Assumptions: use IF before debug
 */
public class TestDebugLoggerPerformance {

    private static final Logger LOG = Utils.getLoggerForClass();
    static int repeat = 10000;

    public static void run() {
        Timer timer;
        LOG.info("run");
        List<String> list = new ArrayList<>();

        timer = new Timer();
        for (int i = 0; i < repeat; i++) {
            LOG.debug("msg");
        }
        list.add(timer.stop() + " LOG.debug NO if ");

        timer = new Timer();
        for (int i = 0; i < repeat; i++) {
            if (LOG.isDebugEnabled())
                LOG.debug("msg");
        }
        list.add(timer.stop() + " LOG.debug WITH if ");

        list.add("second cycle");

        timer = new Timer();
        for (int i = 0; i < repeat; i++) {
            LOG.debug("msg");
        }
        list.add(timer.stop() + " LOG.debug NO if ");

        timer = new Timer();
        for (int i = 0; i < repeat; i++) {
            if (LOG.isDebugEnabled())
                LOG.debug("msg");
        }
        list.add(timer.stop() + " LOG.debug WITH if ");

        for (String s : list) {
            LOG.info(s);
        }

    }
}
