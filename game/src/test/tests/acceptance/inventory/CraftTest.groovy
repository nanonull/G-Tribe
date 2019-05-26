package tests.acceptance.inventory

import conversion7.game.stages.world.inventory.CraftInventory
import conversion7.game.stages.world.inventory.items.SkinItem
import conversion7.game.stages.world.inventory.items.SkinRobeItem
import conversion7.game.stages.world.inventory.items.StoneItem
import conversion7.game.stages.world.inventory.items.weapons.CudgelItem
import conversion7.game.stages.world.inventory.items.weapons.HammerItem
import conversion7.game.stages.world.inventory.items.weapons.StickItem
import conversion7.game.stages.world.objects.unit.AbstractSquad
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

public class CraftTest extends BaseGdxgSpec {

    public void 'test craft'() {
        given:
        AbstractSquad humanSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        // hammer = stick + stone
        worldSteps.makeTeamCouldUseItem(humanSquad.getTeam(), HammerItem.class);

        when:
        worldSteps.addItemToInventory(StickItem.class, 1, humanSquad.getInventory());
        then: "no target item yet"
        WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getCraftInventory(), HammerItem.class);

        when: "add final ingredient"
        worldSteps.addItemToInventory(StoneItem.class, 1, humanSquad.getInventory());
        then: "1 qty possible craft"
        WorldAsserts.assertInventoryContainsItem(humanSquad.getCraftInventory(), HammerItem.class, 1);

        when:
        worldSteps.addItemToInventory(StoneItem.class, 1, humanSquad.getInventory());
        worldSteps.addItemToInventory(StickItem.class, 1, humanSquad.getInventory());
        then: "+1 qty to possible craft"
        WorldAsserts.assertInventoryContainsItem(humanSquad.getCraftInventory(), HammerItem.class, 2);

        when:
        humanSquad.inventoryController.executeCraft(HammerItem.class)
        then: "craft 1 qty of item"
        WorldAsserts.assertInventoryContainsItem(humanSquad.getCraftInventory(), HammerItem.class, 1);
        WorldAsserts.assertInventoryContainsItem(humanSquad.getInventory(), HammerItem.class, 1);
        and: "half of ingredients left"
        WorldAsserts.assertInventoryContainsItem(humanSquad.getInventory(), StickItem.class, 1);
        WorldAsserts.assertInventoryContainsItem(humanSquad.getInventory(), StoneItem.class, 1);

    }

    public void 'test SkillRestriction'() {
        given:
        int ROBE_CRAFT_CONSUME = 3;

        def humanSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());

        when:
        worldSteps.addItemToInventory(StickItem.class, 1, humanSquad.getInventory());
        worldSteps.addItemToInventory(StoneItem.class, 1, humanSquad.getInventory());

        then: "skill > not active"
        WorldAsserts.assertInventoryIsEmpty(humanSquad.getCraftInventory());

        when:
        worldSteps.makeTeamCouldUseItem(humanSquad.getTeam(), HammerItem.class);

        then: "learn skill > activate craft"
        WorldAsserts.assertInventoryContainsItem(humanSquad.getCraftInventory(), HammerItem.class, 1);

        when:
        worldSteps.makeTeamCouldUseItem(humanSquad.getTeam(), SkinRobeItem.class);
        worldSteps.addItemToInventory(SkinItem.class, ROBE_CRAFT_CONSUME, humanSquad.getInventory());

        then: "add consumable > activate craft"
        WorldAsserts.assertInventoryContainsItem(humanSquad.getCraftInventory(), SkinRobeItem.class, 1);
    }

    public void 'test BreakCudgelOnSticks'() {
        given:
        AbstractSquad humanSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        worldSteps.makeTeamCouldUseItem(humanSquad.getTeam(), StickItem.class);
        worldSteps.addItemsNeededForCraft(humanSquad, StickItem.class);

        when:
        worldSteps.craft(humanSquad, StickItem.class);

        then:
        WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getInventory(), CudgelItem.class);
        WorldAsserts.assertInventoryContainsItem(humanSquad.getInventory(), StickItem.class,
                CraftInventory.getRecipeForItem(StickItem.class).getFinalItemQuantityPerCraft());
    }

    public void 'test AllRecipesCrafted'() {
        given:
        lockCore()
        AbstractSquad humanSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        releaseCoreAndWaitNextCoreStep()

        when:
        worldSteps.makeTeamCouldUseRecipes(humanSquad.getTeam(), CraftInventory.RECIPES_LIST);
        worldSteps.addItemsNeededForCraft(humanSquad, CraftInventory.RECIPES_LIST);
        then:
        WorldAsserts.assertHaveConsumablesToCraft(humanSquad, CraftInventory.RECIPES_LIST);

        when:
        worldSteps.craftAll(humanSquad, CraftInventory.RECIPES_LIST);
        then:
        WorldAsserts.assertInventoryContainsCraftedItemsOnly(humanSquad.getInventory(), CraftInventory.RECIPES_LIST);
    }
}
