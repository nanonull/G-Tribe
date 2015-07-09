package conversion7.game.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import conversion7.engine.Gdxg;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.ui.ClientUi;

public class AreaObjectCouldNotMoveDialog extends AbstractInfoDialog {

    private AreaObject currentAreaObject;
    private Cell moveOnCell;

    public AreaObjectCouldNotMoveDialog(Stage stage, Skin skin, int direction) {
        super(stage, "Army could not move", skin, direction);

        description.setText("Some units have no Action points.\n" +
                "You could split army to move at least units which have Action points.");

        row();
        Table buttonsTable = new Table();
        add(buttonsTable).expandX().fill();
        buttonsTable.defaults().left().top().pad(ClientUi.SPACING);

        TextButton closeButton = new TextButton("Close", Assets.uiSkin);
        buttonsTable.add(closeButton);
        closeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                hide();
            }
        });


        TextButton splitButton = new TextButton("I want split my army", Assets.uiSkin);
        buttonsTable.add(splitButton).right();
        splitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                hide();
                Gdxg.clientUi.getSplitMergeUnitsWindow().showFor(currentAreaObject, moveOnCell);
            }
        });
    }

    public void showFor(AreaObject currentAreaObject, Cell moveOnCell) {
        this.currentAreaObject = currentAreaObject;
        this.moveOnCell = moveOnCell;
        show();
    }

}
