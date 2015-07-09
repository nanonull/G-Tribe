package conversion7.engine.ashley;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import conversion7.engine.Gdxg;

public class FluentPollingSystem extends IteratingSystem {

    public FluentPollingSystem() {
        super(Family.all(PollingComponent.class).get());
    }

    @Override
    protected void processEntity(final Entity entity, final float delta) {
        PollingComponent pollingComponent = entity.getComponent(PollingComponent.class);

        pollingComponent.timePassed += delta;
        if (pollingComponent.timePassed >= pollingComponent.getPollingIntervalSeconds()) {
            pollingComponent.timePassed -= pollingComponent.getPollingIntervalSeconds();
            try {
                PollingComponent.Status status = pollingComponent.getCallable().call();
                pollingComponent.setStatus(status);
                if (!status.equals(PollingComponent.Status.ACTIVE)) {
                    entity.remove(PollingComponent.class);
                    Gdxg.ENTITY_SYSTEMS_ENGINE.removeEntity(entity);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error during polling: " + e.getMessage(), e);
            }
        }
    }
}
