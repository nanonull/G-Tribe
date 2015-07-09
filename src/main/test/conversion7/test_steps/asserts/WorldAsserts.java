package conversion7.test_steps.asserts;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.engine.ClientCore;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.Area;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.BasicInventory;
import conversion7.game.stages.world.inventory.CraftRecipe;
import conversion7.game.stages.world.inventory.MainInventory;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.inventory.items.types.ClothesItem;
import conversion7.game.stages.world.inventory.items.types.MeleeWeaponItem;
import conversion7.game.stages.world.inventory.items.types.RangeBulletItem;
import conversion7.game.stages.world.inventory.items.types.RangeWeaponItem;
import conversion7.game.stages.world.objects.AbstractSquad;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.TownFragment;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;
import conversion7.game.stages.world.objects.controllers.UnitsController;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.TeamClassesManager;
import conversion7.game.stages.world.team.UnitClassTeamInfo;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;
import conversion7.game.stages.world.view.AreaViewer;
import conversion7.test_steps.WorldSteps;
import org.slf4j.Logger;
import org.fest.assertions.api.Fail;

import java.util.Iterator;

import static org.fest.assertions.api.Assertions.assertThat;

public class WorldAsserts {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static void assertUnitDead(Unit unit) {
        assertThat(unit.getParams().getHealth()).isLessThanOrEqualTo(0);
        assertThat(unit.getAreaObject().getUnits().contains(unit, true)).isFalse();
    }

    public static void assertUnitAlive(Unit unit) {
        assertThat(unit.getParams().getHealth()).isGreaterThan(0);
        assertThat(unit.getAreaObject().getUnits().contains(unit, true)).isTrue();
    }

    public static void assertAreaObjectContainsUnit(AreaObject areaObject, Unit unit) {
        assertThat(areaObject.getUnits().contains(unit, true)).isTrue();
    }

    public static void assertAreaObjectDoesntContainUnit(AreaObject areaObject, Unit unit) {
        assertThat(areaObject.getUnits().contains(unit, true)).isFalse();
    }

    public static void assertAreaObjectContainsUnits(AreaObject areaObject, Array<Unit> units) {
        for (Unit unit : units) {
            assertAreaObjectContainsUnit(areaObject, unit);
        }
    }

    public static void assertControllerIsValidated(UnitsController unitsController) {
        assertThat(unitsController.isValidated()).isTrue();
    }

    public static void assertControllerIsNotValidated(UnitsController unitsController) {
        assertThat(unitsController.isValidated()).isFalse();
    }

    public static void assertAreaObjectAlive(AreaObject areaObject) {
        if (areaObject.isSquad()) {
            assertThat(areaObject.getTeam().getArmies()
                    .contains((AbstractSquad) areaObject, true)).isTrue();
        } else if (areaObject.isTownFragment()) {
            assertThat(areaObject.getTeam().getTownFragments()
                    .contains((TownFragment) areaObject, true)).isTrue();
        } else {
            Fail.fail("unknown object type: " + areaObject.getClass());
        }
    }

    public static void assertAreaObjectDefeated(AreaObject areaObject) {
        assertThat(areaObject.isRemovedFromWorld()).isTrue();
        if (areaObject.isSquad()) {
            assertThat(areaObject.getTeam().getArmies()
                    .contains((AbstractSquad) areaObject, true)).isTrue();
        } else if (areaObject.isTownFragment()) {
            assertThat(areaObject.getTeam().getTownFragments()
                    .contains((TownFragment) areaObject, true)).isTrue();
        } else {
            Fail.fail("unknown object type: " + areaObject.getClass());
        }
    }

    public static void assertTeamAlive(Team team) {
        assertThat(World.TEAMS.contains(team, true)).isTrue();
    }

    public static void assertTeamDefeated(Team team) {
        assertThat(World.TEAMS.contains(team, true)).isTrue();
        assertThat(team.isDefeated()).isTrue();
    }

    public static void assertTeamClassesManagerContainsInfoAbout(TeamClassesManager teamClassesManager,
                                                                 Class<? extends Unit> aClass, int unitsAmountForThisClass) {
        UnitClassTeamInfo unitClassTeamInfo = teamClassesManager.getTeamInfo(aClass);
        assertThat(unitClassTeamInfo).isNotNull();
        assertThat(unitClassTeamInfo.getAmount()).isEqualTo(unitsAmountForThisClass);
    }

    public static void assertTeamControllerContainsUnit(TeamClassesManager teamClassesManager, Unit unit) {
        assertThat(teamClassesManager.getAllTeamUnits()).contains(unit);
    }

    public static void assertAreaObjectFoodStorageIs(AreaObject areaObject, int food) {
        assertThat(areaObject.getFoodStorage().getFood()).isEqualTo(food);
    }

    public static void assertWorldIsActiveStage() {
        assertThat(ClientCore.core.getActiveStage()).isInstanceOf(AreaViewer.class);
    }

    public static void assertUnitHasDamage(Unit unit, int damage) {
        LOG.info("assertUnitHasDamage: " + damage);
        assertThat(damage).isGreaterThan(0);
        assertThat(unit.getMeleeDamage()).isEqualTo(damage);
    }

    public static void assertUnitHasDamageGreaterThan(Unit unit, int target) {
        LOG.info("assertUnitHasDamageGreaterThan: " + target);
        assertThat(unit.getMeleeDamage()).isGreaterThan(target);
    }

    public static void assertUnitHasDefence(Unit unit, int def) {
        LOG.info("assertUnitHasDefence: " + def);
        assertThat(def).isGreaterThan(0);
        assertThat(unit.getDefence()).isEqualTo(def);
    }

    public static void assertUnitHasHealth(Unit unit, int health) {
        LOG.info("assertUnitHasHealth: " + health);
        assertThat(health).isGreaterThan(0);
        assertThat(unit.getParams().getHealth()).isEqualTo(health);
    }

    public static void assertUnitHasDefenceGreaterThan(Unit unit, int def) {
        LOG.info("assertUnitHasDefenceGreaterThan: " + def);
        assertThat(unit.getDefence()).isGreaterThan(def);
    }

    public static void assertInventoryContainsItem(BasicInventory inventory,
                                                   AbstractInventoryItem item) {
        assertInventoryContainsItem(inventory, item.getClass(), item.getQuantity());
    }

    public static void assertInventoryContainsItem(BasicInventory inventory,
                                                   Class<? extends AbstractInventoryItem> itemClass, int qty) {
        LOG.info(String.format("assertInventoryContainsItem > %s contains: %s, qty=%d",
                inventory.getClass().getSimpleName(), itemClass.getSimpleName(), qty));
        assertThat(inventory.getItem(itemClass)).isNotNull();
        assertThat(inventory.getItem(itemClass).getQuantity()).isEqualTo(qty);
    }

    public static void assertInventoryContainsItemsFrom(BasicInventory inventory, Array<CraftRecipe> recipesList) {
        for (CraftRecipe craftRecipe : recipesList) {
            assertInventoryContainsItem(inventory, craftRecipe.getFinalItemClass(), craftRecipe.getFinalItemQuantityPerCraft());
        }
    }

    public static void assertInventoryContainsCraftedItemsOnly(BasicInventory inventory, Array<CraftRecipe> recipesList) {
        LOG.info("assertInventoryContainsCraftedItemsOnly > " + recipesList);
        Array<AbstractInventoryItem> items = new Array<>();
        for (CraftRecipe craftRecipe : recipesList) {
            AbstractInventoryItem inventoryItem = null;
            try {
                inventoryItem = craftRecipe.getFinalItemClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                Utils.error(e);
            }
            assert inventoryItem != null;
            inventoryItem.setQuantity(craftRecipe.getFinalItemQuantityPerCraft());
            items.add(inventoryItem);
        }
        assertInventoryContainsItemsOnly(inventory, items);
    }

    public static void assertInventoryContainsItemsOnly(BasicInventory inventory, Array<AbstractInventoryItem> expectedItems) {
        ObjectMap<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> actualItemsCopy = new ObjectMap();
        inventory.getItems(actualItemsCopy);

        Iterator<AbstractInventoryItem> expItemsIterator = expectedItems.iterator();
        while (expItemsIterator.hasNext()) {
            AbstractInventoryItem item = expItemsIterator.next();
            assertInventoryContainsItem(inventory, item);
            actualItemsCopy.remove(item.getClass());
            expItemsIterator.remove();
        }

        assertThat(actualItemsCopy).isEmpty();
    }

    public static void assertInventoryContainsItemsAtLeast(MainInventory inventory, Array<AbstractInventoryItem> inventoryItems) {
        for (AbstractInventoryItem inventoryItem : inventoryItems) {
            assertInventoryContainsItemAtLeast(inventory, inventoryItem.getClass(), inventoryItem.getQuantity());
        }

    }

    public static void assertInventoryContainsItemAtLeast(BasicInventory inventory,
                                                          Class<? extends AbstractInventoryItem> itemClass, int qtyAtLeast) {
        LOG.info(String.format("assertInventoryContainsItem > %s contains: %s, qtyAtLeast=%d",
                inventory.getClass().getSimpleName(), itemClass.getSimpleName(), qtyAtLeast));
        assertThat(inventory.getItem(itemClass)).isNotNull();
        assertThat(inventory.getItem(itemClass).getQuantity()).isGreaterThanOrEqualTo(qtyAtLeast);
    }

    public static void assertInventoryIsEmpty(BasicInventory inventory) {
        assertThat(inventory.isEmpty()).isTrue();
    }

    public static void assertInventoryDoesntContainItem(BasicInventory inventory, Class<? extends AbstractInventoryItem> itemClass) {
        assertThat(inventory.getItem(itemClass)).isNull();
    }

    public static void assertUnitHasNoEffect(Unit unit, Class<? extends AbstractUnitEffect> effectClass) {
        assertThat(unit.getEffectManager().containsEffect(effectClass)).isFalse();
    }

    public static void assertUnitHasEffect(Unit unit, Class<? extends AbstractUnitEffect> effectClass) {
        assertThat(unit.getEffectManager().containsEffect(effectClass)).isTrue();
    }

    public static void assertEffectHasTickCounter(AbstractUnitEffect effect, int expCounter) {
        assertThat(effect.getTickCounter()).isEqualTo(expCounter);
    }

    public static void assertUnitHasTemperature(Unit unit, int t) {
        assertThat(unit.isAlive()).as("restart test!").isTrue();
        assertThat(unit.getTemperature()).isEqualTo(t);
    }

    public static void assertPlayerTeamIsNotDefeated() {
        assertThat(World.getPlayerTeam()).isNotNull();
    }

    public static void assertUnitHasEquipedMeleeWeapon(Unit unit, Class<? extends MeleeWeaponItem> aClass) {
        assertThat(unit.getEquipment().getMeleeWeaponItem()).isNotNull();
        assertThat(unit.getEquipment().getMeleeWeaponItem()).isInstanceOf(aClass);
        assertThat(unit.getEquipment().getMeleeWeaponItem().getQuantity()).isEqualTo(1);
    }

    public static void assertUnitHasNoEquippedMeleeWeapon(Unit unit) {
        assertThat(unit.getEquipment().getMeleeWeaponItem()).isNull();
    }

    public static void assertUnitHasEquipedRangeWeapon(Unit unit, Class<? extends RangeWeaponItem> aClass) {
        assertThat(unit.getEquipment().getRangeWeaponItem()).isNotNull();
        assertThat(unit.getEquipment().getRangeWeaponItem()).isInstanceOf(aClass);
        assertThat(unit.getEquipment().getRangeWeaponItem().getQuantity()).isEqualTo(1);
    }

    public static void assertUnitHasNoEquippedRangeWeapon(Unit unit) {
        assertThat(unit.getEquipment().getRangeWeaponItem()).isNull();
    }

    public static void assertUnitHasEquipedRangeBullets(Unit unit, Class<? extends RangeBulletItem> aClass) {
        assertThat(unit.getEquipment().getRangeBulletsItem()).isNotNull();
        assertThat(unit.getEquipment().getRangeBulletsItem()).isInstanceOf(aClass);
    }

    public static void assertUnitHasEquipedRangeBullets(Unit unit, Class<? extends RangeBulletItem> aClass, int amount) {
        assertThat(unit.getEquipment().getRangeBulletsItem()).isNotNull();
        assertThat(unit.getEquipment().getRangeBulletsItem()).isInstanceOf(aClass);
        assertThat(unit.getEquipment().getRangeBulletsItem().getQuantity()).isEqualTo(amount);
    }

    public static void assertUnitHasNoEquippedRangeBullets(Unit unit) {
        assertThat(unit.getEquipment().getRangeBulletsItem()).isNull();
    }

    public static void assertUnitHasEquipedClothes(Unit unit, Class<? extends ClothesItem> aClass) {
        assertThat(unit.getEquipment().getClothesItem()).isNotNull();
        assertThat(unit.getEquipment().getClothesItem()).isInstanceOf(aClass);
        assertThat(unit.getEquipment().getClothesItem().getQuantity()).isEqualTo(1);
    }

    public static void assertUnitHasNoEquippedClothes(Unit unit) {
        assertThat(unit.getEquipment().getClothesItem()).isNull();
    }

    public static void assertInventoryDoesntContainDefaultEquipmentPackage(BasicInventory inventory) {
        assertInventoryDoesntContainItem(inventory, WorldSteps.DefaultPackageItem.MELEE.getInventoryItemClass());
        assertInventoryDoesntContainItem(inventory, WorldSteps.DefaultPackageItem.RANGE.getInventoryItemClass());
        assertInventoryDoesntContainItem(inventory, WorldSteps.DefaultPackageItem.BULLETS.getInventoryItemClass());
        assertInventoryDoesntContainItem(inventory, WorldSteps.DefaultPackageItem.CLOTHES.getInventoryItemClass());
    }

    public static void assertInventoryContainsDefaultEquipmentPackage(BasicInventory inventory) {
        assertInventoryContainsItem(inventory, WorldSteps.DefaultPackageItem.MELEE.getInventoryItemClass(), 1);
        assertInventoryContainsItem(inventory, WorldSteps.DefaultPackageItem.RANGE.getInventoryItemClass(), 1);
        assertInventoryContainsItem(inventory, WorldSteps.DefaultPackageItem.BULLETS.getInventoryItemClass(), 1);
        assertInventoryContainsItem(inventory, WorldSteps.DefaultPackageItem.CLOTHES.getInventoryItemClass(), 1);
    }

    public static void assertUnitHasEquippedDefaultEquipmentPackage(Unit unit) {
        WorldAsserts.assertUnitHasEquipedMeleeWeapon(unit, (Class<? extends MeleeWeaponItem>)
                WorldSteps.DefaultPackageItem.MELEE.getInventoryItemClass());
        WorldAsserts.assertUnitHasEquipedRangeWeapon(unit, (Class<? extends RangeWeaponItem>)
                WorldSteps.DefaultPackageItem.RANGE.getInventoryItemClass());
        WorldAsserts.assertUnitHasEquipedRangeBullets(unit, (Class<? extends RangeBulletItem>)
                WorldSteps.DefaultPackageItem.BULLETS.getInventoryItemClass(), 1);
        WorldAsserts.assertUnitHasEquipedClothes(unit, (Class<? extends ClothesItem>)
                WorldSteps.DefaultPackageItem.CLOTHES.getInventoryItemClass());
    }


    public static void assertHaveConsumablesToCraft(AreaObject areaObject, Array<CraftRecipe> recipes) {
        LOG.info(String.format("assertHaveConsumablesToCraft > %s \n%s", areaObject, recipes));
        for (CraftRecipe recipe : recipes) {
            assertInventoryContainsItemAtLeast(areaObject.getCraftInventory(), recipe.getFinalItemClass(), recipe.getFinalItemQuantityPerCraft());
        }

    }

    public static void assertAreaHasAreaCoords(Area area, int x, int y) {
        assertThat(area.worldPosInAreas.x).isEqualTo(x);
        assertThat(area.worldPosInAreas.y).isEqualTo(y);
    }

    public static void assertAreaObjectHasAction(AreaObject areaObject, Class<? extends AbstractAreaObjectAction> actionClass) {
        assertThat(areaObject.getAction(actionClass)).isNotNull();
    }

    public static void assertAreaObjectHasNoAction(AreaObject areaObject, Class<? extends AbstractAreaObjectAction> actionClass) {
        assertThat(areaObject.getAction(actionClass)).isNull();
    }
}
