package tests.acceptance.inventory

import conversion7.game.services.WorldServices
import conversion7.game.stages.world.inventory.InventoryItemStaticParams
import conversion7.game.stages.world.inventory.items.ArrowItem
import conversion7.game.stages.world.inventory.items.types.ClothesItem
import conversion7.game.stages.world.inventory.items.types.MeleeWeaponItem
import conversion7.game.stages.world.inventory.items.types.RangeWeaponItem
import conversion7.game.stages.world.inventory.items.weapons.AtlatlItem
import conversion7.game.stages.world.inventory.items.weapons.BowItem
import conversion7.game.stages.world.inventory.items.weapons.JavelinItem
import conversion7.game.stages.world.objects.AreaObject
import conversion7.game.stages.world.objects.actions.items.RangeAttackAction
import conversion7.game.stages.world.unit.Unit
import org.testng.Assert
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts
import shared.steps.WorldSteps
import spock.lang.Ignore

// TODO review unit Equipment
@Ignore
@Deprecated
class EquipmentAutoTest extends BaseGdxgSpec {

    public void 'test UnitFullEquipped'() {
        when:
        lockCore()
        def humanSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        Unit unit1 = humanSquad.unit;
        unit1.setSpecialization(AreaObject.UnitSpecialization.RANGE);
        worldSteps.makeUnitCouldEquipItemsFromDefaultEquipmentPackage(unit1);
        worldSteps.addDefaultEquipmentPackageToInventory(humanSquad.getMilitaryInventory());
        releaseCore()
        waitForNextCoreStep()

        then:
        WorldAsserts.assertInventoryDoesntContainDefaultEquipmentPackage(humanSquad.getMilitaryInventory());
        WorldAsserts.assertUnitHasEquippedDefaultEquipmentPackage(unit1);
    }

    public void 'test NewSwitchedRangerWillTakeRangeWeapon'() {
        given:
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        Unit unit = humanSquad1.unit;
        worldSteps.makeUnitCouldEquipItem(unit, BowItem.class);
        worldSteps.makeUnitCouldEquipItem(unit, ArrowItem.class);
        unit.setSpecialization(AreaObject.UnitSpecialization.MELEE);

        // act 1
        humanSquad1.getMilitaryInventory().addItem(BowItem.class, 1);
        humanSquad1.getMilitaryInventory().addItem(ArrowItem.class, 1);

        // assert 1
        WorldAsserts.assertUnitHasNoEquippedRangeWeapon(unit);
        WorldAsserts.assertUnitHasNoEquippedRangeBullets(unit);

        // act 2
        unit.setSpecialization(AreaObject.UnitSpecialization.RANGE);

        // assert 2
        WorldAsserts.assertUnitHasEquipedRangeWeapon(unit, BowItem.class);
        WorldAsserts.assertUnitHasEquipedRangeBullets(unit, ArrowItem.class);
    }

    public void 'test OldSwitchedRanger Should DropRangeWeapon'() {
        given:
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        Unit unit = humanSquad1.unit;
        worldSteps.makeUnitCouldEquipItem(unit, BowItem.class);
        worldSteps.makeUnitCouldEquipItem(unit, ArrowItem.class);
        unit.setSpecialization(AreaObject.UnitSpecialization.RANGE);

        // act 1
        humanSquad1.getMilitaryInventory().addItem(BowItem.class, 1);
        humanSquad1.getMilitaryInventory().addItem(ArrowItem.class, 1);

        // assert 1
        WorldAsserts.assertUnitHasEquipedRangeWeapon(unit, BowItem.class);
        WorldAsserts.assertUnitHasEquipedRangeBullets(unit, ArrowItem.class);

        // act 2
        unit.setSpecialization(AreaObject.UnitSpecialization.MELEE);

        // assert 2
        WorldAsserts.assertUnitHasNoEquippedRangeWeapon(unit);
        WorldAsserts.assertUnitHasNoEquippedRangeBullets(unit);
    }

    public void 'test RangeUnitEquippingPriority'() {
        given:
        def humanSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        // 1st unit by power
        Unit unit1 = humanSquad.unit
        unit1.setSpecialization(AreaObject.UnitSpecialization.RANGE);


        worldSteps.makeUnitCouldEquipItem(unit1, BowItem.class);
        worldSteps.makeUnitCouldEquipItem(unit1, ArrowItem.class);

        worldSteps.addItemToInventory(BowItem.class, 2, humanSquad.getMilitaryInventory());
        worldSteps.addItemToInventory(ArrowItem.class, 1,
                humanSquad.getMilitaryInventory());

        WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), BowItem.class);
        WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), ArrowItem.class);
        WorldAsserts.assertUnitHasEquipedRangeWeapon(unit1, BowItem.class);
        WorldAsserts.assertUnitHasEquipedRangeBullets(unit1, ArrowItem.class, 1);

    }

    public void test_BulletEquipmentLimit() {
        given:
        final int MAX_QTY_EQUIP = InventoryItemStaticParams.ARROW.getEquipQuantityLimit();
        final int EXTRA_ITEMS = 1;

        def humanSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        Unit unit1 = humanSquad.unit
        unit1.setSpecialization(AreaObject.UnitSpecialization.RANGE);
        worldSteps.makeUnitCouldEquipItem(unit1, BowItem.class);
        worldSteps.makeUnitCouldEquipItem(unit1, ArrowItem.class);

        worldSteps.addItemToInventory(BowItem.class, 1, humanSquad.getMilitaryInventory());
        worldSteps.addItemToInventory(ArrowItem.class, MAX_QTY_EQUIP + EXTRA_ITEMS,
                humanSquad.getMilitaryInventory());

        WorldAsserts.assertInventoryContainsItem(humanSquad.getMilitaryInventory(), ArrowItem.class, EXTRA_ITEMS);
        WorldAsserts.assertUnitHasEquipedRangeBullets(unit1, ArrowItem.class, MAX_QTY_EQUIP);
    }

    public void test_DropEquippedWeaponAndBulletDueToNewWeaponType() {
        given:
        Class<? extends RangeWeaponItem> NEW_BETTER_RANGE_WEAPON = BowItem.class;

        def humanSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        Unit unit1 = humanSquad.unit
        unit1.setSpecialization(AreaObject.UnitSpecialization.RANGE);
        worldSteps.makeUnitCouldEquipItem(unit1, AtlatlItem.class);
        worldSteps.makeUnitCouldEquipItem(unit1, ArrowItem.class);
        worldSteps.makeUnitCouldEquipItem(unit1, JavelinItem.class);
        worldSteps.makeUnitCouldEquipItem(unit1, NEW_BETTER_RANGE_WEAPON);

        worldSteps.addItemToInventory(AtlatlItem.class, 1, humanSquad.getMilitaryInventory());
        worldSteps.addItemToInventory(ArrowItem.class, 1, humanSquad.getMilitaryInventory());
        worldSteps.addItemToInventory(JavelinItem.class, 1, humanSquad.getMilitaryInventory());

        // pre-asserts
        WorldAsserts.assertUnitHasEquipedRangeWeapon(unit1, AtlatlItem.class);
        WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), JavelinItem.class);
        WorldAsserts.assertInventoryContainsItem(humanSquad.getMilitaryInventory(), ArrowItem.class, 1);

        // new better weapon
        worldSteps.addItemToInventory(NEW_BETTER_RANGE_WEAPON, 1, humanSquad.getMilitaryInventory());

        // ASSERTS
        WorldAsserts.assertUnitHasEquipedRangeWeapon(unit1, NEW_BETTER_RANGE_WEAPON);
        WorldAsserts.assertUnitHasEquipedRangeBullets(unit1, ArrowItem.class, 1);

        WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), NEW_BETTER_RANGE_WEAPON);
        WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), ArrowItem.class);
        WorldAsserts.assertInventoryContainsItem(humanSquad.getMilitaryInventory(), AtlatlItem.class, 1);
        WorldAsserts.assertInventoryContainsItem(humanSquad.getMilitaryInventory(), JavelinItem.class, 1);
    }

    public void test_RangeUnitCouldEquipBulletsOnly() {
        given:
        def humanSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        Unit unit1 = humanSquad.unit

        unit1.setSpecialization(AreaObject.UnitSpecialization.RANGE);
        worldSteps.makeUnitCouldEquipItem(unit1, BowItem.class);
        worldSteps.makeUnitCouldEquipItem(unit1, ArrowItem.class);
        worldSteps.makeUnitCouldEquipItem(unit1, JavelinItem.class);

        // weapon
        worldSteps.addItemToInventory(BowItem.class, 1, humanSquad.getMilitaryInventory());
        WorldAsserts.assertUnitHasEquipedRangeWeapon(unit1, BowItem.class);

        // bullets
        worldSteps.addItemToInventory(ArrowItem.class, 2, humanSquad.getMilitaryInventory());
        WorldAsserts.assertUnitHasEquipedRangeBullets(unit1, ArrowItem.class, 1);

        // equip better bullet, drop worse
        worldSteps.addItemToInventory(JavelinItem.class, 1, humanSquad.getMilitaryInventory());
        WorldAsserts.assertInventoryIsEmpty(humanSquad.getMilitaryInventory());
        WorldAsserts.assertUnitHasEquipedRangeBullets(unit1, ArrowItem.class, 2);
    }

    public void test_NoItemsDuplicationOnEquip() {
        given:
        def humanSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        Unit unit1 = humanSquad.unit

        unit1.setSpecialization(AreaObject.UnitSpecialization.RANGE);
        worldSteps.makeUnitCouldEquipItem(unit1, BowItem.class);

        worldSteps.addItemToInventory(BowItem.class, 1, humanSquad.getMilitaryInventory());

        WorldAsserts.assertUnitHasEquipedRangeWeapon(unit1, BowItem.class);
    }

    public void test_MeleeVsRangeUnitEquippingPriority_minimumRangeEquip() {
        given:
        def humanSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        Unit unit1 = humanSquad.unit

        unit1.setSpecialization(AreaObject.UnitSpecialization.MELEE);
        worldSteps.makeUnitCouldEquipItemsFromDefaultEquipmentPackage(unit1);

        worldSteps.addDefaultEquipmentPackageToInventory(humanSquad.getMilitaryInventory());

        // melee
        WorldAsserts.assertUnitHasEquipedMeleeWeapon(unit1,
                (Class<? extends MeleeWeaponItem>) WorldSteps.DefaultPackageItem.MELEE.getInventoryItemClass());
        WorldAsserts.assertUnitHasEquipedClothes(unit1,
                (Class<? extends ClothesItem>) WorldSteps.DefaultPackageItem.CLOTHES.getInventoryItemClass());

    }

    public void test_MeleeVsRangeUnitEquippingPriority_fullRangeEquip() {
        given:
        def humanSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        Unit unit1 = humanSquad.unit

        unit1.setSpecialization(AreaObject.UnitSpecialization.MELEE);
        worldSteps.makeUnitCouldEquipItemsFromDefaultEquipmentPackage(unit1);

        worldSteps.addDefaultEquipmentPackageToInventory(humanSquad.getMilitaryInventory());
        worldSteps.addDefaultEquipmentPackageToInventory(humanSquad.getMilitaryInventory());

        // melee
        WorldAsserts.assertUnitHasEquipedMeleeWeapon(unit1,
                (Class<? extends MeleeWeaponItem>) WorldSteps.DefaultPackageItem.MELEE.getInventoryItemClass());
        WorldAsserts.assertUnitHasEquipedClothes(unit1,
                (Class<? extends ClothesItem>) WorldSteps.DefaultPackageItem.CLOTHES.getInventoryItemClass());

    }

    public void test_RangeAttackAction_addedWhenUnitChangedSpecializationAndEquipWeapon() {
        given:
        Assert.assertTrue(false, "review test goal");

        WorldServices.nextUnitSpecialization = AreaObject.UnitSpecialization.MELEE;
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam()
                , worldSteps.getNextStandaloneCell());
        Unit futureRangeUnit = humanSquad1.unit;
        worldSteps.makeUnitCouldEquipItem(futureRangeUnit, ArrowItem.class);
        worldSteps.addItemToInventory(ArrowItem.class, 1, humanSquad1.getMilitaryInventory());
        WorldAsserts.assertAreaObjectHasNoAction(humanSquad1, RangeAttackAction.class);

        when:
        futureRangeUnit.setSpecialization(AreaObject.UnitSpecialization.RANGE);

        then:
        WorldAsserts.assertAreaObjectHasAction(humanSquad1, RangeAttackAction.class);
    }

    // player steps: learn skill > equip bullet: see action
    public void test_RangeAttackAction_addedAfterRequiredSkillLearned_andUnitEquipsWeapon() {
        given:
        WorldServices.nextUnitSpecialization = AreaObject.UnitSpecialization.RANGE;
        def humanSquad1 = worldSteps.createUnit(worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());
        Unit rangeUnit = humanSquad1.unit;
        worldSteps.addItemToInventory(ArrowItem.class, 1, humanSquad1.getMilitaryInventory());
        WorldAsserts.assertAreaObjectHasNoAction(humanSquad1, RangeAttackAction.class);

        when:
        worldSteps.makeUnitCouldEquipItem(rangeUnit, ArrowItem.class);

        then:
        WorldAsserts.assertAreaObjectHasAction(humanSquad1, RangeAttackAction.class);

    }
}
