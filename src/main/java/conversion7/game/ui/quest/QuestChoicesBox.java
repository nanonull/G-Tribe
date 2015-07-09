package conversion7.game.ui.quest;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import conversion7.engine.quest.QuestOption;
import conversion7.game.ui.ClientUi;

public class QuestChoicesBox extends WidgetGroup {

    public static final int PAD = ClientUi.SPACING;
    public static final int ROW_HEIGHT_WITH_PAD = ClientUi.BUTTON_HEIGHT + PAD;
    public static final int QUEST_CHOICES_BOX_HEIGHT = 200;
    private static final int QUEST_CHOICES_BOX_CONTENT_HEIGHT = QUEST_CHOICES_BOX_HEIGHT - PAD * 2
            - ClientUi.MAGIC_COMPENSATE_PAD;

    private Skin skin;
    private Array<QuestChoiceButton> buttons = new Array();
    private int totalHeight;
    private int activeButtons;

    public QuestChoicesBox(Skin skin) {
        this.skin = skin;
    }

    @Override
    public float getPrefHeight() {
        return totalHeight;
    }

    @Override
    public void layout() {
        for (int i = 0; i < activeButtons; i++) {
            QuestChoiceButton choiceButton = buttons.get(i);
            choiceButton.getButton().setWidth(getWidth() - ClientUi.SCROLL_LINE_SIZE - ClientUi.DOUBLE_SPACING);
            choiceButton.getButton().setPosition(PAD, totalHeight - (i + 1) * ROW_HEIGHT_WITH_PAD);
        }
    }

    public void refreshItems(Array<QuestOption> newChoiceItems) {
        TextButton button;

        for (QuestChoiceButton choiceButton : buttons) {
            choiceButton.reset();
        }

        activeButtons = newChoiceItems.size;
        totalHeight = Math.max(activeButtons * (ROW_HEIGHT_WITH_PAD),
                QUEST_CHOICES_BOX_CONTENT_HEIGHT);
        for (int i = 0; i < activeButtons; i++) {
            QuestOption questOption = newChoiceItems.get(i);
            final QuestChoiceButton questChoiceButton;

            if (buttons.size > i) {
                questChoiceButton = buttons.get(i);
            } else {
                button = new TextButton("null", skin);
                button.setHeight(ClientUi.BUTTON_HEIGHT);

                questChoiceButton = new QuestChoiceButton();
                questChoiceButton.setButton(button);
                buttons.add(questChoiceButton);
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        QuestChoiceEvent changeEvent = Pools.obtain(QuestChoiceEvent.class);
                        changeEvent.setClickedButton(questChoiceButton);
                        fire(changeEvent);
                        Pools.free(changeEvent);
                    }
                });
            }

            questChoiceButton.setQuestOption(questOption);
            questChoiceButton.updateButtonText();
            questChoiceButton.activate(this);
        }

        invalidate();
    }
}
