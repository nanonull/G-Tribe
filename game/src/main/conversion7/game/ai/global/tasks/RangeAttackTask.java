package conversion7.game.ai.global.tasks;

import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.actions.ActionEvaluation;
import conversion7.game.stages.world.view.AreaViewerAnimationsHelper;

public class RangeAttackTask extends AbstractUnitTask<AbstractSquad> {

    private AbstractSquad targetSquad;
    private Cell targetCell;

    public RangeAttackTask(AbstractSquad owner, AbstractSquad targetSquad) {
        super(owner);
        this.targetSquad = targetSquad;
    }

    public RangeAttackTask(AbstractSquad squad, Cell targetCell) {
        super(squad);
        this.targetCell = targetCell;
    }

    public static boolean isApplicable(AbstractSquad attacker, Cell targetCell) {
        return attacker.canAttack(targetCell)
                && ActionEvaluation.RANGE_ATTACK.evaluateOwner(attacker);
    }

    private void recalcTargetCell() {
        if (targetSquad != null) {
            targetCell = targetSquad.cell;
        }
    }

    @Override
    public boolean isValid() {
        recalcTargetCell();
        return isApplicable(owner, targetCell);
    }

    @Override
    public void run() {
        AreaViewerAnimationsHelper.subscribeOnAnimationCompleted(owner, this);
        recalcTargetCell();
        owner.initAttack(targetCell).setMeleeAttack(true).start();
        if (!AreaViewerAnimationsHelper.hasAnimationStarted(owner)) {
            onEvent();
        }
    }
}
