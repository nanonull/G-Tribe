package conversion7.game.stages.world.ai.tasks.single;

import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.interfaces.AreaViewerInputResolver;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import org.slf4j.Logger;

public class FollowTask extends MoveTask implements AreaViewerInputResolver {

    private static final Logger LOG = Utils.getLoggerForClass();
    public static final int DEFAULT_PRIORITY =
            PropertiesLoader.getIntProperty("AreaObjectTask.Priority.FollowTask");

    AreaObject targetObject;

    public FollowTask(AreaObject owner, int defaultPriority) {
        super(owner, null, defaultPriority);
    }

    public FollowTask(AreaObject owner) {
        this(owner, DEFAULT_PRIORITY);
    }

    @Override
    public String getDescription() {
        return new StringBuilder(getClass().getSimpleName()).append(" ")
                .append(targetObject.getCell().x).append(",").append(targetObject.getCell().y).toString();
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
        }
        return false;
    }

    public boolean followStep() {
        setMoveTarget(targetObject.getCell());
        return moveStep();
    }

    public void setTarget(AreaObject target) {
        this.targetObject = target;
    }

    /** Need object to follow. */
    @Override
    public boolean couldAcceptInput(Cell input) {
        return input.isSeized() && !input.isSeizedBy(World.getAreaViewer().selectedObject);
    }

    @Override
    public void handleInput(Cell input) {
        setTarget(input.getSeizedBy());
    }

    @Override
    public void onInputHandled() {
        World.getAreaViewer().unhideSelection();
    }
}
