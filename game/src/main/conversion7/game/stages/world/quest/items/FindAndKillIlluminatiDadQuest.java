package conversion7.game.stages.world.quest.items;

import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.quest.BaseQuest;
import org.slf4j.Logger;

public class FindAndKillIlluminatiDadQuest extends BaseQuest {

    private static final Logger LOG = Utils.getLoggerForClass();


    @Override
    public void initEntries() {
        initEntry(State.S1, "Find and kill Illuminati Dad");
    }

    @Override
    public void onStart() {
    }


    public enum State {
        S2, S1
    }
}