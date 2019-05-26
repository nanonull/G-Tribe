package shared.asserts

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import conversion7.aop.TestSteps
import conversion7.engine.Gdxg
import conversion7.engine.utils.Utils
import conversion7.game.GdxgConstants
import conversion7.game.stages.world.area.Area
import conversion7.game.stages.world.inventory.BasicInventory
import conversion7.game.stages.world.inventory.CraftRecipe
import conversion7.game.stages.world.inventory.items.types.*
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.objects.AreaObject
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction
import conversion7.game.stages.world.objects.actions.items.RangeAttackAction
import conversion7.game.stages.world.objects.actions.items.RitualAction
import conversion7.game.stages.world.objects.buildings.Camp
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.TeamClassesManager
import conversion7.game.stages.world.team.UnitClassTeamInfo
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitParameterType
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect
import conversion7.game.stages.world.view.AreaViewer
import org.fest.assertions.api.Fail
import org.slf4j.Logger
import shared.steps.WorldSteps

import static org.fest.assertions.api.Assertions.assertThat

@TestSteps
class WorldAsserts {

    private static final Logger LOG = Utils.getLoggerForClass()

    static void assertUnitDead(Unit unit) {
        assertThat(unit.isAlive()).isFalse()
        assertThat(unit.getTotalParam(UnitParameterType.HEALTH)).isLessThanOrEqualTo(0)
    }

    static void assertUnitAlive(Unit unit) {
        assertThat(unit.isAlive()).isTrue()
        assertThat(unit.getTotalParam(UnitParameterType.HEALTH)).isGreaterThan(0)
    }

    static void assertAreaObjectAlive(AreaObject areaObject) {
        if (areaObject.isSquad()) {
            assertUnitAlive(((AbstractSquad) areaObject).unit)
        } else if (areaObject.isTownFragment()) {
            assertThat(areaObject.getTeam().getCamps()
                    .contains((Camp) areaObject, true)).isTrue()
        } else {
            Fail.fail("unknown object type: " + areaObject.getClass())
        }
    }

    static void assertAreaObjectDefeated(AreaObject areaObject) {
        assertAreaObjectDefeated(areaObject, true)
    }

    static void assertAreaObjectDefeated(AreaObject areaObject, boolean defeatedFlag) {
        assertAreaObjectDefeated(areaObject, defeatedFlag, false)
    }

    /** @param afterNewStepStarted means new world step started and object was removed from team                                              */

    static void assertAreaObjectDefeated(AreaObject areaObject, boolean defeatedFlag, boolean afterNewStepStarted) {
        assertThat(areaObject.isRemovedFromWorld()).as("isRemovedFromWorld").isEqualTo(defeatedFlag)
        // will be removed from team on world loop validation
        boolean teamContainsObject = true
        if (defeatedFlag && afterNewStepStarted) {
            teamContainsObject = false
        }
        if (areaObject.isSquad()) {
            assertThat(areaObject.getTeam().getSquads().contains((AbstractSquad) areaObject, true))
                    .as("Team contains AreaObject").isEqualTo(teamContainsObject)
        } else if (areaObject.isTownFragment()) {
            assertThat(areaObject.getTeam().getCamps().contains((Camp) areaObject, true))
                    .as("Team contains Camp").isEqualTo(teamContainsObject)
        } else {
            Fail.fail("unknown object type: " + areaObject.getClass())
        }
    }

    static void assertTeamAlive(Team team) {
        assertThat(Gdxg.core.world.teams.contains(team, true)).isTrue()
    }

    static void assertTeamDefeated(Team team) {
        assertTeamDefeated(team, true)
    }

    static void assertTeamDefeated(Team team, boolean shouldBeDefeated) {
        assertTeamDefeated(team, shouldBeDefeated, true)
    }

    /**use checkOnNewWorldStepStarted = false if world.finishStep() was not called, otherwise use true*/
    static void assertTeamDefeated(Team team, boolean shouldBeDefeated, boolean checkOnNewWorldStepStarted) {
        boolean worldContainsObject = true
        if (shouldBeDefeated && checkOnNewWorldStepStarted) {
            worldContainsObject = false
        }
        assert Gdxg.core.world.teams.contains(team, true) == worldContainsObject
        assert team.isDefeated() == shouldBeDefeated
    }

    static void assertTeamClassesManagerContainsInfoAbout(TeamClassesManager teamClassesManager,
                                                          Class<Unit> aClass, Integer unitsAmountForThisClass) {
        UnitClassTeamInfo unitClassTeamInfo = teamClassesManager.getTeamInfo(aClass)
        if (unitsAmountForThisClass == null) {
            assert unitClassTeamInfo == null
        } else {
            assert unitClassTeamInfo != null
            assert unitClassTeamInfo.getAmount() == unitsAmountForThisClass
        }
    }

    static void assertTeamControllerContainsUnit(boolean isTrue
                                                 , TeamClassesManager teamClassesManager
                                                 , Unit unit) {
        assert teamClassesManager.getAllTeamUnits().contains(unit) == isTrue
    }

    static void assertWorldIsActiveStage() {
        assertThat(Gdxg.core.getActiveStage()).isInstanceOf(AreaViewer.class)
    }

    static void assertUnitHasDamage(Unit unit, int damage) {
        LOG.info("assertUnitHasDamage: " + damage)
        assertThat(damage).isGreaterThan(0)
        assertThat(unit.getMeleeDamage()).isEqualTo(damage)
    }

    static void assertUnitHasDamageGreaterThan(Unit unit, int target) {
        LOG.info("assertUnitHasDamageGreaterThan: " + target)
        assertThat(unit.getMeleeDamage()).isGreaterThan(target)
    }

    static void assertUnitHasDefence(Unit unit, int defence) {
        LOG.info("assertUnitHasDefence: " + defence)
        assert defence > 0
        assert unit.getDefence() == defence
    }

    static void assertUnitHasFullHealth(Unit unit) {
        LOG.info("assertUnitHasFullHealth: ")
        assertUnitHasHealth(unit, unit.getMaxHealth())
    }

    static void assertUnitHasHealth(Unit unit, int health) {
        LOG.info("assertUnitHasHealth: " + health)
        assertThat(health).isGreaterThan(0)
        assertThat(unit.getBaseParams().get(UnitParameterType.HEALTH)).isEqualTo(health)
    }

    static void assertUnitHasDefenceGreaterThan(Unit unit, int defence) {
        LOG.info("assertUnitHasDefenceGreaterThan: " + defence)
        assertThat(unit.getDefence()).isGreaterThan(defence)
    }

    static void assertInventoryContainsItemsFrom(BasicInventory inventory, Array<CraftRecipe> recipesList) {
        for (CraftRecipe craftRecipe : recipesList) {
            assertInventoryContainsItem(inventory, craftRecipe.getFinalItemClass(), craftRecipe.getFinalItemQuantityPerCraft())
        }
    }

    static void assertInventoryContainsItem(BasicInventory inventory,
                                            Class<? extends AbstractInventoryItem> itemClass, int qty) {
        LOG.info(String.format("assertInventoryContainsItem > %s contains: %s, qty=%d",
                inventory.getClass().getSimpleName(), itemClass.getSimpleName(), qty))
        assert inventory.getItem(itemClass)
        assert inventory.getItem(itemClass).getQuantity() == qty
    }


    static void assertInventoryContainsCraftedItemsOnly(BasicInventory inventory, Array<CraftRecipe> recipesList) {
        LOG.info("assertInventoryContainsCraftedItemsOnly > " + recipesList)
        Array<AbstractInventoryItem> items = new Array<>()
        for (CraftRecipe craftRecipe : recipesList) {
            AbstractInventoryItem inventoryItem = null
            try {
                inventoryItem = craftRecipe.getFinalItemClass().newInstance()
            } catch (InstantiationException | IllegalAccessException e) {
                Utils.error(e)
            }
            assert inventoryItem != null
            inventoryItem.setQuantity(craftRecipe.getFinalItemQuantityPerCraft())
            items.add(inventoryItem)
        }
        assertInventoryContainsItemsOnly(inventory, items)
    }


    static void assertInventoryContainsItemsOnly(BasicInventory inventory, Array<AbstractInventoryItem> expectedItems) {
        ObjectMap<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> actualItemsCopy = new ObjectMap()
        inventory.getItems(actualItemsCopy)

        Iterator<AbstractInventoryItem> expItemsIterator = expectedItems.iterator()
        while (expItemsIterator.hasNext()) {
            AbstractInventoryItem item = expItemsIterator.next()
            assertInventoryContainsItem(inventory, item)
            actualItemsCopy.remove((Class<? extends AbstractInventoryItem>) item.getClass())
            expItemsIterator.remove()
        }

        assertThat(actualItemsCopy).isEmpty()
    }

    static void assertInventoryContainsItem(BasicInventory inventory,
                                            AbstractInventoryItem item) {
        assertInventoryContainsItem(inventory, item.getClass(), item.getQuantity())
    }

    static void assertInventoryContainsItemAtLeast(BasicInventory inventory,
                                                   Class<? extends AbstractInventoryItem> itemClass, int qtyAtLeast) {
        LOG.info(String.format("assertInventoryContainsItem > %s contains: %s, qtyAtLeast=%d",
                inventory.getClass().getSimpleName(), itemClass.getSimpleName(), qtyAtLeast))
        assertThat(inventory.getItem(itemClass)).isNotNull()
        assertThat(inventory.getItem(itemClass).getQuantity()).isGreaterThanOrEqualTo(qtyAtLeast)
    }

    static void assertInventoryIsEmpty(BasicInventory inventory) {
        assertThat(inventory.isEmpty()).isTrue()
    }

    static void assertUnitHasNoEffect(Unit unit, Class<? extends AbstractUnitEffect> effectClass) {
        assert !(unit.getEffectManager().containsEffect(effectClass))
    }

    static void assertUnitHasEffect(Unit unit, Class<? extends AbstractUnitEffect> effectClass) {
        assert (unit.getEffectManager().containsEffect(effectClass))
    }

    static void assertUnitEffectHasTickCounter(AbstractUnitEffect effect, int expCounter) {
        assertThat(effect.getTickCounter()).isEqualTo(expCounter)
    }

    static void assertPlayerTeamIsNotDefeated() {
//        assertTeamDefeated(WorldStubs.getPlayerTeam(), false)
    }

    static void assertUnitHasNoEquippedMeleeWeapon(Unit unit) {
        assertThat(unit.getEquipment().getMeleeWeaponItem()).isNull()
    }

    static void assertUnitHasNoEquippedRangeWeapon(Unit unit) {
        assertThat(unit.getEquipment().getRangeWeaponItem()).isNull()
    }

    static void assertUnitHasEquipedRangeBullets(Unit unit, Class<? extends RangeBulletItem> aClass) {
        assertThat(unit.getEquipment().getRangeBulletsItem()).isNotNull()
        assertThat(unit.getEquipment().getRangeBulletsItem()).isInstanceOf(aClass)
    }

    static void assertUnitHasNoEquippedRangeBullets(Unit unit) {
        assertThat(unit.getEquipment().getRangeBulletsItem()).isNull()
    }

    static void assertUnitHasNoEquippedClothes(Unit unit) {
        assertThat(unit.getEquipment().getClothesItem()).isNull()
    }

    static void assertInventoryDoesntContainDefaultEquipmentPackage(BasicInventory inventory) {
        assertInventoryDoesntContainItem(inventory, WorldSteps.DefaultPackageItem.MELEE.getInventoryItemClass())
        assertInventoryDoesntContainItem(inventory, WorldSteps.DefaultPackageItem.RANGE.getInventoryItemClass())
        assertInventoryDoesntContainItem(inventory, WorldSteps.DefaultPackageItem.BULLETS.getInventoryItemClass())
        assertInventoryDoesntContainItem(inventory, WorldSteps.DefaultPackageItem.CLOTHES.getInventoryItemClass())
    }


    static void assertInventoryDoesntContainItem(BasicInventory inventory, Class<? extends AbstractInventoryItem> itemClass) {
        assert inventory.getItem(itemClass) == null
    }

    static void assertInventoryContainsDefaultEquipmentPackage(BasicInventory inventory) {
        assertInventoryContainsItem(inventory, WorldSteps.DefaultPackageItem.MELEE.getInventoryItemClass(), 1)
        assertInventoryContainsItem(inventory, WorldSteps.DefaultPackageItem.RANGE.getInventoryItemClass(), 1)
        assertInventoryContainsItem(inventory, WorldSteps.DefaultPackageItem.BULLETS.getInventoryItemClass(), 1)
        assertInventoryContainsItem(inventory, WorldSteps.DefaultPackageItem.CLOTHES.getInventoryItemClass(), 1)
    }

    static void assertUnitHasEquippedDefaultEquipmentPackage(Unit unit) {
        WorldAsserts.assertUnitHasEquipedMeleeWeapon(unit, (Class<? extends MeleeWeaponItem>)
                WorldSteps.DefaultPackageItem.MELEE.getInventoryItemClass())
        WorldAsserts.assertUnitHasEquipedRangeWeapon(unit, (Class<? extends RangeWeaponItem>)
                WorldSteps.DefaultPackageItem.RANGE.getInventoryItemClass())
        WorldAsserts.assertUnitHasEquipedRangeBullets(unit, (Class<? extends RangeBulletItem>)
                WorldSteps.DefaultPackageItem.BULLETS.getInventoryItemClass(), 1)
        WorldAsserts.assertUnitHasEquipedClothes(unit, (Class<? extends ClothesItem>)
                WorldSteps.DefaultPackageItem.CLOTHES.getInventoryItemClass())
    }

    static void assertUnitHasEquipedMeleeWeapon(Unit unit, Class<? extends MeleeWeaponItem> aClass) {
        assertThat(unit.getEquipment().getMeleeWeaponItem()).isNotNull()
        assertThat(unit.getEquipment().getMeleeWeaponItem()).isInstanceOf(aClass)
        assertThat(unit.getEquipment().getMeleeWeaponItem().getQuantity()).isEqualTo(1)
    }

    static void assertUnitHasEquipedRangeWeapon(Unit unit, Class<? extends RangeWeaponItem> aClass) {
        assertThat(unit.getEquipment().getRangeWeaponItem()).isNotNull()
        assertThat(unit.getEquipment().getRangeWeaponItem()).isInstanceOf(aClass)
        assertThat(unit.getEquipment().getRangeWeaponItem().getQuantity()).isEqualTo(1)
    }


    static void assertUnitHasEquipedRangeBullets(Unit unit, Class<? extends RangeBulletItem> aClass, int amount) {
        assertThat(unit.getEquipment().getRangeBulletsItem()).isNotNull()
        assertThat(unit.getEquipment().getRangeBulletsItem()).isInstanceOf(aClass)
        assertThat(unit.getEquipment().getRangeBulletsItem().getQuantity()).isEqualTo(amount)
    }

    static void assertUnitHasEquipedClothes(Unit unit, Class<? extends ClothesItem> aClass) {
        assertThat(unit.getEquipment().getClothesItem()).isNotNull()
        assertThat(unit.getEquipment().getClothesItem()).isInstanceOf(aClass)
        assertThat(unit.getEquipment().getClothesItem().getQuantity()).isEqualTo(1)
    }

    static void assertHaveConsumablesToCraft(AbstractSquad areaObject, Array<CraftRecipe> recipes) {
        LOG.info(String.format("assertHaveConsumablesToCraft > %s \n%s", areaObject, recipes))
        for (CraftRecipe recipe : recipes) {
            assertInventoryContainsItemAtLeast(areaObject.getCraftInventory(), recipe.getFinalItemClass(), recipe.getFinalItemQuantityPerCraft())
        }

    }

    static void assertAreaHasAreaCoords(Area area, int x, int y) {
        assertThat(area.worldPosInAreas.x).isEqualTo(x)
        assertThat(area.worldPosInAreas.y).isEqualTo(y)
    }

    static void assertRangeAttackPossible(AbstractSquad abstractSquad) {
        assertAreaObjectHasAction(abstractSquad, RangeAttackAction.class)
        assertThat(abstractSquad.unit.canRangeAttack()).isTrue()
    }


    static void assertAreaObjectHasAction(AreaObject areaObject, Class<? extends AbstractAreaObjectAction> actionClass) {
        assert (areaObject.getActionsController().getAction(actionClass))
    }

    static void assertRangeAttackNotPossible(AbstractSquad abstractSquad) {
        assertAreaObjectHasNoAction(abstractSquad, RangeAttackAction.class)
        assert !(abstractSquad.unit.canRangeAttack())
    }


    static void assertAreaObjectHasNoAction(AreaObject areaObject, Class<? extends AbstractAreaObjectAction> actionClass) {
        assertThat(areaObject.getActionsController().getAction(actionClass)).isNull()
    }

    static void assertRitualPossible(AbstractSquad abstractSquad) {
        WorldAsserts.assertAreaObjectHasAction(abstractSquad, RitualAction.class)
    }

    static void assertRitualNotPossible(AbstractSquad abstractSquad) {
        WorldAsserts.assertAreaObjectHasNoAction(abstractSquad, RitualAction.class)
    }

    static void assertAreaObjectActionPointsIs(AbstractSquad areaObject, int ap) {
        assertThat(areaObject.unit.actionPoints).isEqualTo(ap)
    }

    static void assertUnitActionPointsIs(Unit unit, int ap) {
        assertThat(unit.getActionPoints()).isEqualTo(ap)

    }

    static void assertCellHasTown(Cell cell, boolean hasTown) {
        assertThat(cell.getCamp() != null).isEqualTo(hasTown)
    }

    static void assertTownConstructionCompleted(Camp town) {
        assertThat(town.isConstructionCompleted())
                .as("Camp Construction Completed, " + town.getConstructionProgress()
                        + "/" + Camp.CAMP_CONSTRUCTION_AP_TOTAL)
                .isTrue()
    }

    static void assertSquadCouldMove(AbstractSquad squad) {
        assertThat(GdxgConstants.DEVELOPER_MODE).as("Turn off DEVELOPER_MODE for this test!").isFalse()
        assertThat(squad.canMove()).isTrue()
    }

    static void assertSquadCouldNotMove(AbstractSquad squad) {
        assertThat(GdxgConstants.DEVELOPER_MODE).as("Turn off DEVELOPER_MODE for this test!").isFalse()
        assertThat(squad.canMove()).isFalse()
    }

    static void assertSquadSeesCells(AbstractSquad squad, Cell... cells) {
        LOG.info("assertSquadSeesCells\nSquad stateBodyText: {}", squad.getLastCell())
        Array<Cell> visible = squad.getVisibleCellsWithMyCell()
        assert visible.containsAll(cells)

        for (Cell cell : cells) {
            Array<AbstractSquad> visibleBySquads = cell.visibleBySquads
            assert (visibleBySquads).contains(squad): "visibleForObjects on: " + cell
        }
    }

    static void assertSquadDoesntSeeCells(AbstractSquad squad, Cell... cells) {
        LOG.info("assertSquadDoesntSeeCells\nSquad stateBodyText: {}", squad.getLastCell())
        Array<Cell> visible = squad.getVisibleCellsWithMyCell()
        assertThat(visible).doesNotContain(cells)

        for (Cell cell : cells) {
            Array<AbstractSquad> visibleBySquads = cell.visibleBySquads
            assertThat(visibleBySquads).as("visibleForObjects on: " + cell).doesNotContain(squad)
        }
    }

    static void assertSquadSeesAnother(AbstractSquad squad, AbstractSquad another) {
        assert (squad.visibleObjects).contains(another)
        assert (another.visibleForObjects).contains(squad)
    }

    static void assertSquadDoesntSeeAnother(AbstractSquad squad, AbstractSquad another) {
        assertThat(squad.visibleObjects).doesNotContain(another)
        assertThat(another.visibleForObjects).doesNotContain(squad)
    }

    static void assertSquadSeesAnotherOutOfStealth(AbstractSquad squad, AbstractSquad anotherNotInStealth) {
        assertSquadSeesCells(squad, anotherNotInStealth.getLastCell())
        assertSquadSeesAnother(squad, anotherNotInStealth)
        assertTeamSeesSquad(squad.getTeam(), anotherNotInStealth)
    }

    static void assertSquadDoesntSeeAnotherInStealth(AbstractSquad squad
                                                     , AbstractSquad anotherInStealth) {
        assertSquadSeesCells(squad, anotherInStealth.getLastCell())
        assertSquadDoesntSeeAnother(squad, anotherInStealth)
        assertTeamSeesSquad(squad.getTeam(), anotherInStealth)
    }

    static void assertTeamSeesSquad(Team team, AbstractSquad anotherNotInStealth) {
        assert (team.canSeeObject(anotherNotInStealth))
    }

    static void assertTeamDoesntSeeSquad(Team team, AbstractSquad anotherNotInStealth) {
        assertThat(team.canSeeObject(anotherNotInStealth)).isFalse()
    }

    static void assertSquadsSeesEachOtherOutOfStealth(AbstractSquad squad1, AbstractSquad squad2) {
        assertSquadSeesAnotherOutOfStealth(squad1, squad2)
        assertSquadSeesAnotherOutOfStealth(squad2, squad1)
    }
}
