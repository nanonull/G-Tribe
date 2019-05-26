package conversion7.engine.artemis.commons;

import com.artemis.Aspect;
import com.artemis.AspectSubscriptionManager;
import com.artemis.EntitySubscription;
import conversion7.engine.artemis.engine.DestroyEntityComponent;
import conversion7.engine.artemis.engine.time.SchedulingComponent;

/** Could not store in system classes, because static.init invoked before artemis engine is ready */
public class Aspects {

    public static EntitySubscription destroys;
    public static EntitySubscription schedulings;

    public static void init(AspectSubscriptionManager artemisSubsription) {
        destroys = artemisSubsription
                .get(Aspect.all(DestroyEntityComponent.class));
        schedulings = artemisSubsription
                .get(Aspect.all(SchedulingComponent.class));
    }
}
