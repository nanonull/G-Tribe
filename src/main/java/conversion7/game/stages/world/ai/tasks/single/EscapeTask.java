package conversion7.game.stages.world.ai.tasks.single;

import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.objects.AreaObject;
import org.slf4j.Logger;

public class EscapeTask extends MoveTask {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final int DEFAULT_PRIORITY =
            PropertiesLoader.getIntProperty("AreaObjectTask.Priority.EscapeTask");

    private AreaObject escapeFrom;

    public EscapeTask(AreaObject owner, AreaObject escapeFrom) {
        super(owner, null, DEFAULT_PRIORITY);
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

        if (owner.hasMorePowerThan(escapeFrom)) {
            LOG.info("has more power > cancel escaping");
            cancel();
            return true;
        }

        if (owner.getCell().distanceTo(escapeFrom.getCell()) > 3) {
            complete();
            return true;
        }

        if (!hasMoveTarget()) {
            Point2s escapeDirection = owner.getCell().diffWithCell(escapeFrom.getCell());
            escapeDirection.multiply(-8);
            setMoveTarget(owner.getCell().getArea().getCell(escapeDirection.x + owner.getCell().x, escapeDirection.y + owner.getCell().y));
        }

        moveStep();
        return false;
    }
}
