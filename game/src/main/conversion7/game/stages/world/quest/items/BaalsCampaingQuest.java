package conversion7.game.stages.world.quest.items;

import conversion7.game.stages.world.adventure.BaalsMainCampaign;
import conversion7.game.stages.world.quest.BaseQuest;

public class BaalsCampaingQuest extends BaseQuest {

    @Override
    public void initEntries() {
        initEntry(State.S0, BaalsMainCampaign.QUEST_TEXT_0);
    }

    @Override
    public void onStart() {

    }

    public enum State {
        S0, S1, S2, S3, S4
    }
}
