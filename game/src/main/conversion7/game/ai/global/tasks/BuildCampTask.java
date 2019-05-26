package conversion7.game.ai.global.tasks;

import conversion7.game.stages.world.objects.actions.items.BuildCampAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.actions.ActionEvaluation;

public class BuildCampTask extends AbstractUnitTask<AbstractSquad> {

    public BuildCampTask(AbstractSquad owner) {
        super(owner);
    }

    public static boolean isApplicable(AbstractSquad squad) {
        return ActionEvaluation.BUILD_CAMP.evaluateOwner(squad);
    }

    @Override
    public boolean isValid() {
        return isApplicable(owner);
    }

    @Override
    public void run() {
        owner.getActionsController().forceTreeValidationFromThisNode();
        owner.getActionsController().getAction(BuildCampAction.class).run();
        complete();
    }
}
