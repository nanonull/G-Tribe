package conversion7.engine.artemis.engine.time;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.World;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

import java.util.UUID;
import java.util.concurrent.Callable;

public class PollingSystem extends BasePollingSystem<PollingComponent> {
    private static final Logger LOG = Utils.getLoggerForClass();
    private static World artemisEngine;
    public ComponentMapper<PollingComponent> components;

    public PollingSystem() {
        super(Aspect.all(PollingComponent.class));
    }

    @Override
    public ComponentMapper<PollingComponent> getMapper() {
        return components;
    }

    public static PollingComponent schedule(int intervalMillis, Callable<Boolean> callable) {
        return artemisEngine.getSystem(PollingSystem.class).schedulePolling(intervalMillis, callable);
    }

    public static PollingComponent schedule(String name, int intervalMillis, Float stopAfterSeconds, Callable<Boolean> callable) {
        return artemisEngine.getSystem(PollingSystem.class).schedulePolling(name, intervalMillis, stopAfterSeconds, callable);
    }

    public static void waitLastPollingCompleted() {
        artemisEngine.getSystem(PollingSystem.class).waitLastPollingFinished();
    }

    public static void waitPollingCompleted(UUID pollingEntity) {
        artemisEngine.getSystem(PollingSystem.class).waitPollingFinished(pollingEntity);
    }
}
