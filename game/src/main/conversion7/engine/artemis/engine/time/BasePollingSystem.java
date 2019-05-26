package conversion7.engine.artemis.engine.time;

import com.artemis.*;
import com.artemis.managers.UuidEntityManager;
import com.artemis.utils.IntBag;
import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import conversion7.engine.artemis.engine.DestroyEntitySystem;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public abstract class BasePollingSystem<T extends BasePollingComponent> extends BaseEntitySystem {
    private static final Logger LOG = Utils.getLoggerForClass();
    private static World artemisEngine;
    protected float intervalDeltaSec;
    private BasePollingComponent lastScheduledComp;
    /** Better use lastScheduledComp */
    @Deprecated
    private UUID lastScheduledEntity;

    public BasePollingSystem(Aspect.Builder aspect) {
        super(aspect);
    }

    public abstract ComponentMapper<T> getMapper();

    public T schedulePolling(int intervalMillis, Callable<Boolean> callable) {
        return schedulePolling("unnamed", intervalMillis, null, callable);
    }

    public T schedulePolling(String name, int intervalMillis, Float stopAfterSeconds, Callable<Boolean> callable) {
        int entityId = artemisEngine.create();
        T pollingComponent = getMapper().create(entityId);
        pollingComponent.name = name;
        pollingComponent.stopAfterSeconds = stopAfterSeconds;
        pollingComponent.callable = callable;
        pollingComponent.intervalSeconds = intervalMillis / 1000f;
        pollingComponent.entityUuid = artemisEngine.getSystem(UuidEntityManager.class).getUuid(artemisEngine.getEntity(entityId));
        lastScheduledComp = pollingComponent;
        lastScheduledEntity = pollingComponent.entityUuid;
        LOG.debug("{} schedules polling for entity {} - {}", this.getClass().getSimpleName(), entityId, lastScheduledEntity);
        return pollingComponent;
    }

    public void waitPollingFinished(UUID pollingEntity) {
        try {
            Awaitility.await()
                    .pollInterval(1, TimeUnit.MILLISECONDS)
                    .timeout(Duration.FOREVER)
                    .until(() -> {
                        return isPollingCompleted(pollingEntity);
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isPollingCompleted(UUID pollingEntity) {
        Entity entity = artemisEngine.getSystem(UuidEntityManager.class).getEntity(pollingEntity);
        if (entity == null) {
            return true;
        }
        return !getMapper().has(entity) || DestroyEntitySystem.components.has(entity);
    }

    public void waitLastPollingFinished() {
        waitPollingFinished(lastScheduledEntity);
    }

    @Override
    protected final void processSystem() {
        intervalDeltaSec = world.getDelta();
        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();
        for (int i = 0, s = actives.size(); s > i; i++) {
            process(ids[i]);
        }
    }


    protected void process(int e) {
        BasePollingComponent pollingComponent = getMapper().get(e);
        pollingComponent.collectedIntervalSeconds += intervalDeltaSec;
        pollingComponent.collectedTotalSeconds += intervalDeltaSec;

//        if (LOG.isDebugEnabled()) {
//            LOG.debug("entity {}: intervalDeltaSec {} - collectedIntervalSeconds {}"
//                    , e, intervalDeltaSec, pollingComponent.collectedIntervalSeconds);
//        }

        if (pollingComponent.collectedIntervalSeconds >= pollingComponent.intervalSeconds) {
            if (pollingComponent.intervalSeconds == 0) {
                pollingComponent.collectedIntervalSeconds = 0;
            } else {
                pollingComponent.collectedIntervalSeconds -= pollingComponent.intervalSeconds;
            }
            try {
                Boolean res = pollingComponent.callable.call();
                if (res != null && res) {
                    LOG.debug("polling completed for entity {}", e);
                    complete(e, pollingComponent);
                    return;
                }
            } catch (Exception e1) {
                throw new RuntimeException(pollingComponent.toString(), e1);
            }
        } else {
//            LOG.debug("wait for polling interval on entity {}", e);
        }

        if (pollingComponent.stopAfterSeconds != null
                && pollingComponent.collectedTotalSeconds > pollingComponent.stopAfterSeconds) {
            LOG.info("polling cancelled due to [stopAfterSeconds={}] for entity {}", pollingComponent.stopAfterSeconds, e);
            complete(e, pollingComponent);
        }
    }

    private void complete(int entity, BasePollingComponent pollingComponent) {
        if (pollingComponent.postAction != null) {
            pollingComponent.postAction.run();
        }
        DestroyEntitySystem.create(entity);
    }


}
