package conversion7.engine.artemis.engine.time;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.World;

import java.util.UUID;
import java.util.concurrent.Callable;

public class BeforeGameEngineTickSystem extends BasePollingSystem<BeforeGameEngineComponent> {
    public static ComponentMapper<BeforeGameEngineComponent> components;
    private static World artemisEngine;

    public BeforeGameEngineTickSystem() {
        super(Aspect.all(BeforeGameEngineComponent.class));
    }

    @Override
    public ComponentMapper<BeforeGameEngineComponent> getMapper() {
        return components;
    }

    public static BasePollingComponent schedule(int intervalMillis, Callable<Boolean> callable) {
        return artemisEngine.getSystem(BeforeGameEngineTickSystem.class).schedulePolling(intervalMillis, callable);
    }

    public static BasePollingComponent schedule(Callable<Boolean> callable) {
        return schedule(0, callable);
    }

    public static void waitLastPollingCompleted() {
        artemisEngine.getSystem(BeforeGameEngineTickSystem.class).waitLastPollingFinished();
    }

    public static void waitPollingCompleted(UUID pollingEntity) {
        artemisEngine.getSystem(BeforeGameEngineTickSystem.class).waitPollingFinished(pollingEntity);
    }

}