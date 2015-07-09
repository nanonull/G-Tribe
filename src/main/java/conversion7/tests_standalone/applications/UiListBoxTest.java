package conversion7.tests_standalone.applications;

/**
 * ****************************************************************************
 * Copyright 2011 See AUTHORS file.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.test.TestScene;
import org.testng.annotations.Test;

public class UiListBoxTest extends ClientCore {

    static Object[] listEntries = {"This is a list entry1", "And another one1", "The meaning of life1", "Is hard to come by1",
            "This is a list entry2", "And another one2", "The meaning of life2", "Is hard to come by2", "This is a list entry3",
            "And another one3", "The meaning of life3", "Is hard to come by3", "This is a list entry4", "And another one4",
            "The meaning of life4", "Is hard to come by4", "This is a list entry5", "And another one5", "The meaning of life5",
            "Is hard to come by5"};

    @Test
    public void uiTreeTest() {
        ClientApplication.start(new UiListBoxTest());
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

        List list = new List(skin);
        list.setItems(listEntries);
        list.getSelection().setMultiple(true);
        list.getSelection().setRequired(false);
        // list.getSelection().setToggle(true);
        ScrollPane scrollPane2 = new ScrollPane(list, skin);
        scrollPane2.setFlickScroll(false);

        Gdxg.clientUi.stageGUI.addActor(scrollPane2);

    }

}
