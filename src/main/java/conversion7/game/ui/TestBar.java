package conversion7.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.tests_standalone.RuntimeTestInvoker;
import org.slf4j.Logger;

import static java.lang.String.format;

public class TestBar extends Window {

    private static final Logger LOG = Utils.getLoggerForClass();

    private final static String TITLE = "Test Bar";

    private boolean displayed = false;

    public TestBar() {
        super(TITLE, Assets.uiSkin);
        setY(GdxgConstants.SCREEN_HEIGHT_IN_PX * 0.85f);
        setHeight(64);
        left().top();
        registerButtons();
    }


    public void registerButtons() {

        final TextButton runTestBut = new TextButton("Run Test", Assets.uiSkin);
        add(runTestBut).pad(ClientUi.SPACING).expandY().fill();

        runTestBut.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                LOG.info("< " + runTestBut.getText() + " click");
                RuntimeTestInvoker.run();
                LOG.info("> clicked");
            }
        });


        TextButton button5 = new TextButton("TurnDebugHint", Assets.uiSkin);
        add(button5).pad(ClientUi.SPACING).expandY().fill();

        button5.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdxg.clientUi.getDebugCellHint().turnEnabled();
            }
        });

        TextButton button6 = new TextButton("DevMode", Assets.uiSkin);
        add(button6).pad(ClientUi.SPACING).expandY().fill();

        button6.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (GdxgConstants.DEVELOPER_MODE) {
                    GdxgConstants.DEVELOPER_MODE = false;
                    UiLogger.addInfoLabel("dev mode OFF");
                } else {
                    GdxgConstants.DEVELOPER_MODE = true;
                    UiLogger.addInfoLabel("dev mode ON");
                }
            }
        });

        TextButton turnAi = new TextButton("TurnAi", Assets.uiSkin);
        add(turnAi).pad(ClientUi.SPACING).expandY().fill();

        turnAi.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (GdxgConstants.AI_AREA_OBJECT_ENABLED) {
                    GdxgConstants.AI_AREA_OBJECT_ENABLED = false;
                    UiLogger.addInfoLabel("AI_AREA_OBJECT_ENABLED OFF");
                } else {
                    GdxgConstants.AI_AREA_OBJECT_ENABLED = true;
                    UiLogger.addInfoLabel("AI_AREA_OBJECT_ENABLED ON");
                }
            }
        });

        TextButton textButton = new TextButton("Run test", Assets.uiSkin);
        add(textButton).pad(ClientUi.SPACING).expandY().fill();

        textButton.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                RuntimeTestInvoker.run();
            }
        });

        pack();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        this.getTitleLabel().setText(format("%s, fps: %d", TITLE, Gdx.graphics.getFramesPerSecond()));
    }

    public void turn() {
        if (displayed) {
            hide();
        } else {
            show();
        }
    }

    public void hide() {
        displayed = false;
        remove();
    }

    public void show() {
        displayed = true;
        Gdxg.clientUi.stageGUI.addActor(this);
    }
}
