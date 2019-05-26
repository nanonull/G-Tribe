package conversion7.game.stages.world.ai_deprecated.tasks.single;

import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import org.slf4j.Logger;

public class EscapeTaskOldOld extends MoveTaskOld {

    private static final Logger LOG = Utils.getLoggerForClass();

    private AbstractSquad escapeFrom;

    public EscapeTaskOldOld(AbstractSquad owner, AbstractSquad escapeFrom) {
        super(owner, null);
        this.escapeFrom = escapeFrom;
    }

    @Override
    protected void initRadius() {
    }

    @Override
    public boolean execute() {
        if (escapeFrom.isRemovedFromWorld()) {
            LOG.info("escapeFrom.isRemovedFromWorld");
            cancel();
            return true;
        }

        if (owner.hasMoreActualPowerThan(escapeFrom)) {
            LOG.info("has more power > cancel escaping");
            cancel();
            return true;
        }

        if (owner.getLastCell().distanceTo(escapeFrom.getLastCell()) > 3) {
            complete();
            return true;
        }

        if (!hasMoveTarget()) {
            Point2s escapeDirection = owner.getLastCell().getDiffWithCell(escapeFrom.getLastCell());
            escapeDirection.multiply(-8);
            setMoveTarget(owner.getLastCell().getArea().getCell(escapeDirection.x + owner.getLastCell().x, escapeDirection.y + owner.getLastCell().y));
        }

        moveStep();
        return false;
    }
}
