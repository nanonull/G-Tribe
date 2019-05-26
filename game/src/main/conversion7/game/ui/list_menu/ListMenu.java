package conversion7.game.ui.list_menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.SelectBoxList;
import conversion7.game.Assets;
import conversion7.game.ui.utils.UiUtils;

import java.util.List;

public class ListMenu {
    static Vector2 coords = new Vector2();
    static SelectBoxList selectBox = new SelectBoxList(Assets.uiSkin);

    public static void show(List<MenuItem> menuItems) {
        Stage stage = Gdxg.clientUi.stageGUI;
        stage.addActor(selectBox);
        selectBox.setItems(menuItems);

        coords.set(Gdx.input.getX(), Gdx.input.getY());
        stage.screenToStageCoordinates(coords);
        selectBox.setPosition(coords.x, coords.y);
        selectBox.toFront();
        UiUtils.keepWithinStage(selectBox, false);
        selectBox.setVisible(true);
    }
}
