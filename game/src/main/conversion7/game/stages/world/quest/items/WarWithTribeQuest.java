package conversion7.game.stages.world.quest.items;

import conversion7.game.stages.world.quest.BaseQuest;
import conversion7.game.stages.world.team.Team;

public class WarWithTribeQuest extends BaseQuest {

    public int wars;

    @Override
    public String getAddInfo() {
        return " | Wars from beginning: " + wars;
    }

    public static void newWarStarted(Team team) {
        WarWithTribeQuest tribeQuest = team.journal.getOrCreate(WarWithTribeQuest.class);
        tribeQuest.wars++;
    }

    public static void noActiveWars(Team team) {
        WarWithTribeQuest tribeQuest = team.journal.getOrCreate(WarWithTribeQuest.class);
        tribeQuest.complete(State.S1);
    }

    @Override
    public void initEntries() {
        initEntry(State.S1, "You have open war(s)");
    }

    @Override
    public void onStart() {

    }

    public enum State {
        S1
    }
}