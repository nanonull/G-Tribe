package conversion7.engine.utils;

import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import org.slf4j.Logger;
import org.testng.Assert;

/** For strict control of unsafe work with collections between start-end methods: reordering/removing/inserting */
public class AbstractIterationRegistrator {

    private static final Logger LOG = Utils.getLoggerForClass();

    private boolean started;
    private StackTraceElement[] startPlace;
    private AbstractSquad startedOnSquad;

    public void start(AbstractSquad squad) {
        assertNotStarted(squad);
        this.startedOnSquad = squad;
        started = true;
        startPlace = Thread.currentThread().getStackTrace();
    }

    public void assertNotStarted(AbstractSquad newSquad) {
        if (started) {
            if (startedOnSquad != newSquad) {
                LOG.error("Array iterations started on different objects!");
            }

            StringBuilder stringBuilder = new StringBuilder("Attempt to start array iteration on ").append(newSquad).append("\n")
                    .append("but it's already started at:\n")
                    .append(startedOnSquad).append("\nat:\n");
            for (StackTraceElement stackTraceElement : startPlace) {
                stringBuilder.append(stackTraceElement).append("\n");
            }
            throw new GdxRuntimeException("Array iteration already started! (place where started is shown below)", new Throwable(stringBuilder.toString()));
        }
    }

    public void end() {
        Assert.assertTrue(started);
        started = false;
    }

    /** Mark to known that current iteration is safe iteration */
    public void safeIteration() {
    }

    public void reset() {
        started = false;
        startPlace = null;
    }
}
