package conversion7.game.stages.world.quest.items;

import conversion7.game.stages.world.quest.BaseQuest;

public class AttackBaalsCamp extends BaseQuest {

    @Override
    public void initEntries() {
        initEntry(State.S1, "Attack Baals camp");
    }

    @Override
    public void onStart() {
    }


    public enum State {
        S1
    }
}