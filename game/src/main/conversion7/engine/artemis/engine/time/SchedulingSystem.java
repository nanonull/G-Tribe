package conversion7.engine.artemis.engine.time;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.UuidEntityManager;
import com.artemis.systems.IntervalEntityProcessingSystem;
import conversion7.engine.artemis.engine.DestroyEntitySystem;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

import java.util.UUID;

public class SchedulingSystem extends IntervalEntityProcessingSystem {
    private static final Logger LOG = Utils.getLoggerForClass();
    public static ComponentMapper<SchedulingComponent> components;
    private static World artemisEngine;
    private static UuidEntityManager artemisUuids;
    private long previousTimeMillis;
    private long millisDelta;

    public SchedulingSystem(float intervalSec) {
        super(Aspect.all(SchedulingComponent.class), intervalSec);
    }

    /** Separate entity */
    public static UUID schedule(String name, int millisDelay, Runnable runnable) {
        return schedule(name, millisDelay, artemisEngine.create(), runnable);
    }

    /** Be careful: by default entity will be removed after scheduling executed */
    public static UUID schedule(String name, int millisDelay, int entity, Runnable runnable) {
        SchedulingComponent schedulingComponent = components.create(entity);
        schedulingComponent.runnable = runnable;
        schedulingComponent.delayTimeMillis = millisDelay;
        schedulingComponent.name = name;
        UUID uuid = artemisUuids.getUuid(artemisEngine.getEntity(entity));
        LOG.debug("schedule {} {} in world {}", schedulingComponent, uuid, artemisEngine);
        return uuid;
    }

    // TODO move to system based on step-system
    public static UUID scheduleOnNextStep(String name, Runnable run) {
        return schedule(name, 0, artemisEngine.create(), run);
    }

    @Override
    protected void processSystem() {
        long currentTimeMillis = System.currentTimeMillis();

        if (previousTimeMillis != 0) {
            millisDelta = currentTimeMillis - previousTimeMillis;
            super.processSystem();
        }

        previousTimeMillis = currentTimeMillis;
    }

    @Override
    protected void process(Entity e) {
        SchedulingComponent schedulingComponent = components.get(e);
        schedulingComponent.collectedTimeMillis += millisDelta;

        if (schedulingComponent.collectedTimeMillis >= schedulingComponent.delayTimeMillis) {
            schedulingComponent.runnable.run();
            if (schedulingComponent.killEntity) {
                DestroyEntitySystem.create(e.getId());
            } else {
                components.remove(e);
            }
        }
    }
}