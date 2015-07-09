package conversion7.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.ui.HintForm;
import conversion7.game.ui.UiLogger;
import conversion7.game.ui.dialogs.InfoDialog;
import conversion7.game.ui.dialogs.InputDialog;
import conversion7.game.ui.inputlisteners.ContinuousInput;
import org.slf4j.Logger;

public class DefaultClientUi {

    private static final Logger LOG = Utils.getLoggerForClass();

    public final static int SPACING = 4;
    public final static int HALF_SPACING = SPACING / 2;
    public static final int DOUBLE_SPACING = SPACING * 2;
    public final static int SCROLL_LINE_SIZE = 20;
    public static final int WINDOW_HEADER_HEIGHT = 20;
    public static final int BUTTON_HEIGHT = 28;
    /** Body has extra 2 pixels and could make e.g. scrollbar appear */
    public static final int MAGIC_COMPENSATE_PAD = 2;

    public InputMultiplexer inputMultiplexer = new InputMultiplexer();
    public Viewport screenViewport = new ScreenViewport();

    public Stage stageGUI = new Stage(screenViewport, Gdxg.spriteBatch);
    protected DefaultClientGraphic defaultClientGraphic;

    public DefaultClientUi(DefaultClientGraphic defaultClientGraphic) {
        this.defaultClientGraphic = defaultClientGraphic;
        Gdx.input.setInputProcessor(inputMultiplexer);
        UiLogger.init(stageGUI);
        ContinuousInput.init(defaultClientGraphic);
        hintForm = new HintForm(stageGUI);
        LOG.info("GUI registered");
    }

    /**
     * Handling priority: Gui > GlobalStage > GameStages...<p>
     * In parallel {@link conversion7.game.ui.inputlisteners.ContinuousInput} could be handled without catching<p>
     * GameStage + Gui<br>
     * or Gui only
     */
    public void registerInputProcessors(Array<InputProcessor> newProcessors) {

        inputMultiplexer.getProcessors().clear();
        if (newProcessors != null) {
            for (InputProcessor newProcessor : newProcessors) {
                inputMultiplexer.addProcessor(newProcessor);
            }
        }
        inputMultiplexer.addProcessor(0, stageGUI);
        inputMultiplexer.addProcessor(1, defaultClientGraphic.globalStage);

        LOG.info("inputMultiplexer.getProcessors().size = " + inputMultiplexer.getProcessors().size);
    }

    private InfoDialog infoDialog;

    public InfoDialog getInfoDialog() {
        if (infoDialog == null) {
            infoDialog = new InfoDialog(stageGUI, "empty",
                    Assets.uiSkin, AnimatedWindow.Direction.down);
        }
        return infoDialog;
    }

    //

    private InputDialog inputDialog;

    public InputDialog getInputDialog() {
        if (inputDialog == null) {
            inputDialog = new InputDialog(stageGUI, "",
                    Assets.uiSkin, AnimatedWindow.Direction.down);
        }
        return inputDialog;
    }

    private HintForm hintForm;

    public HintForm getHintForm() {
        return hintForm;
    }
}
