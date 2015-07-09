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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.test.TestScene;
import org.testng.annotations.Test;

public class UiTreeTest extends ClientCore {

    @Test
    public void uiTreeTest() {
        ClientApplication.start(new UiTreeTest());
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

        Table table = new Table();
        table.setFillParent(true);
        Gdxg.clientUi.stageGUI.addActor(table);

        final Tree tree = new Tree(skin);

        final Node moo1 = new Node(new TextButton("moo1", skin));
        final Node moo2 = new Node(new TextButton("moo2", skin));
        final Node moo3 = new Node(new TextButton("moo3", skin));
        final Node moo4 = new Node(new TextButton("moo4", skin));
        final Node moo5 = new Node(new TextButton("moo5", skin));
        tree.add(moo1);
        tree.add(moo2);
        moo2.add(moo3);
        moo3.add(moo4);
        tree.add(moo5);

        moo5.getActor().addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                tree.remove(moo4);
            }
        });

        table.add(tree).fill().expand();
    }

}
