package conversion7.game.stages.world.ai_deprecated.tasks.single;

import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import org.slf4j.Logger;

public class AttackTaskOldOld extends FollowTaskOldOld {

    private static final Logger LOG = Utils.getLoggerForClass();
    protected AbstractSquad targetSquad;

    public AttackTaskOldOld(AbstractSquad owner) {
        this(owner, null);
    }

    public AttackTaskOldOld(AbstractSquad owner, AbstractSquad targetSquad) {
        super(owner);
        setTarget(targetSquad);
    }

    @Override
    public void setTarget(AreaObject target) {
        super.setTarget(target);
        this.targetSquad = (AbstractSquad) target;
    }

    /** Returns true if target is attacked OR target is dead. */
    @Override
    public boolean execute() {
        if (targetSquad.isRemovedFromWorld()) {
            LOG.info("targetSquad.isRemovedFromWorld");
            cancel();
            return true;
        }

        if (owner.isNeighborOf(targetSquad)) {
            owner.meleeAttack(targetSquad);
            complete();
            return true;
        }

        super.execute();
        return false;
    }

    /** Need object to attack. */
    @Override
    public boolean couldAcceptInput(Cell input) {
        return super.couldAcceptInput(input)
                && input.getSquad().getTeam() != Gdxg.getAreaViewer().getSelectedSquad().getTeam();
    }

}
