package shared;

import com.artemis.Aspect;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import conversion7.engine.artemis.engine.DestroyEntityComponent;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

// FIXME investigate
public class TestingSubscriptionListener implements EntitySubscription.SubscriptionListener {
    private static final Logger LOG = Utils.getLoggerForClass();

    public static final Aspect.Builder TARGET_ASPECT = Aspect.all(DestroyEntityComponent.class);

    @Override
    public void inserted(IntBag entities) {
        LOG.debug("inserted (see TestingSubscriptionListener.TARGET_ASPECT):\n{}", entities);
    }

    @Override
    public void removed(IntBag entities) {
        LOG.debug("removed (see TestingSubscriptionListener.TARGET_ASPECT):\n{}", entities);
    }
}
