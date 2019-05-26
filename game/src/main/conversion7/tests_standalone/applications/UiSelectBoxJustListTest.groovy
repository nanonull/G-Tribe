package conversion7.tests_standalone.applications

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import conversion7.engine.ClientApplication
import conversion7.engine.ClientCore
import conversion7.engine.Gdxg
import conversion7.engine.custom2d.SelectBoxList
import conversion7.engine.utils.Utils
import conversion7.game.Assets
import conversion7.game.stages.test.TestScene
import conversion7.game.ui.UiLogger
import conversion7.game.ui.list_menu.ListMenu
import conversion7.game.ui.list_menu.MenuItem
import org.testng.annotations.Test

public class UiSelectBoxJustListTest extends ClientCore {

    @Test
    public void test1() {
        ClientApplication.startLibgdxCoreApp(new UiSelectBoxJustListTest());
        waitCreated();
        Utils.infinitySleepThread();
    }

    @Override
    public void create() {
        super.create();
        // test body:
        createUi();

        // render it:
        TestScene testScene = new TestScene(Gdxg.graphic.getCamera());
        Gdxg.core.activateStage(testScene);
    }

    private void createUi() {
        Skin skin = new Skin(Gdx.files.internal(Assets.ATLASES_FOLDER + "uiskin.json"));

        SelectBoxList selectBox = new SelectBoxList(skin);
        Gdxg.clientUi.stageGUI.addActor(selectBox);
        ListMenu.show([
                new MenuItem("1", {
                    UiLogger.addInfoLabel("1")
                })
                , new MenuItem("2", {
            UiLogger.addInfoLabel("2")
        })
        ]);
    }

}
