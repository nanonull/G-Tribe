package conversion7.game.stages.world.objects.actions.subactions;

import conversion7.engine.Gdxg;
import conversion7.game.stages.world.objects.actions.ShareFoodAction;

public class InputFoodForShareSubaction extends AbstractInputFoodAmountSubaction {

    private ShareFoodAction shareFoodAction;

    public InputFoodForShareSubaction(ShareFoodAction action) {
        super(action);
        this.shareFoodAction = action;
    }

    @Override
    public void execute() {
        Gdxg.clientUi.getInputDialog().startFor(this);
    }

    @Override
    public void handleInput(String text) {
        shareFoodAction.complete(getAcceptedInteger());
    }


}
