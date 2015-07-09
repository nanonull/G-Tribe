package conversion7.tests_standalone.misc;

import com.badlogic.gdx.utils.Pools;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.utils.Timer;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

/** new Object faster in 5-10 times from Pools.get */
public class TestPoolAndNewInstance {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static void run_SceneGroup3d() {
        LOG.info("run_SceneGroup3d");

        Timer timer = new Timer();
        for (int i = 0; i < 1000000; i++) {
            SceneGroup3d object = new SceneGroup3d();
        }
        timer.stop("new Object");

        Timer timer2 = new Timer();
        for (int i = 0; i < 1000000; i++) {
            SceneGroup3d object = Pools.obtain(SceneGroup3d.class);
            Pools.free(object);
        }
        timer2.stop("Pools");
    }

    /**
     * Results:
     * 2014-11-26 07:01:01 INFO  Timer:39 - Timer stopped at 5208283 nanoseconds. Message: 'new Object'
     * 2014-11-26 07:01:01 INFO  Timer:39 - Timer stopped at 50335574 nanoseconds. Message: 'Pools'
     */
    public static void run1() {
        LOG.info("run1");

        Timer timer = new Timer();
        for (int i = 0; i < 1000000; i++) {
            Object object = new Object();
        }
        timer.stop("new Object");

        Timer timer2 = new Timer();
        for (int i = 0; i < 1000000; i++) {
            Object object = Pools.obtain(Object.class);
            Pools.free(object);
        }
        timer2.stop("Pools");
    }

    /**
     * 2014-11-26 07:03:14 INFO  Timer:39 - Timer stopped at 67620744 nanoseconds. Message: 'Pools'
     * 2014-11-26 07:03:14 INFO  Timer:39 - Timer stopped at 7559 nanoseconds. Message: 'new Object'
     */
    public static void run2() {
        LOG.info("run2");

        Timer timer2 = new Timer();
        for (int i = 0; i < 1000000; i++) {
            Object object = Pools.obtain(Object.class);
            Pools.free(object);
        }
        timer2.stop("Pools");

        Timer timer = new Timer();
        for (int i = 0; i < 1000000; i++) {
            Object object = new Object();
        }
        timer.stop("new Object");
    }

    /**
     * 2014-11-26 07:05:11 INFO  Timer:39 - Timer stopped at 4579790 nanoseconds. Message: 'new Object'
     * 2014-11-26 07:05:11 INFO  Timer:39 - Timer stopped at 25093276 nanoseconds. Message: 'Pools'
     */
    public static void runNoFree() {
        LOG.info("runNoFree");

        Timer timer = new Timer();
        for (int i = 0; i < 1000000; i++) {
            Object object = new Object();
        }
        timer.stop("new Object");

        Timer timer2 = new Timer();
        for (int i = 0; i < 1000000; i++) {
            Object object = Pools.obtain(Object.class);
        }
        timer2.stop("Pools");
    }

}
