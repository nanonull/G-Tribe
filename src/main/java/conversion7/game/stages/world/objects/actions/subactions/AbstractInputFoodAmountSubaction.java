package conversion7.game.stages.world.objects.actions.subactions;

import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;
import conversion7.game.ui.UiLogger;

public abstract class AbstractInputFoodAmountSubaction extends AbstractInputIntegerSubaction {

    public AbstractInputFoodAmountSubaction(AbstractAreaObjectAction action) {
        super(action);
    }

    @Override
    public boolean couldAcceptInput(String text) {
        if (super.couldAcceptInput(text)) {
            int inputFood = getAcceptedInteger();
            int maxValue = parentAction.getObject().getFoodStorage().getFood();
            if (inputFood > 0 && inputFood <= maxValue) {
                return true;
            } else {
                UiLogger.addInfoLabel(String.format("Possible value is in range [1 - %d]", maxValue));
            }

        }
        return false;
    }

}
