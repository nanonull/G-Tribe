package conversion7.game.stages.world.ai.tasks.single;

import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import org.slf4j.Logger;

public class AttackTask extends FollowTask {

    private static final Logger LOG = Utils.getLoggerForClass();
    public static final int DEFAULT_PRIORITY =
            PropertiesLoader.getIntProperty("AreaObjectTask.Priority.AttackTask");

    public AttackTask(AreaObject owner, AreaObject targetObject, int priority) {
        super(owner, priority);
        this.targetObject = targetObject;
    }

    public AttackTask(AreaObject owner, AreaObject targetObject) {
        this(owner, targetObject, DEFAULT_PRIORITY);
    }

    public AttackTask(AreaObject owner) {
        this(owner, null);
    }

    @Override
    public boolean execute() {
        if (targetObject.isRemovedFromWorld()) {
            LOG.info("targetObject.isRemovedFromWorld");
            cancel();
            return true;
        }

        if (!owner.isNeighborOf(targetObject)) {
            followStep();
        } else {
            owner.attack(targetObject);
            complete();
            return true;
        }
        return false;
    }

    /** Need object to attack. */
    @Override
    public boolean couldAcceptInput(Cell input) {
        if (super.couldAcceptInput(input)) {
            return input.getSeizedBy().getTeam() != World.getAreaViewer().selectedObject.getTeam();
        } else {
            return false;
        }
    }

}
