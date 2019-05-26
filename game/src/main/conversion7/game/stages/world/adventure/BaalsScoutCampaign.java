package conversion7.game.stages.world.adventure;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.items.FusionCellItem;
import conversion7.game.stages.world.inventory.items.weapons.FusionBlasterItem;
import conversion7.game.stages.world.inventory.items.weapons.PowerFistItem;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AiGoalHelper;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.unit_classes.UnitClassConstants;
import conversion7.game.unit_classes.ufo.BaalScout;

import java.util.Iterator;

public class BaalsScoutCampaign {
    private static final int ARRIVE_AFTER_STEP = 1;
    private static final int PLACE_RADIUS = (int) (World.SPACE_SHIP_AREA_RADIUS * 0.5f);
    private static final int WAVE_UNITS = 5;
    private static final int WAVE_HP = (int) (UnitClassConstants.BASE_POWER * 0.3f);
    public static int waveLevel = 1;

    public static Team getMainTeam() {
        return Gdxg.core.world.getBaalsTeam();
    }

    public static void newStep(World world) {
        int step = world.step;
        if (step >= ARRIVE_AFTER_STEP && getMainTeam().getSquads().size < step
                && world.humanPlayers.size > 0
            /*&& !GdxgConstants.DEVELOPER_MODE*/) {
            arrive(world);
        }

    }

    public static void arrive(World world) {
        world.addEventMainUiNotification(BaalsMainCampaign.TEAM_NAME + " scouts has landed");
        Team team = getMainTeam();

        Cell cellOrigin = world.getSpaceShip().getLastCell();
        Array<Cell> cellsAroundOnRadius = cellOrigin.
                getCellsAround(0, PLACE_RADIUS, new Array<>());

        Iterator<Cell> iterator = cellsAroundOnRadius.iterator();
        while ((iterator.hasNext())) {
            if (!iterator.next().canBeSeized()) {
                iterator.remove();
            }
        }

        int unitsToPlace = Math.min(WAVE_UNITS * world.humanPlayers.size, cellsAroundOnRadius.size);
        int rangeUnits = waveLevel * world.humanPlayers.size;
        rangeUnits = Math.min(rangeUnits, unitsToPlace / 2);

        cellsAroundOnRadius.shuffle();
        for (int i = 0; i < unitsToPlace; i++) {
            Cell cellToPlace = cellsAroundOnRadius.get(i);
            WorldSquad squad = WorldSquad.create(BaalScout.class, team, cellToPlace);
            squad.equipment.equipMeleeWeaponItem(new PowerFistItem());

            boolean range = false;
            if (i < world.humanPlayers.size) {
                squad.addAiGoal(AiGoalHelper.moveToAndAttackTribe(world.humanPlayers.get(i)));
            } else {
                if (rangeUnits > 0) {
                    range = true;
                    rangeUnits--;
                    squad.equipment.equipRangeWeaponItem(FusionBlasterItem.class);
                    team.getInventory().addItem(FusionCellItem.class, 3);
                }
            }

            if (!range) {
                squad.power.updateMaxValue(WAVE_HP * waveLevel);
            }

        }
        waveLevel++;
    }


}
