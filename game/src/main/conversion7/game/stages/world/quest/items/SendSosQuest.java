package conversion7.game.stages.world.quest.items;

import conversion7.engine.AudioPlayer;
import conversion7.game.stages.world.quest.BaseQuest;

public class SendSosQuest extends BaseQuest {
    @Override
    public void initEntries() {
        initEntry(State.S1, "build iron factory");
        initEntry(State.S2, "repair ship");
        initEntry(State.S3, "build uranus factory");
        initEntry(State.S4, "build communication satellite");
        initEntry(State.S5, "move satellite to orbit and send SOS");

    }

    @Override
    public void failAllOpen() {
        super.failAllOpen();
        AudioPlayer.playEnd();
    }

    @Override
    public void onStart() {

    }

    public enum State {
        S2, S3, S4, S5, S1

    }
}
