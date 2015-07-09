package conversion7.game.ui.quest;

import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import conversion7.engine.quest.QuestOption;

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
