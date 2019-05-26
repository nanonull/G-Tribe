package conversion7;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.table.DefaultTable;
import conversion7.game.Assets;
import conversion7.game.ui.ClientUi;

public class QuestEditor extends ClientCore {

    private Skin skin;
    private Stage stageGUI;
    private DefaultTable mainTable;

    public static void main(String[] args) {
        ClientApplication.startLibgdxCoreApp(new QuestEditor());
    }

    @Override
    public void create() {
        super.create();

        skin = Assets.uiSkin;
        stageGUI = Gdxg.graphic.getClientUi().stageGUI;

        mainTable = new DefaultTable().applyDefaultPaddings();
        stageGUI.addActor(mainTable);
        mainTable.pad(ClientUi.SPACING);
        mainTable.setFillParent(true);
        mainTable.setBackground(new TextureRegionColoredDrawable(new Color(0.0F, 0.0F, 0.0F, 0.7F), Assets.pixel));

        Label inputHint = new Label("Type quest options row by row:", Assets.labelStyle14orange);
        final TextArea inputTextArea = new TextArea("", skin);
        inputTextArea.setText("QUEST_OPTION_1\nQUEST_OPTION_2");

        TextButton generateButton = new TextButton("Generate", skin);

        Label outputOptionsHint = new Label("Copy into Quest class:", Assets.labelStyle14green);
        final TextArea outputOptionsTextArea = new TextArea("", skin);

        Label outputResKeysHint = new Label("Copy into Resource key class:", Assets.labelStyle14green);
        final TextArea outputResKeysTextArea = new TextArea("", skin);

        Label outputTextResKeyHint = new Label("Copy into text.properties class:", Assets.labelStyle14green);
        final TextArea outputTextResKeyTextArea = new TextArea("", skin);

        generateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                generateCode(inputTextArea.getText(), outputOptionsTextArea, outputResKeysTextArea, outputTextResKeyTextArea);
            }
        });

        mainTable.add(inputHint);
        mainTable.row();
        mainTable.add(inputTextArea).expand().fill();
        mainTable.row();
        mainTable.add(generateButton);

        mainTable.row();
        mainTable.add(outputOptionsHint);
        mainTable.row();
        mainTable.add(outputOptionsTextArea).expand().fill();

        mainTable.row();
        mainTable.add(outputResKeysHint);
        mainTable.row();
        mainTable.add(outputResKeysTextArea).expand().fill();

        mainTable.row();
        mainTable.add(outputTextResKeyHint);
        mainTable.row();
        mainTable.add(outputTextResKeyTextArea).expand().fill();
    }

    private void generateCode(String input, TextArea outputTextArea, TextArea outputResKeysTextArea, TextArea outputTextResKeyTextArea) {
        outputTextArea.setText("");
        outputResKeysTextArea.setText("");
        outputTextResKeyTextArea.setText("");

        String[] questItems = input.split("\n");

        for (String questItem : questItems) {
            if (questItem.isEmpty()) {
                continue;
            }
            outputTextArea.appendText("final QuestOption ".concat(questItem).concat(" = new QuestOption(ResourceKey.")
                    .concat(questItem).concat(".getValue(),\n")
                    .concat("            { ->\n")
                    .concat("            });\n\n"));
            outputResKeysTextArea.appendText(questItem + "(\"" + questItem + "\"),");
            outputTextResKeyTextArea.appendText(questItem + "=\n");

        }
    }

}
