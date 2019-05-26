package conversion7.game.stages.world.ai_deprecated.tasks.single;

import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class FortifyTask extends AbstractSquadTaskSingle {


    public FortifyTask(AbstractSquad owner) {
        super(owner);
    }

    @Override
    public boolean execute() {
//        owner.getActionsController().getAction(FortifyAction.class).begin();
        return true;
    }
}
