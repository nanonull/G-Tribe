package conversion7.game.stages.world.quest.items;

import conversion7.game.stages.world.quest.BaseQuest;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;

public class FertilizeAnimalsQuest extends BaseQuest {


    public static void onNewUnitBorn(Unit childUnit) {
        if (childUnit.squad.isAnimal() && childUnit.squad.team.isHumanPlayer()) {
            FertilizeAnimalsQuest animalsQuest = childUnit.squad.team.journal.getOrCreate(FertilizeAnimalsQuest.class);
            animalsQuest.complete(State.S2);
        }
    }

    public static void addedAnimal(Team team) {
        BaseQuest quest = startQuest(team, FertilizeAnimalsQuest.class);
    }

    @Override
    public void initEntries() {
        initEntry(State.S1, "You've got animal unit.");
        complete(State.S1);
        initEntry(State.S2, "Find another unit of same class for reproduction.");
    }

    @Override
    public void onStart() {

    }

    public enum State {
        S1, S2
    }
}
