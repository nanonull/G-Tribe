package conversion7.acceptance_tests;

import conversion7.AAATest;
import conversion7.AbstractTests;
import conversion7.engine.utils.Utils;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.BasicInventory;
import conversion7.game.stages.world.inventory.items.MammothTuskItem;
import conversion7.game.stages.world.inventory.items.StoneItem;
import conversion7.game.stages.world.objects.HumanSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.test_steps.BattleSteps;
import conversion7.test_steps.WorldSteps;
import conversion7.test_steps.asserts.WorldAsserts;
import org.slf4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AcceptanceInventoryTest extends AbstractTests {

    private static final Logger LOG = Utils.getLoggerForClass();

    Team defaultTeam;
    HumanSquad defaultHumanSquad;

    @BeforeClass(alwaysRun = true)
    @Override
    public void beforeClass() {
        super.beforeClass();
        LOG.info("BeforeClass");
        defaultTeam = World.createHumanTeam(false);
        defaultHumanSquad = defaultTeam.createHumanSquad(WorldSteps.getNextStandaloneCell());
    }

    @Test
    public void test_AddItemToMainInventory() {
        new AAATest() {

            public static final int ITEM_QTY = 1;

            @Override
            public void body() {
                // add
                WorldSteps.addItemToInventory(StoneItem.class, ITEM_QTY, defaultHumanSquad.getMainInventory());
                WorldAsserts.assertInventoryContainsItem(defaultHumanSquad.getMainInventory(), StoneItem.class, ITEM_QTY);

                // merge
                WorldSteps.addItemToInventory(StoneItem.class, ITEM_QTY, defaultHumanSquad.getMainInventory());
                WorldAsserts.assertInventoryContainsItem(defaultHumanSquad.getMainInventory(), StoneItem.class, 2 * ITEM_QTY);
            }

        }.run();
    }

    @Test(invocationCount = 1)
    public void test_CellInventoryAfterArmyDefeated() {
        new AAATest() {

            public static final int ITEM_QTY = 1;

            @Override
            public void body() {
                Team humanTeam1 = World.getPlayerTeam();
                Team team2 = World.createHumanTeam(false);
                HumanSquad army1 = humanTeam1.createHumanSquad(WorldSteps.getNextStandaloneCell());
                HumanSquad army2 = team2.createHumanSquad(WorldSteps.getNextNeighborCell());

                Unit unit1 = WorldServices.createSomeHumanUnit();
                WorldSteps.addUnitToAreaObject(unit1, army1);
                WorldSteps.makeUnitInvincible(unit1);

                Unit unit2 = WorldServices.createSomeHumanUnit();
                WorldSteps.addUnitToAreaObject(unit2, army2);
                WorldSteps.addItemToInventory(MammothTuskItem.class, ITEM_QTY, army2.getMainInventory());
                WorldSteps.addDefaultEquipmentPackageToInventory(army2.getMilitaryInventory());

                BasicInventory defeatedArmyCellInventory = army2.getCell().getInventory();
                defeatedArmyCellInventory.clearItems();

                BattleSteps.setAutoBattle(true);
                army1.attack(army2);

                WorldAsserts.assertInventoryContainsDefaultEquipmentPackage(defeatedArmyCellInventory);
                WorldAsserts.assertInventoryContainsItem(defeatedArmyCellInventory, MammothTuskItem.class, ITEM_QTY);
            }

        }.run();
    }

}
