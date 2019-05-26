package conversion7.engine.dialog;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class QuestTextPhrase {
    private Label.LabelStyle labelStyle;
    private String text;

    public QuestTextPhrase(Label.LabelStyle labelStyle, String text){
        this.labelStyle = labelStyle;
        this.text = text;
    }
    public Label.LabelStyle getLabelStyle() {
        return labelStyle;
    }

    public String getText() {
        return text;
    }
}
