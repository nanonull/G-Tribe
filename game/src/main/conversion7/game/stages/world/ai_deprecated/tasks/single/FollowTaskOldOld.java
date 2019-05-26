package conversion7.game.stages.world.ai_deprecated.tasks.single;

import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import org.slf4j.Logger;

public class FollowTaskOldOld extends MoveTaskOld {

    private static final Logger LOG = Utils.getLoggerForClass();

    private AreaObject targetObject;

    public FollowTaskOldOld(AbstractSquad owner) {
        super(owner, null);
    }

    @Override
    public String getDescription() {
        return new StringBuilder(getClass().getSimpleName()).append(" ")
                .append(targetObject.getLastCell().toStringAsWorldCoordinate()).toString();
    }

    public void setTarget(AreaObject target) {
        this.targetObject = target;
    }

    /** Returns true if target is on neighbor cell OR target is dead. */
    @Override
    public boolean execute() {
        if (targetObject.isRemovedFromWorld()) {
            LOG.info("targetObject.isRemovedFromWorld");
            cancel();
            return true;
        }

        if (!owner.sees(targetObject)) {
            LOG.info("{}\nDon't see target: {}", owner, targetObject);
            if (getLastMoveTarget() == null) {
                Utils.printErrorWithCurrentStacktrace("INVEST: How is it possible? There must happened at least 1 movement, so approximate target should be present...");
                cancel();
            } else if (owner.getLastCell() == getLastMoveTarget()) {
                LOG.info("We came on last cell where saw target... {}", getLastMoveTarget());
                cancel();
            } else {
                LOG.info("Continue to cell where last saw... {}", getLastMoveTarget());
                followStep(getLastMoveTarget());
            }
            return false;
        }

        return followStep(targetObject.getLastCell());
    }

    public boolean followStep(Cell cell) {
        LOG.info("followStep {}", cell);
        setMoveTarget(cell);
        return moveStep();
    }

    /** Need object to follow. */
    public boolean couldAcceptInput(Cell input) {
        return input.hasSquad() && !input.isSeizedBy(Gdxg.getAreaViewer().getSelectedSquad());
    }

}
