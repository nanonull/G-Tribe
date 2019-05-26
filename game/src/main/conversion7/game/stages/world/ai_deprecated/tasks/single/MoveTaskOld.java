package conversion7.game.stages.world.ai_deprecated.tasks.single;

import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import org.slf4j.Logger;

public class MoveTaskOld extends AbstractSquadTaskSingle {

    private static final Logger LOG = Utils.getLoggerForClass();

    protected int radius;
    protected int randomMoveAmplitude;
    protected Point2s lastMoveTargetDiff;
    private Cell moveTarget;
    private Cell lastMoveTarget;

    public MoveTaskOld(AbstractSquad owner, Cell moveTo) {
        super(owner);
        setMoveTarget(moveTo);
        initRadius();
    }

    @Override
    public String getDescription() {
        StringBuilder stringBuilder = new StringBuilder(super.toString()).append(" ");
        if (moveTarget == null) {
            stringBuilder.append("target=null");
        } else {
            stringBuilder.append(moveTarget.toStringAsWorldCoordinate());
        }
        return stringBuilder.toString();
    }

    public Cell getCellForMoveInRandomDirection() {
        int rndY = MathUtils.RANDOM.nextInt(radius) * (owner.getLastCell().isFromSouthHemisphere() ? 1 : -1)
                - radius / 4;
        Point2s randomPoint = new Point2s(MathUtils.RANDOM.nextInt(randomMoveAmplitude) - radius, rndY);
        if (LOG.isDebugEnabled()) LOG.debug("getCellForMoveInRandomDirection: " + randomPoint);
        return owner.getLastCell().getArea().getCell(owner.getLastCell(), randomPoint);
    }

    public Cell getMoveTarget() {
        return moveTarget;
    }

    public void setMoveTarget(Cell target) {
        if (target != null) {
            this.moveTarget = target;
            this.lastMoveTarget = moveTarget;
            this.lastMoveTargetDiff = owner.getLastCell().getDiffWithCell(target);
        }
    }

    public Cell getLastMoveTarget() {
        return lastMoveTarget;
    }

    protected void initRadius() {
        radius = 8;
        randomMoveAmplitude = radius * 2 + 1;
    }

    @Override
    public String toString() {
        return new StringBuilder(this.getClass().getSimpleName())
                .append(" moveTarget=").append(moveTarget).toString();
    }

    public boolean execute() {
        if (moveStep()) {
            complete();
            return true;
        }
        return false;
    }

    /** Returns true if movement completed */
    public boolean moveStep() {
        if (owner.canMove() && owner.moveOneStepTo(moveTarget)) {
            clearTarget();
            return true;
        }
        return false;
    }

    private void clearTarget() {
        moveTarget = null;
    }

    protected boolean hasMoveTarget() {
        return moveTarget != null;
    }

    public void repeatPreviousMovement() {
        if (LOG.isDebugEnabled())
            LOG.debug("no better cells - continue movement in previous direction " + lastMoveTargetDiff);
        setMoveTarget(owner.getLastCell().getArea().getCell(owner.getLastCell(), lastMoveTargetDiff));
    }

    /** This movement has goal to move on cells with comfort temperature */
    public void initMoveInRandomDirection() {
        setMoveTarget(getCellForMoveInRandomDirection());
    }


}
