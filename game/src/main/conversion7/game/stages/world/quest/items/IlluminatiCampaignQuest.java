package conversion7.game.stages.world.quest.items;

import conversion7.game.stages.world.quest.BaseQuest;

public class IlluminatiCampaignQuest extends BaseQuest {
    @Override
    public void initEntries() {
        initEntry(State.S1, "Illuminati are searching Archon and Baal");
    }

    @Override
    public void onStart() {

    }

    public enum State {
        S1
    }
}
