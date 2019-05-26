package conversion7.game.stages.world.ai_deprecated.tasks.single;

import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import org.slf4j.Logger;

public class AnimalChaseTaskOldOld extends AttackTaskOldOld {

    private static final Logger LOG = Utils.getLoggerForClass();

    private static final int CHASING_STEPS_LIMIT = 10;

    private int chaseSteps;

    public AnimalChaseTaskOldOld(AbstractSquad owner) {
        this(owner, null);
    }

    public AnimalChaseTaskOldOld(AbstractSquad owner, AbstractSquad targetObject) {
        super(owner, targetObject);
    }

    /** Returns true if target was attacked OR target is dead. */
    @Override
    public boolean execute() {
        if (targetSquad.isRemovedFromWorld()) {
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
            LOG.info("Stop chasing, owner = {}", owner);
            owner.setChasingCancelled(true);
            cancel();
            return true;
        }

        return false;
    }


}
