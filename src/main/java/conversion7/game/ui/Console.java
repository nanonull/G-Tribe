package conversion7.game.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.run.ConsoleCommandExecutor;
import conversion7.game.ui.inputlisteners.ContinuousInput;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Console extends AnimatedWindow {

    private static final Logger LOG = Utils.getLoggerForClass();

    ScrollPane scroll;
    private Table consoleLogTable = new Table();
    final TextField inputCommandField;
    List<String> consoleHistory = new ArrayList<>();
    int consoleHistoryUserPosition = 0;

    public Console(Stage stageGUI) {
        super(stageGUI, "Console", Assets.uiSkin, Direction.down);

        left().top();
        setSize(GdxgConstants.SCREEN_WIDTH_IN_PX, GdxgConstants.SCREEN_HEIGHT_IN_PX / 2);
        setY(GdxgConstants.SCREEN_HEIGHT_IN_PX / 2);
        updateAnimationBounds();

        // DISPLAY COMMANDS
        consoleLogTable.left().bottom();
        scroll = new ScrollPane(consoleLogTable, Assets.uiSkin);
        add(scroll).fill().expand().left();
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);

        // INPUT
        row().fill();
        final Table inputTable = new Table();
        add(inputTable).left();

        inputTable.row();
        inputTable.add(new Label("Command:", Assets.labelStyle14yellow)).spaceRight(10f);

        inputCommandField = new TextField("", Assets.uiSkin);
        inputTable.add(inputCommandField).fill().expand();

        logInfoToConsole("Type 'help' for listing all commands");

        inputCommandField.addListener(new InputListener() {

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    String commandText = inputCommandField.getText();
                    if (commandText.length() > 0) {
                        inputCommandField.setText("");
                        logCommandStarted(commandText);
                        ConsoleCommandExecutor.runCmd(commandText);
                        return true;
                    }
                }
                if (keycode == Input.Keys.UP) {
                    navigateUpInCommandHistory();
                    return true;
                }
                if (keycode == Input.Keys.DOWN) {
                    navigateDownInCommandHistory();
                    return true;
                }

                return super.keyUp(event, keycode);
            }
        });

        this.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                LOG.info("touchDown in " + getClass());
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return true;
            }
        });
    }

    /** Previous command */
    private void navigateUpInCommandHistory() {
        if (LOG.isDebugEnabled()) LOG.debug("consoleHistoryUserPosition: " + consoleHistoryUserPosition);
        if (consoleHistory.isEmpty()) {
            return;
        }

        consoleHistoryUserPosition--;
        if (consoleHistoryUserPosition == -1) {
            consoleHistoryUserPosition = 0;
        }
        loadCommandFromHistory();
    }

    /** Return back to the newest commands */
    private void navigateDownInCommandHistory() {
        if (LOG.isDebugEnabled()) LOG.debug("consoleHistoryUserPosition: " + consoleHistoryUserPosition);
        if (consoleHistory.isEmpty()) {
            return;
        }

        if (consoleHistoryUserPosition < consoleHistory.size()) {
            consoleHistoryUserPosition++;
            if (consoleHistoryUserPosition < consoleHistory.size()) {
                loadCommandFromHistory();
            } else {
                inputCommandField.setText("");
            }
        }
    }

    private void loadCommandFromHistory() {
        String upCommand = consoleHistory.get(consoleHistoryUserPosition);
        inputCommandField.setText(upCommand);
        inputCommandField.setCursorPosition(upCommand.length());
    }

    public void logErrorToConsole(String text) {
        logToConsole(text, true);
    }

    public void logInfoToConsole(String text) {
        logToConsole(text, false);
    }

    private void logCommandStarted(String text) {
        logInfoToConsole(text);
        consoleHistory.add(text);
        consoleHistoryUserPosition = consoleHistory.size();
    }

    private void logToConsole(String text, boolean error) {
        LOG.info("logToConsole: " + text);
        Label label;
        if (error) {
            label = new Label(text, Assets.labelStyle14red);
        } else {
            label = new Label(text, Assets.labelStyle14green);
        }
        consoleLogTable.row();
        consoleLogTable.add(label).left();
        label.setAlignment(Align.left);
        scroll.layout();
        scroll.setScrollPercentY(100);
    }

    @Override
    public void onShow() {
        ContinuousInput.setEnabled(false);
        getStage().setKeyboardFocus(inputCommandField);
    }

    @Override
    public void onHide() {
        ContinuousInput.setEnabled(true);
    }

}
