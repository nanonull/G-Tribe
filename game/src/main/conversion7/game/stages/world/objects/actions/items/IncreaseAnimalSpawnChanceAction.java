package conversion7.game.stages.world.objects.actions.items;

import conversion7.engine.Gdxg;
import conversion7.game.stages.world.objects.AnimalSpawn;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.dialogs.InfoDialog;

public class IncreaseAnimalSpawnChanceAction extends AbstractSquadAction {

    public IncreaseAnimalSpawnChanceAction() {
        super(Group.TRIBE);
    }

    @Override
    public void begin() {
        AbstractSquad squad = getSquad();
        AnimalSpawn lastSpawn = squad.getLastCell().getArea().getLastSpawn();
        if (lastSpawn == null) {
            lastSpawn.setMaxAnimalSpawnChance();
        } else {
            Gdxg.clientUi.getInfoDialog().show("Can't apply", "Area has no spawn");
        }
    }

    @Override
    protected String buildDescription() {
        return getName() + "\n \nSet animal spawn chance to " + AnimalSpawn.MAX_SPAWN_CHANCE + "% in current area";
    }
}
