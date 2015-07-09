package conversion7.test_steps;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.AbstractTests;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.FastAsserts;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.BasicInventory;
import conversion7.game.stages.world.inventory.CraftInventory;
import conversion7.game.stages.world.inventory.CraftRecipe;
import conversion7.game.stages.world.inventory.items.ArrowItem;
import conversion7.game.stages.world.inventory.items.BowItem;
import conversion7.game.stages.world.inventory.items.SkinItem;
import conversion7.game.stages.world.inventory.items.StickItem;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AbstractSquad;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.HumanSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.skills.AbstractSkill;
import conversion7.game.stages.world.unit.Unit;
import conversion7.test_steps.asserts.WorldAsserts;
import org.slf4j.Logger;
import org.testng.Assert;

import static org.fest.assertions.api.Assertions.assertThat;

public class WorldSteps {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static final Point2s cellsIterator = new Point2s();

    public static Cell getNextStandaloneCell() {
        cellsIterator.x += 2;
        if (cellsIterator.x >= World.WIDTH_IN_CELLS - 3) {
            cellsIterator.x = 1;
            cellsIterator.y += 2;
        }

        LOG.info("getNextStandaloneCell: " + cellsIterator);
        FastAsserts.assertLessThan(cellsIterator.y, World.HEIGHT_IN_CELLS - 3,
                "There are no free cells anymore in World!");

        Cell cell = World.getArea(0, 0).getCell(cellsIterator.x, cellsIterator.y);
        clearCellAndNeighborsForTest(cell);
        return cell;
    }

    public static void clearCellAndNeighborsForTest(Cell origin) {
        clearCellForTest(origin);
        for (Cell cell : origin.getNeighborCells()) {
            if (cell.isSeized()) {
                LOG.info("clearCellAndNeighborsForTest > kill object for testing purposes on " + cell);
                cell.getSeizedBy().defeat();
            }
        }
        WorldAsserts.assertPlayerTeamIsNotDefeated();
    }

    public static void clearCellForTest(Cell origin) {
        if (origin.isSeized()) {
            LOG.info("clearCellForTest > kill object for testing purposes on origin: " + origin);
            origin.getSeizedBy().defeat();
        }
    }

    public static Cell getNextNeighborCell() {
        cellsIterator.x += 1;

        LOG.info("getNextNeighborCell: " + cellsIterator);
        FastAsserts.assertLessThan(cellsIterator.x, World.WIDTH_IN_CELLS,
                "There are no space for NeighborCell!");

        Cell cell = World.getArea(0, 0).getCell(cellsIterator.x, cellsIterator.y);
        clearCellForTest(cell);
        return cell;
    }

    public static void makePlayerTeamInvincible() {
        LOG.info("makePlayerTeamInvincible");
        HumanSquad armyGarantsThatPlayerTeamWillNotBeKilled = WorldServices.createHumanSquadWithSomeUnit(
                World.getPlayerTeam(), WorldSteps.getNextStandaloneCell());
        for (Unit unit : WorldServices.createSomeHumanUnits(
                AreaObject.UNITS_AMOUNT_LIMIT - armyGarantsThatPlayerTeamWillNotBeKilled.getUnits().size)) {
            WorldSteps.makeUnitInvincible(unit);
            armyGarantsThatPlayerTeamWillNotBeKilled.getUnitsController().addUnit(unit);
        }
        armyGarantsThatPlayerTeamWillNotBeKilled.validate();
    }

    public static void rewindTeamsToStartNewWorldStep() {
        int curStep = World.step;

        while (World.step == curStep) {
            Team activeTeam = World.getActiveTeam();
            LOG.info("rewindTeamsToStartNewWorldStep.activeTeam: " + activeTeam);
            if (activeTeam != null && activeTeam.isHumanPlayer()) {
                // double check to skip real multi-threading implementations
                if (World.step == curStep) {
                    World.nextTeamTurn();
                }
            }
            Utils.sleepThread(200);
            AbstractTests.checkAndResetGameThreadCrash();
        }
    }

    public static void rewindWorldSteps(int steps) {
        for (int i = 0; i < steps; i++) {
            LOG.info("rewind world step: " + i);
            WorldSteps.rewindTeamsToStartNewWorldStep();
            Utils.sleepThread(500);
        }
    }

    public static void moveObjectOnCell(AreaObject areaObject, Cell cell) {
        areaObject.moveOn(cell);
    }

    public static void updateUnitVitality(Unit unit, int onValue) {
        unit.getParams().updateVitality(onValue);
    }

    public static void makeUnitInvincible(Unit unit) {
        LOG.info("makeUnitInvincible: " + unit);
        unit.getParams().updateVitality(10000);
        unit.getParams().updateHealthToVitality();
    }


    public static void addUnitToAreaObject(Unit someUnit, AreaObject areaObject) {
        areaObject.getUnitsController().addUnitAndValidate(someUnit);
    }

    public static void addUnitsToAreaObject(Array<Unit> units, AreaObject areaObject) {
        areaObject.getUnitsController().addUnitsAndValidate(units);
    }

    public static void removeAndValidateUnitFromAreaObject(Unit someUnit, AreaObject areaObject) {
        areaObject.getUnitsController().removeAndValidate(someUnit);
    }

    public static void removeUnitFromAreaObject(Unit someUnit, AreaObject areaObject) {
        areaObject.getUnitsController().remove(someUnit);
    }

    public static void setUnitTemperature(Unit unit, int t) {
        unit.setTemperature(t);

    }

    public static void addDefaultEquipmentPackageToInventory(BasicInventory inventory) {
        inventory.addItem(StickItem.class, 1);
        inventory.addItem(BowItem.class, 1);
        inventory.addItem(ArrowItem.class, 1);
        inventory.addItem(SkinItem.class, 1);
    }

    public static void teamLearnsSkill(Team humanTeam, Class<? extends AbstractSkill> skillClass) {
        humanTeam.getTeamSkillsManager().getSkill(skillClass).learn();
    }

    public static void addItemToInventory(Class<? extends AbstractInventoryItem> itemClass, int qty, BasicInventory inventory) {
        inventory.addItem(itemClass, qty);
    }

    public static void makeUnitCouldEquipItemsFromDefaultEquipmentPackage(Unit unit) {
        makeUnitCouldEquipItem(unit, DefaultPackageItem.MELEE.getInventoryItemClass());
        makeUnitCouldEquipItem(unit, DefaultPackageItem.RANGE.getInventoryItemClass());
        makeUnitCouldEquipItem(unit, DefaultPackageItem.BULLETS.getInventoryItemClass());
        makeUnitCouldEquipItem(unit, DefaultPackageItem.CLOTHES.getInventoryItemClass());
    }

    public static void makeUnitCouldEquipItem(Unit unit, Class<? extends AbstractInventoryItem> itemClass) {
        makeTeamCouldUseItem(unit.getAreaObject().getTeam(), itemClass);
    }

    public static void makeTeamCouldUseItem(Team team, Class<? extends AbstractInventoryItem> itemClass) {
        try {
            makeTeamCouldUseItem(team, itemClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            Utils.error(e);
        }
    }

    public static void makeTeamCouldUseItemsFromInventory(Team team, BasicInventory inventory) {
        for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> inventoryItemEntry : inventory.getItemsIterator()) {
            makeTeamCouldUseItem(team, inventoryItemEntry.value);
        }
    }

    public static void makeTeamCouldUseItem(Team team, AbstractInventoryItem inventoryItem) {
        Class<? extends AbstractSkill> requiredSkillLearned = inventoryItem.getParams().getRequiredSkillLearned();
        if (requiredSkillLearned == null) {
            return;
        }

        AbstractSkill reqSkill = team.getTeamSkillsManager().getSkill(requiredSkillLearned);
        if (reqSkill.isNotLearnStarted()) {
            reqSkill.learn();
        }
    }

    public static void makeTeamCouldUseRecipes(Team team, Array<CraftRecipe> recipesList) {
        Array<CraftRecipe> craftRecipesCopy = new Array<>(recipesList);
        for (CraftRecipe craftRecipe : craftRecipesCopy) {
            makeTeamCouldUseRecipe(team, craftRecipe);
        }
    }

    public static void makeTeamCouldUseRecipe(Team team, CraftRecipe recipe) {
        makeTeamCouldUseItem(team, recipe.getFinalItemClass());
    }

    public static void addItemsNeededForCraft(AreaObject areaObject, Array<CraftRecipe> recipes) {
        LOG.info("addItemsNeededForCraft: " + recipes);
        Array<CraftRecipe> craftRecipesCopy = new Array<>(recipes);
        assertThat(recipes).isNotEmpty();
        for (CraftRecipe recipe : craftRecipesCopy) {
            addItemsNeededForCraft(areaObject, recipe);
        }
    }

    public static void addItemsNeededForCraft(AreaObject areaObject, CraftRecipe recipe) {
        LOG.info("addItemsNeededForCraft " + recipe);
        Array<CraftRecipe.Consumable> consumablesCopy = new Array<>(recipe.getConsumables());
        for (CraftRecipe.Consumable consumable : consumablesCopy) {
            LOG.info(" consumable " + consumable);
            addItemToInventory(consumable.getItemClass(), consumable.getQuantity(), areaObject.getMainInventory());
        }
    }

    public static void addItemsNeededForCraft(AreaObject areaObject, Class<? extends AbstractInventoryItem> itemClass) {
        addItemsNeededForCraft(areaObject, CraftInventory.getRecipeForItem(itemClass));
    }

    public static void craftAll(AreaObject areaObject, Array<CraftRecipe> recipes) {
        LOG.info("craftAll > " + recipes);
        Array<CraftRecipe> craftRecipesCopy = new Array<>(recipes);
        for (CraftRecipe recipe : craftRecipesCopy) {
            craft(areaObject, recipe);
        }
    }

    public static void craft(AreaObject areaObject, CraftRecipe craftRecipe) {
        LOG.info("craft > " + craftRecipe);
        areaObject.getCraftInventory().craft(craftRecipe.getFinalItemClass(), 1);
    }

    public static void craft(AreaObject areaObject, Class<? extends AbstractInventoryItem> itemClass) {
        craft(areaObject, CraftInventory.getRecipeForItem(itemClass));
    }

    public static void placeAnimalHerdsAround(AreaObject areaObject, int howManyToPlace) {
        Array<Cell> neighborCells = areaObject.getCell().getNeighborCells();
        int placedAround = 0;
        for (Cell neighborCell : neighborCells) {
            if (neighborCell.couldBeSeized()) {
                WorldServices.createAnimalHerd(neighborCell);
                placedAround++;
                if (placedAround == howManyToPlace) {
                    break;
                }
            }
        }
        Assert.assertEquals(placedAround, howManyToPlace);
    }

    public static void makePerfectConditionsOnCell(Cell cell) {
        cell.setTemperature(Unit.HEALTHY_TEMPERATURE_MIN);
        cell.setFood(AreaObject.UNITS_AMOUNT_LIMIT);
        cell.setWater(AreaObject.UNITS_AMOUNT_LIMIT);
    }

    public static void executeRangeAttackWithoutMiss(AbstractSquad squad1, AbstractSquad squadTarget) {
        GdxgConstants.NO_ATTACK_MISS = true;
        squad1.executeRangeAttack(squadTarget);
        GdxgConstants.NO_ATTACK_MISS = false;
    }


    public enum DefaultPackageItem {
        MELEE(StickItem.class), RANGE(BowItem.class), BULLETS(ArrowItem.class), CLOTHES(SkinItem.class);

        private Class<? extends AbstractInventoryItem> inventoryItemClass;

        DefaultPackageItem(Class<? extends AbstractInventoryItem> inventoryItemClass) {
            this.inventoryItemClass = inventoryItemClass;
        }

        public Class<? extends AbstractInventoryItem> getInventoryItemClass() {
            return inventoryItemClass;
        }
    }
}
