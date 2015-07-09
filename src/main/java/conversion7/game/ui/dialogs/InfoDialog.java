package conversion7.game.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import conversion7.game.Assets;
import conversion7.game.ui.ClientUi;

public class InfoDialog extends AbstractInfoDialog {

    public InfoDialog(Stage stage, String title, Skin skin, int direction) {
        super(stage, title, skin, direction);

        row();
        Table buttonsTable = new Table();
        add(buttonsTable).expandX().fill();
        buttonsTable.defaults().left().top().pad(ClientUi.SPACING);

        TextButton okButton = new TextButton("   OK   ", Assets.uiSkin);
        buttonsTable.add(okButton);
        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
    }

    public void show(String titleText, String message) {
        getTitleLabel().setText(titleText);
        description.setText(message);
        show();
    }
}
