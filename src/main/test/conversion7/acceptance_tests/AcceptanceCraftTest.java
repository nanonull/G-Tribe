package conversion7.acceptance_tests;

import conversion7.AAATest;
import conversion7.AbstractTests;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.CraftInventory;
import conversion7.game.stages.world.inventory.items.CudgelItem;
import conversion7.game.stages.world.inventory.items.HammerItem;
import conversion7.game.stages.world.inventory.items.SkinItem;
import conversion7.game.stages.world.inventory.items.SkinRobeItem;
import conversion7.game.stages.world.inventory.items.StickItem;
import conversion7.game.stages.world.inventory.items.StoneItem;
import conversion7.game.stages.world.objects.HumanSquad;
import conversion7.test_steps.WorldSteps;
import conversion7.test_steps.asserts.WorldAsserts;
import org.testng.annotations.Test;

public class AcceptanceCraftTest extends AbstractTests {

    @Test(invocationCount = 1)
    public void test_simpleCraft() {
        new AAATest() {

            public static final int FINAL_CRAFT_QTY = 2;

            @Override
            public void body() {
                // hammer = stick + stone
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());
                WorldSteps.makeTeamCouldUseItem(humanSquad.getTeam(), HammerItem.class);

                WorldSteps.addItemToInventory(StickItem.class, 1, humanSquad.getMainInventory());
                WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getCraftInventory(), HammerItem.class);

                // 1 possible craft
                WorldSteps.addItemToInventory(StoneItem.class, 1, humanSquad.getMainInventory());
                WorldAsserts.assertInventoryContainsItem(humanSquad.getCraftInventory(), HammerItem.class, 1);

                // +1 qty to possible craft
                WorldSteps.addItemToInventory(StoneItem.class, 2, humanSquad.getMainInventory());
                WorldSteps.addItemToInventory(StickItem.class, 1, humanSquad.getMainInventory());
                WorldAsserts.assertInventoryContainsItem(humanSquad.getCraftInventory(), HammerItem.class, FINAL_CRAFT_QTY);

                // perform craft
                humanSquad.getCraftInventory().craft(HammerItem.class);
                WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getCraftInventory(), HammerItem.class);
                WorldAsserts.assertInventoryContainsItem(humanSquad.getMainInventory(), HammerItem.class, FINAL_CRAFT_QTY);
                WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMainInventory(), StickItem.class);
                WorldAsserts.assertInventoryContainsItem(humanSquad.getMainInventory(), StoneItem.class, 1);

            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_SkillRestriction() {
        new AAATest() {

            public static final int ROBE_CRAFT_CONSUME = 3;

            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());

                // -
                WorldSteps.addItemToInventory(StickItem.class, 1, humanSquad.getMainInventory());
                WorldSteps.addItemToInventory(StoneItem.class, 1, humanSquad.getMainInventory());
                WorldAsserts.assertInventoryIsEmpty(humanSquad.getCraftInventory());

                // +
                WorldSteps.makeTeamCouldUseItem(humanSquad.getTeam(), HammerItem.class);
                WorldAsserts.assertInventoryContainsItem(humanSquad.getCraftInventory(), HammerItem.class, 1);

                // at once +
                WorldSteps.makeTeamCouldUseItem(humanSquad.getTeam(), SkinRobeItem.class);
                WorldSteps.addItemToInventory(SkinItem.class, ROBE_CRAFT_CONSUME, humanSquad.getMainInventory());
                WorldAsserts.assertInventoryContainsItem(humanSquad.getCraftInventory(), SkinRobeItem.class, 1);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_BreakCudgelOnSticks() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());
                WorldSteps.makeTeamCouldUseItem(humanSquad.getTeam(), StickItem.class);
                WorldSteps.addItemsNeededForCraft(humanSquad, StickItem.class);
                WorldSteps.craft(humanSquad, StickItem.class);

                WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMainInventory(), CudgelItem.class);
                WorldAsserts.assertInventoryContainsItem(humanSquad.getMainInventory(), StickItem.class,
                        CraftInventory.getRecipeForItem(StickItem.class).getFinalItemQuantityPerCraft());
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_AllRecipesCrafted() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());

                WorldSteps.makeTeamCouldUseRecipes(humanSquad.getTeam(), CraftInventory.RECIPES_LIST);
                WorldSteps.addItemsNeededForCraft(humanSquad, CraftInventory.RECIPES_LIST);
                WorldAsserts.assertHaveConsumablesToCraft(humanSquad, CraftInventory.RECIPES_LIST);

                WorldSteps.craftAll(humanSquad, CraftInventory.RECIPES_LIST);
                WorldAsserts.assertInventoryContainsCraftedItemsOnly(humanSquad.getMainInventory(), CraftInventory.RECIPES_LIST);
            }
        }.run();
    }
}
