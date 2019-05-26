package conversion7.engine.artemis.engine.time;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;

public class CoreDeltaCollectorSystem extends IteratingSystem {
    public static ComponentMapper<CoreDeltaCollectorComponent> components;

    public CoreDeltaCollectorSystem() {
        super(Aspect.all(CoreDeltaCollectorComponent.class));
    }

    @Override
    protected void process(int entityId) {
        CoreDeltaCollectorComponent coreDeltaCollectorComponent = components.get(entityId);
        coreDeltaCollectorComponent.delta += world.delta;
    }
}