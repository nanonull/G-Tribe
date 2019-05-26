package conversion7.game.stages.world.adventure;

import com.badlogic.gdx.utils.Array;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.items.ArrowItem;
import conversion7.game.stages.world.inventory.items.weapons.FusionBlasterItem;
import conversion7.game.stages.world.inventory.items.weapons.PowerFistItem;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.goals.AttackObjectGoal;
import conversion7.game.stages.world.team.goals.FindAndAttackTribeGoal;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.effects.items.VengeanceHitEffect;
import conversion7.game.unit_classes.ufo.BaalScout;

import java.util.Iterator;

@Deprecated
public class BaalsScoutCampaignOld {
    private static final int ARRIVE_AT_STEP = BaalsMainCampaign.STEP_1 * 3;
    public static boolean arrived;
    static Team team;

    public static void newStep(World world) {

        int step = world.step;
        if (step == ARRIVE_AT_STEP
                && BaalsMainCampaign.campaignTargetAlive(world)
                && !GdxgConstants.DEVELOPER_MODE) {
            arrive(world);
            return;
        }

    }

    private static void arrive(World world) {
        arrived = true;
        Team targTeam = BaalsMainCampaign.targTeam;
        targTeam.addEventMainUiNotification(BaalsMainCampaign.TEAM_NAME + " squad has landed");
        team = world.getBaalsTeam();
        Unit unitControlsTribe = targTeam.getUnitControlsTribe();
        if (unitControlsTribe == null) {
            team.addGoal(new FindAndAttackTribeGoal(team, targTeam));
        } else {
            team.addGoal(new AttackObjectGoal(unitControlsTribe.squad));
        }

        Cell cellOrigin = targTeam.getSquads().get(0).getLastCell();
        Array<Cell> cellsAroundOnRadius = cellOrigin.
                getCellsAroundOnRadius(world.widthInCells / 4, new Array<>());

        int unitsToPlace = cellsAroundOnRadius.size / 7;
        Iterator<Cell> iterator = cellsAroundOnRadius.iterator();
        while ((iterator.hasNext())) {
            if (!iterator.next().canBeSeized()) {
                iterator.remove();
            }
        }


        unitsToPlace = Math.min(unitsToPlace, cellsAroundOnRadius.size);
        int unitsWithBows = unitsToPlace / 2;
        team.getInventory().addItem(ArrowItem.class, unitsWithBows * 3);

        cellsAroundOnRadius.shuffle();
        for (int i = 0; i < unitsToPlace; i++) {
            Cell cellToPlace = cellsAroundOnRadius.get(i);
            WorldSquad squad = WorldSquad.create(BaalScout.class, team, cellToPlace);
            if (unitsWithBows > 0) {
                unitsWithBows--;
                squad.equipment.equipRangeWeaponItem(FusionBlasterItem.class);
            }
            squad.equipment.equipMeleeWeaponItem(new PowerFistItem());
            squad.effectManager.getOrCreate(VengeanceHitEffect.class);
        }


    }


}
