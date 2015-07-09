package conversion7.game.stages.world.objects.actions.subactions;

import conversion7.engine.Gdxg;
import conversion7.game.stages.world.objects.actions.RitualAction;

public class InputFoodForRitualSubaction extends AbstractInputFoodAmountSubaction {

    private RitualAction ritualAction;

    public InputFoodForRitualSubaction(RitualAction action) {
        super(action);
        ritualAction = action;
    }

    @Override
    public void execute() {
        Gdxg.clientUi.getInputDialog().startFor(this);
    }

    @Override
    public void handleInput(String text) {
        ritualAction.complete(getAcceptedInteger());
    }

}
