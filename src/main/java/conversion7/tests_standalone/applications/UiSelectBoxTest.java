package conversion7.tests_standalone.applications;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.test.TestScene;
import org.testng.annotations.Test;

public class UiSelectBoxTest extends ClientCore {

    @Test
    public void uiTreeTest() {
        ClientApplication.start(new UiSelectBoxTest());
        waitCoreCreated();
        Utils.infinitySleepThread();
    }

    @Override
    public void create() {
        super.create();
        // test body:
        createUi();

        // render it:
        TestScene testScene = new TestScene(Gdxg.graphic.getCamera());
        core.activateStage(testScene);
    }

    private void createUi() {
        Skin skin = new Skin(Gdx.files.internal(Assets.ATLASES_FOLDER + "uiskin.json"));

        SelectBox selectBox = new SelectBox(skin);
        selectBox.setItems(new String[]{"Item 1", "Item 2", "Item 3"});
        selectBox.setX(400);
        selectBox.setWidth(200);
        Gdxg.clientUi.stageGUI.addActor(selectBox);

    }

}
