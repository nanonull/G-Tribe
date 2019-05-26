package conversion7.engine.dialog;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.Gdxg;
import conversion7.engine.dialog.view.DialogWindow;
import conversion7.engine.dialog.view.QuestChoiceEvent;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.ui.UiLogger;
import org.slf4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractDialog {
    private static final Logger LOG = Utils.getLoggerForClass();
    protected static int INIT_STATE = 0;
    protected static int COMPLETED_STATE = Integer.MAX_VALUE;
    private static boolean dialogsEnabled = true;
    private static AbstractDialog activeDialog;
    private static LinkedList<AbstractDialog> dialogsQueue = new LinkedList<>();
    private boolean toBeCompleted;
    private Array<QuestOption> choiceItems = PoolManager.ARRAYS_POOL.obtain();
    private Array<QuestOption> choiceItemsWip = PoolManager.ARRAYS_POOL.obtain();
    private Array<QuestTextPhrase> descriptionRows = PoolManager.ARRAYS_POOL.obtain();
    private Array<QuestTextPhrase> descriptionRowsWip = PoolManager.ARRAYS_POOL.obtain();
    private Texture picture;
    private DialogWindow questWindow;
    protected Object state = INIT_STATE;
    protected Object prevState = null;
    private Map<Object, List> questStateMapSaved;
    private QuestOption lastSelectedOption;
    private boolean closed;
    private boolean appendToState = false;
    private boolean speakerEnabled = false;
    private String speaker;
    private StringBuilder descriptionRowBuilder = new StringBuilder();
    private boolean pictureViewActive;


    /**
     * Simple template:<br>
     * <p>
     * return [
     * (INIT_STATE):
     * [
     * 'description'
     * , QUEST_OPTION_1
     * , QUEST_OPTION_2
     * ]
     * ]
     */
    protected abstract Map<Object, List> getQuestStateMap();

    public Array<QuestTextPhrase> getDescriptionRows() {
        return descriptionRows;
    }

    public Texture getPicture() {
        return picture;
    }

    public void setPicture(Texture tex) {
        picture = tex;
        setPictureViewActive(true);
    }

    public Array<QuestOption> getChoiceItems() {
        return choiceItems;
    }

    public static AbstractDialog getActiveDialog() {
        return activeDialog;
    }

    public static void setActiveDialog(AbstractDialog activeDialog) {
        AbstractDialog.activeDialog = activeDialog;
    }

    public QuestOption getLastSelectedOption() {
        return lastSelectedOption;
    }

    public void setLastSelectedOption(QuestOption lastSelectedOption) {
        this.lastSelectedOption = lastSelectedOption;
    }

    public boolean getClosed() {
        return closed;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean getAppendToState() {
        return appendToState;
    }

    public boolean isAppendToState() {
        return appendToState;
    }

    public void setAppendToState(boolean appendToState) {
        this.appendToState = appendToState;
    }

    public boolean getSpeakerEnabled() {
        return speakerEnabled;
    }

    public boolean isSpeakerEnabled() {
        return speakerEnabled;
    }

    public void setSpeakerEnabled(boolean speakerEnabled) {
        this.speakerEnabled = speakerEnabled;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String name) {
        if (speaker != null && speakerEnabled) {
            flushSpeakerPhrase();
        }

        speaker = name;
    }

    public StringBuilder getDescriptionRowBuilder() {
        return descriptionRowBuilder;
    }

    public void setDescriptionRowBuilder(StringBuilder descriptionRowBuilder) {
        this.descriptionRowBuilder = descriptionRowBuilder;
    }

    public static void setDialogsEnabled(boolean act) {
        dialogsEnabled = act;
    }

    public void setPictureViewActive(boolean active) {
        pictureViewActive = active;
        questWindow.setPictureViewEnabled(active);
        questWindow.validateWidth();
    }

    public void setQuestWindow(DialogWindow questWindow) {
        this.questWindow = questWindow;
    }

    public static void start(AbstractDialog dialog, DialogWindow questWindow) {
        if (dialogsEnabled) {
            dialog.setQuestWindow(questWindow);
            if (activeDialog != null) {
                LOG.info("Another dialog already active, force complete " + activeDialog.getClass());
                dialogsQueue.add(dialog);
            }

            LOG.info("Start: {}", dialog.getClass().getSimpleName());
            activeDialog = dialog;
//            dialog.setPictureViewActive(false);
            Gdxg.clientUi.disableWorldInteraction();
            dialog.text("\n \n ------------------------ \n ");
            dialog.initQuestState();
            dialog.compute();
            questWindow.showFor(dialog);
        } else {
            UiLogger.addInfoLabel("Dialogs disabled: " + dialog.getClass().getSimpleName());
        }

    }

    protected Map<Object, List> buildQuestStateMap() {
        if (questStateMapSaved == null) {
            questStateMapSaved = getQuestStateMap();
        }

        return questStateMapSaved;
    }

    public void returnBack() {
        if (prevState == null) {
            UiLogger.addErrorLabel("No previous state!");
            this.state = (INIT_STATE);
        } else {
            this.state = prevState;
        }
    }

    public void newState(Object state) {
        prevState = this.state;
        this.state = state;
    }

    public void option(QuestOption option) {
        choiceItems.add(option);
    }

    public QuestOption option(String text, Runnable closure) {
        QuestOption questOption = new QuestOption(text, closure);
        choiceItems.add(questOption);
        return questOption;
    }

    public void text(String line) {
        if (descriptionRowBuilder.length() == 0) {
            if (speakerEnabled) {
                descriptionRowBuilder.append(speaker).append(": \n");
            }

        } else {
            descriptionRowBuilder.append("\n");
        }

        descriptionRowBuilder.append(line);

        LOG.info("text: {}", line);
    }

    public void disableSpeaker() {
        if (speakerEnabled) {
            flushSpeakerPhrase();
        }

        speakerEnabled = false;
    }

    public void enableSpeaker() {
        if (!speakerEnabled) {
            flushSceneDescription();
        }

        speakerEnabled = true;
    }

    public void flushSpeakerPhrase() {
        if (descriptionRowBuilder.length() > 0) {
            QuestTextPhrase phrase = new QuestTextPhrase(DialogConfig.SPEAKER_PHRASE_LABEL_STYLE
                    , descriptionRowBuilder.toString());
            descriptionRows.add(phrase);
            descriptionRowBuilder.setLength(0);
        }

    }

    public void flushSceneDescription() {
        if (descriptionRowBuilder.length() > 0) {
            QuestTextPhrase phrase = new QuestTextPhrase(DialogConfig.SCENE_PHRASE_LABEL_STYLE
                    , descriptionRowBuilder.toString());
            descriptionRows.add(phrase);
            descriptionRowBuilder.setLength(0);
        }
    }

    /** Another way to init: add definition of state in {@link #getQuestStateMap()} for {@link #INIT_STATE} state */
    protected void initQuestState() {
    }

    public void runChoiceClosure(QuestOption option) {
        lastSelectedOption = option;
        lastSelectedOption.getActionClosure().run();
        if (compute()) {
            questWindow.reload();
        }
    }

    public void reCompute() {
        computeItemsOfState();
    }

    /** Global dialog state machine */
    protected void computeItemsOfState() {
        LOG.info("computeItemsOfState state = {}", state);
        List options = buildQuestStateMap().get(state);
        if (options == null) {
            if (state.equals(COMPLETED_STATE)) {
                toBeCompleted = true;
            } else {
                throw new GdxRuntimeException(getClass().toString() + " could not resolve state " + state);
            }
        } else {
            for (Object opt : options) {
                if (opt instanceof String) {
                    text((String) opt);
                } else if (opt instanceof Runnable) {
                    ((Runnable) opt).run();
                } else if (opt instanceof QuestOption) {
                    option((QuestOption) opt);
                } else {
                    UiLogger.addErrorLabel("Unknown option type: " + opt.getClass());
                }
            }
            if (choiceItems.size == 0) {
                option("To world", () -> {
                    newState(COMPLETED_STATE);
                });
            }

        }
    }

    public void complete() {
        LOG.info("complete state = {}", state);
        questWindow.close();
        closed = true;
        activeDialog = null;
        Gdxg.clientUi.enableWorldInteraction();
        if (dialogsQueue.size() > 0) {
            AbstractDialog nextDialog = dialogsQueue.pop();
            start(nextDialog, nextDialog.questWindow);
        }
    }

    public void skipComputeState() {
        appendToState = true;
    }

    public void selected(QuestChoiceEvent event) {
        LOG.info("selected {}", event.getSelectedOption().getText());
        choiceItemsWip.clear();
        descriptionRowsWip.clear();
        choiceItems.clear();
        descriptionRows.clear();

        runChoiceClosure(event.getSelectedOption());
    }

    /** Returns true if need to reload dialog */
    public boolean compute() {
        if (appendToState) {
            LOG.info("compute appendToState = {}", true);
            flushTextBuilder();
            appendToState = false;
            questWindow.append();
            return false;
        } else {
            computeItemsOfState();
        }


        if (toBeCompleted) {
            complete();
        }


        // could be completed during computeStates
        if (closed) {
            return false;
        }


        flushTextBuilder();
        return true;
    }

    public void flushTextBuilder() {
        if (speakerEnabled) {
            flushSpeakerPhrase();
        } else {
            flushSceneDescription();
        }

    }
}
