package conversion7.engine.artemis.engine.time;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.managers.UuidEntityManager;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public class IntervalExecutorSystem extends SelfIntervalEntityExecutorSystem {
    private static final Logger LOG = Utils.getLoggerForClass();
    private static ComponentMapper<IntervalExecutorComponent> components;
    private static World artemisEngine;
    private static UuidEntityManager artemisUuids;

    public IntervalExecutorSystem(float intervalSec) {
        super(Aspect.all(IntervalExecutorComponent.class), intervalSec);
    }

    public ComponentMapper<? extends IntervalExecutorComponent> getComponents() {
        return components;
    }

    /** Separate entity */
    public static IntervalExecutorComponent schedule(int intervalMillis, Runnable runnable) {
        return schedule(intervalMillis, artemisEngine.create(), runnable);
    }

    public static IntervalExecutorComponent schedule(int intervalMillis, int entity, Runnable runnable) {
        IntervalExecutorComponent schedulingComponent = components.create(entity);
        schedulingComponent.runnable = runnable;
        schedulingComponent.intervalMillis = intervalMillis;
        schedulingComponent.entityUuid = artemisUuids.getUuid(
                artemisEngine.getEntity(entity));
        return schedulingComponent;
    }

}