package conversion7.game.stages.world.quest.items;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.CameraController;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.adventure.WorldAdventure;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AnimalSpawn;
import conversion7.game.stages.world.objects.MountainDebris;
import conversion7.game.stages.world.quest.BaseQuest;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.goals.AttackObjectGoal;
import org.slf4j.Logger;

public class DestroyAnimalsSpawnQuest extends BaseQuest {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int EP = 1;
    private MountainDebris mountainDebris;

    public static void placeObjects(Cell fromCell) {
        World world = fromCell.getArea().world;
        Cell debrisCell = WorldAdventure.runAround(world.lastActivePlayerTeam, Area.WIDTH_IN_CELLS,
                Cell.Filters.CAN_SET_DEBRIS, WorldAdventure.Events.PLACE_DEBRIS);
        if (debrisCell != null) {
            world.questDebrisCell = debrisCell;
            LOG.info("debrisCell " + debrisCell);
            Cell debrisEnemyTribeCell = WorldAdventure.runAround(debrisCell, Area.WIDTH_IN_CELLS / 2,
                    Cell.Filters.CAN_PLACE_AI_TRIBE, WorldAdventure.Events.PLACE_AI_TRIBE);
            LOG.info("debrisTribeCell " + debrisEnemyTribeCell);
            world.addImportantObj(debrisCell.getObject(MountainDebris.class));
        }
    }

    @Override
    public void initEntries() {
        initEntry(State.S1, "Strong animal spawn located. Destroy it.");
    }

    @Override
    public void onStart() {
        if (team.world.questDebrisCell != null) {
            debrisFound(team.world.questDebrisCell.getObject(MountainDebris.class));
        }
    }

    private void debrisNotFound() {
        failAllOpen();
    }

    private void debrisFound(MountainDebris mountainDebris) {
        this.mountainDebris = mountainDebris;
        // set spawn
        AnimalSpawn spawn = new AnimalSpawn(mountainDebris.getLastCell(), team.world.animalTeam);
        spawn.setMigration(false);
        spawn.setQuest(true);
        spawn.setSpawnEvery(1);
        spawn.setSpawnedPowerMlt(1.2f);
        spawn.tick();

        Cell spawnLastCell = spawn.getLastCell();
        if (spawn.team.world.lastActivePlayerTeam.canSeeCell(spawnLastCell)) {
            CameraController.scheduleCameraFocusOn(100, spawnLastCell);
        }


        // create quest for player
        stateCellTargets.put(State.S1, spawnLastCell);

        // create ai global task for all ai tribes in 2 areas radius from debris
        Area midArea = mountainDebris.getLastCell().getArea();
        Array<Area> adjAreas = midArea.areasAround;
        for (Area adjArea : adjAreas) {
            notifyAt(adjArea);
        }
        notifyAt(midArea);

        mountainDebris.addDeathListener(whoDead -> {
            complete(State.S1);
            Team killedByTeam = whoDead.getKilledByTeam();
            if (killedByTeam != null) {
                killedByTeam.updateEvolutionPointsOn(EP, DestroyAnimalsSpawnQuest.class.getSimpleName());
            }
            spawn.destroy();
        });
    }

    public void notifyAt(Area area) {
        for (Cell[] cells : area.cells) {
            for (Cell cell : cells) {
                if (cell.hasSquad()) {
                    Team team = cell.squad.team;
                    if (team.isHumanAiTribe()) {
                        team.addGoal(new AttackObjectGoal(mountainDebris));
                    }
                }
            }
        }
    }

    public enum State {
        S1
    }
}