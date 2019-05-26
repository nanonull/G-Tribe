package conversion7.engine.artemis.engine;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public class DisableEntitySystem extends IteratingSystem {
    private static final Logger LOG = Utils.getLoggerForClass();

    public static ComponentMapper<DisableEntityComponent> components;

    public DisableEntitySystem() {
        super(Aspect.all(DisableEntityComponent.class));
    }

    @Override
    protected void process(int entityId) {
        LOG.info("Disable {}", NameManager.getSysName(entityId));
        customDisable();
        components.remove(entityId);
    }

    protected void customDisable() {
//        if (Box2dBodySystem.components.has(entityId)) {
//            Box2dBodyComponent box2dBodyComponent = Box2dBodySystem.components.get(entityId);
//
//            for (Fixture fixture : box2dBodyComponent.body.getFixtureList()) {
//                Filter filter = fixture.getFilterData();
//                filter.categoryBits = Box2dBodySystem.INACTIVE_BODY;
//                filter.maskBits = Box2dBodySystem.NOBODY_MASK;
//                fixture.setFilterData(filter);
//            }
//        }
//
//        if (Actor2dManager.components.has(entityId)) {
//            Actor2dComponent actor2dComponent = Actor2dManager.components.get(entityId);
//            actor2dComponent.previousParent = actor2dComponent.actor.getParent();
//            actor2dComponent.actor.remove();
//        }
    }
}