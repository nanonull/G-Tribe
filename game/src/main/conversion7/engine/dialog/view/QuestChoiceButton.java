package conversion7.engine.dialog.view;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import conversion7.engine.dialog.QuestOption;
import conversion7.game.ui.hint.PopupHintPanel;

public class QuestChoiceButton {
    private TextButton button;
    private QuestOption questOption;

    public TextButton getButton() {
        return button;
    }

    public void setButton(TextButton button) {
        this.button = button;
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
        if (questOption.getDescription() != null) {
            PopupHintPanel.assignHintTo(button, questOption.getDescription());
        }
    }

    public void activate(QuestChoicesBox questChoicesBox) {
        questChoicesBox.addActor(button);
    }
}
