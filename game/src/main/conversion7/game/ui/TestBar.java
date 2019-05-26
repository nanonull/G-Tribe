package conversion7.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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

    boolean debugAllUiEnabled = false;
    boolean debugUiSelModeEnabled = false;
    private boolean debugUiSelModeInitialized;
    private Stage mainStage;
    private Actor lastClickedActor;
    private long lastFrameId;


    public TestBar(Stage mainStage) {
        super(TITLE, Assets.uiSkin);
        this.mainStage = mainStage;
        setY(GdxgConstants.SCREEN_HEIGHT_IN_PX);
        setHeight(64);
        left().top();
        registerButtons();
        setKeepWithinStage(true);
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


        TextButton button5 = new TextButton("CellDbg", Assets.uiSkin);
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

        TextButton devModeBtn = new TextButton("DevMode", Assets.uiSkin);
        add(devModeBtn).pad(ClientUi.SPACING).expandY().fill();

        devModeBtn.addListener(new InputListener() {
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
                Gdxg.getAreaViewer().fullViewRefresh();
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
                if (GdxgConstants.AREA_OBJECT_AI) {
                    GdxgConstants.AREA_OBJECT_AI = false;
                    UiLogger.addInfoLabel("AREA_OBJECT_AI OFF");
                } else {
                    GdxgConstants.AREA_OBJECT_AI = true;
                    UiLogger.addInfoLabel("AREA_OBJECT_AI ON");
                }
            }
        });

        TextButton reloadViewerButton = new TextButton("Reload Viewer", Assets.uiSkin);
        add(reloadViewerButton).pad(ClientUi.SPACING).expandY().fill();
        reloadViewerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdxg.core.areaViewer.requestCellsRefresh();
            }
        });

        TextButton debugAllUiButton = new TextButton("Debug UI", Assets.uiSkin);
        add(debugAllUiButton).pad(ClientUi.SPACING).expandY().fill();
        debugAllUiButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                debugAllUiEnabled = !debugAllUiEnabled;
                Gdxg.clientUi.stageGUI.setDebugAll(debugAllUiEnabled);
                Gdxg.graphic.getGlobalStage().setDebugAll(debugAllUiEnabled);
            }
        });

        String onDebugUiSelMode = "ON Debug UI Sel-Mode";
        String offDebugUiSelMode = "OFF Debug UI Sel-Mode";
        TextButton debugUiSelModeButton = new TextButton(onDebugUiSelMode, Assets.uiSkin);
        add(debugUiSelModeButton).pad(ClientUi.SPACING).expandY().fill();
        debugUiSelModeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                debugUiSelModeEnabled = !debugUiSelModeEnabled;
                if (debugUiSelModeEnabled) {
                    debugUiSelModeButton.setText(offDebugUiSelMode);
                    if (!debugUiSelModeInitialized) {
                        debugUiSelModeInitialized = true;
                        for (Actor actor : getStage().getActors()) {
                            initDebugUiSelModeFor(actor);
                        }
                    }
                } else {
                    debugUiSelModeButton.setText(onDebugUiSelMode);
                }

                Gdxg.clientUi.stageGUI.setDebugAll(debugAllUiEnabled);
                Gdxg.graphic.getGlobalStage().setDebugAll(debugAllUiEnabled);
            }
        });

        TextButton debugLastActorButton = new TextButton("Debug Last Actor", Assets.uiSkin);
        add(debugLastActorButton).pad(ClientUi.SPACING).expandY().fill();
        debugLastActorButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                debugActor(lastClickedActor);
            }
        });

        TextButton sleepOffButton = new TextButton("Awake Sleep", Assets.uiSkin);
        add(sleepOffButton).pad(ClientUi.SPACING).expandY().fill();
        sleepOffButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Utils.activeSleepThread = false;
            }
        });

        pack();
    }

    private void debugActor(Actor actor) {
        if (actor != null && debugUiSelModeEnabled) {
            if (actor instanceof Group) {
                Group group = (Group) actor;
                group.debugAll();
            } else {
                if (actor.hasParent()) {
                    actor.getParent().debugAll();
                }
            }
        }
    }

    private void initDebugUiSelModeFor(Actor actor) {
        if (actor == this) {
            return;
        }
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                long frameId = Gdx.graphics.getFrameId();
                if (lastFrameId != frameId) {
                    lastFrameId = frameId;
                    lastClickedActor = actor;
                    debugActor(actor);
                    LOG.info(actor.getClass().toString());
                }
            }
        });
        if (actor instanceof Group) {
            Group group = (Group) actor;
            for (Actor child : group.getChildren()) {
                initDebugUiSelModeFor(child);
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        this.getTitleLabel().setText(format("%s, fps: %d", TITLE, Gdx.graphics.getFramesPerSecond()));
    }

    public void turn() {
        if (hasParent()) {
            hide();
        } else {
            show();
        }
    }

    public void hide() {
        remove();
    }

    public void show() {
        mainStage.addActor(this);
    }
}
