package conversion7.game.stages.world.objects.actions.items.subactions;

import conversion7.game.stages.world.actions.AbstractAction;
import conversion7.game.stages.world.actions.AbstractInputIntegerSubaction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.UiLogger;

@Deprecated
public abstract class AbstractInputFoodAmountSubaction extends AbstractInputIntegerSubaction {

    private AbstractSquad squadWillShareFood;

    public AbstractInputFoodAmountSubaction(AbstractAction parentSquadAction, AbstractSquad squadWillShareFood) {
        super(parentSquadAction);
        this.squadWillShareFood = squadWillShareFood;
    }

    @Override
    public boolean couldAcceptInput(String text) {
        if (super.couldAcceptInput(text)) {
            int inputFood = getAcceptedInteger();
            int maxValue = 1;
            if (inputFood > 0 && inputFood <= maxValue) {
                return true;
            } else {
                UiLogger.addInfoLabel(String.format("Possible value is in range [1 - %d]", maxValue));
            }

        }
        return false;
    }

}
