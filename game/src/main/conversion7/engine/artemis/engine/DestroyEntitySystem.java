package conversion7.engine.artemis.engine;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public class DestroyEntitySystem extends IteratingSystem {
    private static final Logger LOG = Utils.getLoggerForClass();
    public static ComponentMapper<DestroyEntityComponent> components;

    public DestroyEntitySystem() {
        super(Aspect.all(DestroyEntityComponent.class));
    }

    public static void create(int id) {
        LOG.debug("create for entity {}", id);
        components.create(id);
    }

    @Override
    protected void process(int entityId) {
        destroyEntity(entityId);
        world.delete(entityId);
    }

    protected void destroyEntity(int entityId) {
        LOG.debug("destroyEntity {}", entityId);
    }
}
