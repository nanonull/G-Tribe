package conversion7.game.stages.world.ai.tasks.single;

import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.objects.AreaObject;
import org.slf4j.Logger;

/** Merge two armies into one */
public class MergeTask extends MoveTask {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final int DEFAULT_PRIORITY =
            PropertiesLoader.getIntProperty("AreaObjectTask.Priority.MergeTask");

    private AreaObject mergeWith;

    public MergeTask(AreaObject owner, AreaObject mergeWith) {
        super(owner, null, DEFAULT_PRIORITY);
        this.mergeWith = mergeWith;
    }

    @Override
    protected void initRadius() {
    }

    @Override
    public boolean execute() {
        if (mergeWith.isRemovedFromWorld()) {
            LOG.info("mergeWith.isRemovedFromWorld");
            cancel();
            return true;
        }

        if (owner.getCell().distanceTo(mergeWith.getCell()) > 3) {
            if (LOG.isDebugEnabled()) LOG.debug("mergeWith too far: " + mergeWith);
            cancel();
            return true;
        }

        if (mergeWith.isNeighborOf(owner)) {
            owner.mergeMeInto(mergeWith);
            complete();
            return true;
        }

        if (!hasMoveTarget()) {
            setMoveTarget(mergeWith.getCell());
        }

        moveStep();
        return false;
    }
}
