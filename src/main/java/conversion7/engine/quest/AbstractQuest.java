package conversion7.engine.quest;

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

public abstract class AbstractQuest {

    private static final Logger LOG = Utils.getLoggerForClass();

    private boolean completed;
    private Array<QuestOption> choiceItems = PoolManager.ARRAYS_POOL.obtain();
    private Array<String> descriptionRows = PoolManager.ARRAYS_POOL.obtain();
    private Texture picture;

    public Array<String> getDescriptionRows() {
        return descriptionRows;
    }

    public void addChoiceOption(QuestOption option) {
        choiceItems.add(option);
    }

    public void addDescriptionRow(ResourceKey row) {
        descriptionRows.add(row.getValue());
    }

    public void setPicture(PictureAsset pictureAsset) {
        picture = Assets.getPicture(pictureAsset);
    }

    public Texture getPicture() {
        return picture;
    }

    public Array<QuestOption> getChoiceItems() {
        return choiceItems;
    }

    protected abstract void initQuestState();

    protected void runChoiceClosure(QuestChoiceEvent event) {
        event.getSelectedOption().getActionClosure().run();
    }

    protected abstract void runPostClosureGlobalPhase();

    public void start() {
        Gdxg.clientUi.disableWorldInteraction();
        initQuestState();
        Gdxg.clientUi.getQuestWindow().showFor(this);
    }

    protected void complete() {
        completed = true;
    }

    protected void completeAndEnableInteraction() {
        Gdxg.clientUi.enableWorldInteraction();
        completed = true;
    }

    public void selected(QuestChoiceEvent event) {
        choiceItems.clear();
        descriptionRows.clear();

        // handle event
        runChoiceClosure(event);
        if (!completed) {
            runPostClosureGlobalPhase();
        }

        if (completed) {
            Gdxg.clientUi.getQuestWindow().close();
        } else {
            Gdxg.clientUi.getQuestWindow().refreshActiveQuest();
        }
    }

    public void setPictureViewActive(boolean act) {
        Gdxg.clientUi.getQuestWindow().setPictureViewEnabled(act);
    }
}
