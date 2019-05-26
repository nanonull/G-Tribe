package conversion7.engine.artemis.engine;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public class EnableEntitySystem extends IteratingSystem {
    private static final Logger LOG = Utils.getLoggerForClass();

    public static ComponentMapper<EnableEntityComponent> components;

    public EnableEntitySystem() {
        super(Aspect.all(EnableEntityComponent.class));
    }

    @Override
    protected void process(int entityId) {
        LOG.info("Enable {}", NameManager.getSysName(entityId));
        customActivation();
        components.remove(entityId);
    }

    protected void customActivation() {
//        if (Box2dBodySystem.components.has(entityId)) {
//            Box2dBodyComponent box2dBodyComponent = Box2dBodySystem.components.get(entityId);
//
//            for (Fixture fixture : box2dBodyComponent.body.getFixtureList()) {
//                Filter filter = fixture.getFilterData();
//                filter.categoryBits = Box2dBodySystem.ACTIVE_BODY;
//                filter.maskBits = Box2dBodySystem.ALL_MASK;
//                fixture.setFilterData(filter);
//            }
//        }
//
//        if (Actor2dManager.components.has(entityId)) {
//            Actor2dComponent actor2dComponent = Actor2dManager.components.get(entityId);
//            actor2dComponent.previousParent.addActor(actor2dComponent.actor);
//        }
    }
}