package conversion7.engine.quest_old;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.PictureAsset;
import conversion7.game.strings.ResourceKey;
import conversion7.game.ui.quest.QuestChoiceEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public abstract class AbstractQuest {

    private static final Logger LOG = Utils.getLoggerForClass();

    private boolean completed;
    private Array<QuestOption> choiceItems = PoolManager.ARRAYS_POOL.obtain();
    private Array<String> descriptionRows = PoolManager.ARRAYS_POOL.obtain();
    private Texture picture;
    private static List<AbstractQuest> activeQuests = new ArrayList<>();

    public Array<String> getDescriptionRows() {
        return descriptionRows;
    }

    public Texture getPicture() {
        return picture;
    }

    public static List<AbstractQuest> getActiveQuests() {
        return activeQuests;
    }

    public void setPicture(PictureAsset pictureAsset) {
        picture = Assets.getPicture(pictureAsset);
    }

    public Array<QuestOption> getChoiceItems() {
        return choiceItems;
    }

    public void setPictureViewActive(boolean act) {
        Gdxg.clientUi.getQuestWindow().setPictureViewEnabled(act);
    }

    public void addChoiceOption(QuestOption option) {
        choiceItems.add(option);
    }

    public void addDescriptionRow(String row) {
        descriptionRows.add(row);
    }

    protected abstract void initQuestState();

    protected void runChoiceClosure(QuestChoiceEvent event) {
        event.getSelectedOption().getActionClosure().run();
    }

    protected abstract void runPostClosureGlobalPhase();

    public void start() {
        Gdxg.clientUi.disableWorldInteraction();
        activeQuests.add(this);
        initQuestState();
        Gdxg.clientUi.getQuestWindow().showFor(this);
    }

    public void complete() {
        completed = true;
        activeQuests.remove(this);
        Gdxg.clientUi.getQuestWindow().close();
    }

    public void completeAndEnableInteraction() {
        complete();
        Gdxg.clientUi.enableWorldInteraction();
    }

    public void selected(QuestChoiceEvent event) {
        choiceItems.clear();
        descriptionRows.clear();

        runChoiceClosure(event);
        if (!completed) {
            runPostClosureGlobalPhase();
            Gdxg.clientUi.getQuestWindow().refreshActiveQuest();
        }
    }
}
