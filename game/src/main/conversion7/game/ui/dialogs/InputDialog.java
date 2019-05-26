package conversion7.game.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import conversion7.game.Assets;
import conversion7.game.interfaces.InputProviderReset;
import conversion7.game.interfaces.StringInputResolver;
import conversion7.game.stages.world.objects.actions.items.subactions.InputFoodForShareSubaction;
import conversion7.game.ui.ClientUi;

public class InputDialog extends AbstractInfoDialog implements InputProviderReset {

    private TextField inputField;
    private StringInputResolver inputResolver;

    public InputDialog(Stage stage, String title, Skin skin, int direction) {
        super(stage, title, skin, direction);

        row();
        inputField = new TextField("", Assets.uiSkin);
        add(inputField).center();

        row();
        Table buttonsTable = new Table();
        add(buttonsTable).expandX().fill();
        buttonsTable.defaults().left().top().pad(ClientUi.SPACING);

        TextButton okButton = new TextButton("   OK   ", Assets.uiSkin);
        buttonsTable.add(okButton);
        okButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!Gdx.input.isTouched()) {
                    String inputFieldText = inputField.getText();
                    if (inputResolver.couldAcceptInput(inputFieldText)) {
                        inputResolver.handleInput(inputFieldText);
                        hide();
                        resetInputProvider();
                    }
                }
            }
        });


        TextButton cancelButton = new TextButton("Cancel", Assets.uiSkin);
        buttonsTable.add(cancelButton).right();
        cancelButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                inputResolver.cancel();
                hide();
                resetInputProvider();
            }
        });
    }

    @Override
    public void resetInputProvider() {
        getTitleLabel().setText("empty");
        description.setText("");
        inputField.setText("");
        inputResolver = null;
    }

    public void startFor(InputFoodForShareSubaction inputFoodForShareAction) {
        inputResolver = inputFoodForShareAction;
        getTitleLabel().setText("Input food amount for sharing");
        description.setText("How much food you want give to the selected army?\nAvailable food: "
                + 0);
        show();
    }
}
