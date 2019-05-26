package conversion7.game.stages.world.objects.actions;

import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.Unit;

public abstract class AbstractSquadAction extends AbstractAreaObjectAction {

    public AbstractSquadAction(Group group) {
        super(group);
    }

    @Override
    public boolean isTwoStepCompletion() {
        return false;
    }

    public AbstractSquad getSquad() {
        return (AbstractSquad) super.getObject();
    }

    @Override
    public String getName() {
        return (isSuperAbilityAction() ? "(Super Ability) " : "") + name;
    }

    public boolean isSuperAbilityAction() {
        return actionEvaluation.isSuperAbility();
    }

    @Override
    public void end() {
        if (actionEvaluation.isStandardConsumingFromExecutor() && !cancelled) {
            updateExecutorParameters();
        }
    }

    protected void updateExecutorParameters() {
        updateExecutorParameters(getSquad().unit);
    }

    protected void updateExecutorParameters(Unit consumeFromUnit) {
        if (isSuperAbilityAction()) {
            consumeFromUnit.squad.setInspirationPoints(0);
        }
        consumeFromUnit.squad.updateMoveAp(-actionEvaluation.getMoveApCost());
        consumeFromUnit.squad.updateAttackAp(-actionEvaluation.getAttackApCost());
    }

}
