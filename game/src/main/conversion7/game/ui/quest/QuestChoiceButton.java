package conversion7.game.ui.quest;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import conversion7.engine.quest_old.QuestOption;

public class QuestChoiceButton {
    private TextButton button;
    private QuestOption questOption;

    public void setButton(TextButton button) {
        this.button = button;
    }

    public TextButton getButton() {
        return button;
    }

    public QuestOption getQuestOption() {
        return questOption;
    }

    public void setQuestOption(QuestOption questOption) {
        this.questOption = questOption;
    }

    public void reset() {
        button.remove();
    }

    public void updateButtonText() {
        // TODO add hint when trim ChoiceItem text
        button.setText(questOption.getText());
    }

    public void activate(QuestChoicesBox questChoicesBox) {
        questChoicesBox.addActor(button);
    }
}
