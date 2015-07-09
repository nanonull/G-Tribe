package conversion7.debug_tests;

import conversion7.AAATest;
import conversion7.AbstractTests;
import conversion7.engine.utils.Utils;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.CraftInventory;
import conversion7.game.stages.world.inventory.items.ArrowItem;
import conversion7.game.stages.world.inventory.items.JavelinItem;
import conversion7.game.stages.world.inventory.items.SkinItem;
import conversion7.game.stages.world.inventory.items.StickItem;
import conversion7.game.stages.world.inventory.items.StoneItem;
import conversion7.game.stages.world.objects.AbstractSquad;
import conversion7.game.stages.world.objects.AnimalHerd;
import conversion7.game.stages.world.objects.TownFragment;
import conversion7.game.stages.world.objects.actions.RangeAttackAction;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.test_steps.BattleSteps;
import conversion7.test_steps.WorldSteps;
import conversion7.test_steps.asserts.WorldAsserts;
import org.testng.annotations.Test;

public class WorldDebugSuite extends AbstractTests {

    @Test(invocationCount = 1)
    public void test_InfinityWorldSimulation() {
        new AAATest() {
            @Override
            public void body() {
                while (true) {
                    WorldSteps.rewindTeamsToStartNewWorldStep();
                    Utils.sleepThread(1000);
                }
            }
        }.run();
    }


    @Test(invocationCount = 1)
    public void test_DebugAreaViewer() {
        new AAATest() {
            @Override
            public void body() {
                WorldServices.createAnimalHerd(World.getPlayerTeam().getArmies().get(0).getCell().getCouldBeSeizedNeighborCell());

                Utils.infinitySleepThread();
            }
        }.run();
    }

    @Test
    public void run_ArmyWithSomeInventory() {
        new AAATest() {

            public static final int ITEM_QTY = 1;

            @Override
            public void body() {
                Team playerTeam = World.getPlayerTeam();
                AbstractSquad firstSquad = playerTeam.getArmies().get(0);

                WorldSteps.addItemToInventory(StoneItem.class, ITEM_QTY, firstSquad.getMainInventory());
                WorldSteps.addItemToInventory(StickItem.class, ITEM_QTY, firstSquad.getCell().getInventory());
                WorldSteps.addItemToInventory(StoneItem.class, 10, firstSquad.getCell().getInventory());
                WorldSteps.addItemToInventory(SkinItem.class, ITEM_QTY, firstSquad.getCell().getInventory());
                WorldSteps.addItemToInventory(JavelinItem.class, ITEM_QTY, firstSquad.getCell().getInventory());

                Unit unit = WorldServices.createSomeHumanUnit();
                WorldSteps.addUnitToAreaObject(unit, firstSquad);

                Utils.infinitySleepThread();
            }

        }.run();
    }

    @Test
    public void run_EquipTest() {
        new AAATest() {

            public static final int ITEM_QTY = 1;

            @Override
            public void body() {
                AbstractSquad humanSquad = World.getPlayerTeam().getArmies().get(0);

                WorldSteps.makeTeamCouldUseRecipes(humanSquad.getTeam(), CraftInventory.RECIPES_LIST);
                WorldSteps.addItemsNeededForCraft(humanSquad, CraftInventory.RECIPES_LIST);

                Utils.infinitySleepThread();
            }

        }.run();
    }

    @Test
    public void run_DebugCraft() {
        new AAATest() {

            public static final int ITEM_QTY = 1;

            @Override
            public void body() {
                Team playerTeam = World.getPlayerTeam();
                AbstractSquad firstSquad = playerTeam.getArmies().get(0);

                WorldSteps.addItemToInventory(StoneItem.class, ITEM_QTY, firstSquad.getMainInventory());
                WorldSteps.addItemToInventory(StickItem.class, ITEM_QTY, firstSquad.getCell().getInventory());
                WorldSteps.addItemToInventory(StoneItem.class, 10, firstSquad.getCell().getInventory());
                WorldSteps.addItemToInventory(SkinItem.class, ITEM_QTY, firstSquad.getCell().getInventory());
                WorldSteps.addItemToInventory(JavelinItem.class, ITEM_QTY, firstSquad.getCell().getInventory());

                WorldSteps.makeTeamCouldUseRecipes(playerTeam, CraftInventory.RECIPES_LIST);
                WorldSteps.addItemsNeededForCraft(firstSquad, CraftInventory.RECIPES_LIST);


                Unit unit = WorldServices.createSomeHumanUnit();
                WorldSteps.addUnitToAreaObject(unit, firstSquad);

                Utils.infinitySleepThread();
            }

        }.run();
    }

    @Test(invocationCount = 1)
    public void test_AnimalHerdAttacksHumanTownWithEmptyUnits() {
        new AAATest() {
            @Override
            public void body() {
                Team playerTeam = World.getPlayerTeam();
                TownFragment townFragment = WorldServices.createTownFragment(playerTeam, WorldSteps.getNextStandaloneCell());

                AnimalHerd animalHerd = WorldServices.createAnimalHerd(WorldSteps.getNextNeighborCell());
                BattleSteps.setAutoBattle(true);
                animalHerd.attack(townFragment);

                Utils.infinitySleepThread();
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_debugRangeAttack() {
        WorldServices.nextUnitSpecialization = Unit.UnitSpecialization.RANGE;
        Unit rangeUnit = WorldServices.createSomeHumanUnit();
        AbstractSquad humanSquad1 = World.getPlayerTeam().getArmies().get(0);
        WorldSteps.addUnitToAreaObject(rangeUnit, humanSquad1);
        WorldSteps.addItemToInventory(ArrowItem.class, 1, humanSquad1.getMilitaryInventory());
        WorldSteps.makeUnitCouldEquipItem(rangeUnit, ArrowItem.class);
        WorldAsserts.assertAreaObjectHasAction(humanSquad1, RangeAttackAction.class);

        Utils.infinitySleepThread();
    }

}
