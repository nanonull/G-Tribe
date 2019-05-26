package conversion7.game.ai.global.tasks;

import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.actions.ActionEvaluation;
import conversion7.game.stages.world.view.AreaViewerAnimationsHelper;

public class AttackObjectTask extends AbstractUnitTask<AbstractSquad> {

    private AreaObject targetObject;

    public AttackObjectTask(AbstractSquad owner, AreaObject targetObject) {
        super(owner);
        this.targetObject = targetObject;
    }

    public static boolean isApplicable(AbstractSquad attacker, AreaObject target) {
        return !target.isRemovedFromWorld()
                && ActionEvaluation.MELEE_ATTACK.evaluateOwner(attacker)
                && attacker.isNeighborOf(target);
    }

    @Override
    public boolean isValid() {
        return isApplicable(owner, targetObject);
    }

    @Override
    public void run() {
        AreaViewerAnimationsHelper.subscribeOnAnimationCompleted(owner, this);
        owner.attackByBestWeapon(targetObject);
        if (!AreaViewerAnimationsHelper.hasAnimationStarted(owner)) {
            onEvent();
        }
    }
}
