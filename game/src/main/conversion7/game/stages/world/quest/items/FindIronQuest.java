package conversion7.game.stages.world.quest.items;

import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.quest.BaseQuest;
import org.slf4j.Logger;

public class FindIronQuest extends BaseQuest {

    private static final Logger LOG = Utils.getLoggerForClass();


    @Override
    public void initEntries() {
        initEntry(State.S1, "Find iron and build Iron Factory");
    }

    @Override
    public void onStart() {
    }


    public enum State {
        S1
    }
}