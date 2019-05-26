package conversion7.game.stages.world.objects.actions.items.subactions;

import conversion7.engine.Gdxg;
import conversion7.game.stages.world.objects.actions.items.ShareFoodAction;

// TODO review share food
@Deprecated
public class InputFoodForShareSubaction extends AbstractInputFoodAmountSubaction {

    private ShareFoodAction shareFoodAction;

    public InputFoodForShareSubaction(ShareFoodAction action) {
        super(action, action.getInitiator());
        this.shareFoodAction = action;
    }

    public ShareFoodAction getShareFoodAction() {
        return shareFoodAction;
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
