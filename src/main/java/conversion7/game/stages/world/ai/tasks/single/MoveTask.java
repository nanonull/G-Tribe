package conversion7.game.stages.world.ai.tasks.single;

import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AbstractSquad;
import conversion7.game.stages.world.objects.AreaObject;
import org.slf4j.Logger;

public class MoveTask extends AbstractAreaObjectTaskSingle {

    private static final Logger LOG = Utils.getLoggerForClass();
    public static final int DEFAULT_PRIORITY =
            PropertiesLoader.getIntProperty("AreaObjectTask.Priority.MoveTask");

    protected int RADIUS;
    protected int RANDOM_MOVE_AMPLITUDE;
    private Cell moveTarget;
    protected Point2s lastMoveTargetDiff;

    public MoveTask(AreaObject owner, Cell moveTo, int priority) {
        super(owner, priority);
        setMoveTarget(moveTo);
        initRadius();
    }

    public MoveTask(AreaObject owner, Cell moveTo) {
        this(owner, moveTo, DEFAULT_PRIORITY);
    }

    protected void initRadius() {
        RADIUS = 8;
        RANDOM_MOVE_AMPLITUDE = RADIUS * 2 + 1;
    }

    @Override
    public String toString() {
        return new StringBuilder(this.getClass().getSimpleName())
                .append(" moveTarget=").append(moveTarget).toString();
    }

    @Override
    public String getDescription() {
        StringBuilder stringBuilder = new StringBuilder(super.toString()).append(" ");
        if (moveTarget == null) {
            stringBuilder.append("target=null");
        } else {
            stringBuilder.append(moveTarget.x).append(",").append(moveTarget.y);
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean execute() {
        if (moveStep()) {
            complete();
            return true;
        }
        return false;
    }

    public void setMoveTarget(Cell target) {
        if (target != null) {
            this.moveTarget = target;
            this.lastMoveTargetDiff = owner.getCell().diffWithCell(target);
        }
    }

    protected boolean hasMoveTarget() {
        return moveTarget != null;
    }

    /** Returns true if movement completed */
    public boolean moveStep() {
        if (owner.couldMove() && ((AbstractSquad) owner).moveOneStepTo(moveTarget)) {
            clearTarget();
            return true;
        }
        return false;
    }

    private void clearTarget() {
        moveTarget = null;
    }

    public void repeatPreviousMovement() {
        if (LOG.isDebugEnabled())
            LOG.debug("no better cells - continue movement in previous direction " + lastMoveTargetDiff);
        setMoveTarget(owner.getCell().getArea().getCell(owner.getCell(), lastMoveTargetDiff));
    }

    /** This movement has goal to move on cells with comfort temperature */
    public void initMoveInRandomDirection() {
        setMoveTarget(getCellForMoveInRandomDirection());
    }

    public Cell getCellForMoveInRandomDirection() {
        int rndY = Utils.RANDOM.nextInt(RADIUS) * (owner.getCell().isFromSouthHemisphere() ? 1 : -1)
                - RADIUS / 4;
        Point2s randomPoint = new Point2s(Utils.RANDOM.nextInt(RANDOM_MOVE_AMPLITUDE) - RADIUS, rndY);
        if (LOG.isDebugEnabled()) LOG.debug("getCellForMoveInRandomDirection: " + randomPoint);
        return owner.getCell().getArea().getCell(owner.getCell(), randomPoint);
    }


}
