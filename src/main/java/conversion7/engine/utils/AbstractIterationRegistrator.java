package conversion7.engine.utils;

import com.badlogic.gdx.utils.GdxRuntimeException;
import org.slf4j.Logger;
import org.testng.Assert;

/** For strict control of unsafe work with collections between start-end methods: reordering/removing/inserting */
public class AbstractIterationRegistrator {

    private static final Logger LOG = Utils.getLoggerForClass();

    private boolean started;
    private StackTraceElement[] startPlace;

    public void start() {
        assertNotStarted();
        started = true;
        startPlace = Thread.currentThread().getStackTrace();
    }


    public void end() {
        Assert.assertTrue(started);
        started = false;
    }

    public void assertNotStarted() {
        if (started) {
            StringBuilder stringBuilder = new StringBuilder("Array iteration already started at:\n");
            for (StackTraceElement stackTraceElement : startPlace) {
                stringBuilder.append(stackTraceElement).append("\n");
            }
            throw new GdxRuntimeException("Array iteration already started! (place where started is shown below)", new Throwable(stringBuilder.toString()));
        }
    }

    /** Mark to known that current start-end is safe iteration */
    public void safeIteration() {
    }

    public void reset() {
        started = false;
        startPlace = null;
    }
}
