package conversion7.game.stages.world.objects.actions.subactions;

import conversion7.game.interfaces.StringInputResolver;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;
import conversion7.game.ui.UiLogger;

public abstract class AbstractInputIntegerSubaction extends AbstractSubaction implements StringInputResolver {

    private static final String INPUT_MASK = "^-*[0-9]+";
    private String acceptedText;
    private Integer acceptedInteger;

    public AbstractInputIntegerSubaction(AbstractAreaObjectAction action) {
        super(action);
    }

    public String getAcceptedText() {
        return acceptedText;
    }

    public Integer getAcceptedInteger() {
        return acceptedInteger;
    }

    @Override
    public boolean couldAcceptInput(String text) {
        if (text.matches(INPUT_MASK)) {
            acceptedText = text;
            acceptedInteger = Integer.parseInt(text);
            return true;
        } else {
            UiLogger.addInfoLabel("Input Integer value!");
            return false;
        }
    }
}
