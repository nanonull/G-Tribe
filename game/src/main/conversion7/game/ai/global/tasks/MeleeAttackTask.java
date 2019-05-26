package conversion7.game.ai.global.tasks;

import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.actions.ActionEvaluation;
import conversion7.game.stages.world.view.AreaViewerAnimationsHelper;

public class MeleeAttackTask extends AbstractUnitTask<AbstractSquad> {

    private AbstractSquad targetSquad;
    private Cell targetCell;

    public MeleeAttackTask(AbstractSquad owner, Cell targetCell) {
        super(owner);
        this.targetCell = targetCell;
    }

    public MeleeAttackTask(AbstractSquad owner, AbstractSquad targetSquad) {
        super(owner);
        this.targetSquad = targetSquad;
    }

    public static boolean isApplicable(AbstractSquad attacker, Cell targetCell) {
        return attacker.canAttack(targetCell)
                && ActionEvaluation.MELEE_ATTACK.testMeVsTargetFull(attacker, targetCell);
    }

    @Override
    public boolean isValid() {
        recalcTargetCell();
        return isApplicable(owner, targetCell);
    }

    @Override
    public void run() {
//        owner.addActionListener(this);
        AreaViewerAnimationsHelper.subscribeOnAnimationCompleted(owner, this);
        recalcTargetCell();

        owner.initAttack(targetCell).setMeleeAttack(true).start();
        if (!AreaViewerAnimationsHelper.hasAnimationStarted(owner)) {
            onEvent();
        }
    }

    private void recalcTargetCell() {
        if (targetSquad != null) {
            targetCell = targetSquad.cell;
        }
    }
}
