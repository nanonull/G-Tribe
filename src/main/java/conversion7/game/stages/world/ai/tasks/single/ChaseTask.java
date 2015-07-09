package conversion7.game.stages.world.ai.tasks.single;

import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.objects.AreaObject;
import org.slf4j.Logger;

public class ChaseTask extends AttackTask {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final int DEFAULT_PRIORITY =
            PropertiesLoader.getIntProperty("AreaObjectTask.Priority.ChaseTask");
    private static final int CHASING_STEPS_LIMIT = 10;

    private int chaseSteps;

    public ChaseTask(AreaObject owner, AreaObject targetObject) {
        super(owner, targetObject, DEFAULT_PRIORITY);
        this.targetObject = targetObject;
    }

    public ChaseTask(AreaObject owner) {
        this(owner, null);
    }

    @Override
    public boolean execute() {
        if (targetObject.isRemovedFromWorld()) {
            LOG.info("targetObject.isRemovedFromWorld");
            cancel();
            return true;
        }

        if (super.execute()) {
            return true;
        } else {
            chaseSteps++;
        }

        if (chaseSteps == CHASING_STEPS_LIMIT) {
            if (LOG.isDebugEnabled()) LOG.debug("stop chasing, owner=" + owner);
            owner.setChasingCancelled(true);
            complete();
            return true;
        }

        return false;
    }


}
