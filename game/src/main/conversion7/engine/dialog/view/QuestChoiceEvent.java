package conversion7.engine.dialog.view;

import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import conversion7.engine.dialog.QuestOption;

public class QuestChoiceEvent extends ChangeListener.ChangeEvent {

    private QuestChoiceButton clickedButton;

    @Override
    public void reset() {
        clickedButton = null;
    }

    public QuestOption getSelectedOption() {
        return clickedButton.getQuestOption();
    }

    public void setClickedButton(QuestChoiceButton clickedButton) {
        this.clickedButton = clickedButton;
    }
}
