package conversion7.acceptance_tests;

import conversion7.AAATest;
import conversion7.AbstractTests;
import conversion7.engine.utils.Utils;
import conversion7.game.classes.theOldest.ArdipithecusKadabba;
import conversion7.game.classes.theOldest.Propliopithecus;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.inventory.items.ArrowItem;
import conversion7.game.stages.world.inventory.items.AtlatlItem;
import conversion7.game.stages.world.inventory.items.BowItem;
import conversion7.game.stages.world.inventory.items.JavelinItem;
import conversion7.game.stages.world.inventory.items.SkinItem;
import conversion7.game.stages.world.inventory.items.StickItem;
import conversion7.game.stages.world.inventory.items.types.ClothesItem;
import conversion7.game.stages.world.inventory.items.types.MeleeWeaponItem;
import conversion7.game.stages.world.inventory.items.types.RangeBulletItem;
import conversion7.game.stages.world.inventory.items.types.RangeWeaponItem;
import conversion7.game.stages.world.objects.HumanSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.test_steps.WorldSteps;
import conversion7.test_steps.asserts.WorldAsserts;
import org.slf4j.Logger;
import org.fest.assertions.api.Assertions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AcceptanceEquipmentTest extends AbstractTests {

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


    @Test(invocationCount = 1)
    public void test_MeleeWeaponDamage() {
        new AAATest() {

            @Override
            public void body() {
                Unit unit1 = WorldServices.createSomeHumanUnit();
                WorldSteps.addUnitToAreaObject(unit1, defaultHumanSquad);

                int damageNoWeapon = unit1.getMeleeDamage();
                Assertions.assertThat(damageNoWeapon).isGreaterThan(0);

                unit1.getEquipment().equipMeleeWeaponItem(new StickItem());
                int damageWithWeapon = unit1.getMeleeDamage();

                Assertions.assertThat(damageWithWeapon).isGreaterThan(damageNoWeapon);
            }

        }.run();
    }

    @Test(invocationCount = 1)
    public void test_ClothesArmor() {
        new AAATest() {

            @Override
            public void body() {
                Unit unit1 = WorldServices.createSomeHumanUnit();
                WorldSteps.addUnitToAreaObject(unit1, defaultHumanSquad);

                int armorNoClothes = unit1.getArmor();
                Assertions.assertThat(armorNoClothes).isGreaterThanOrEqualTo(0);

                unit1.getEquipment().equipClothesItem(new SkinItem());
                int armorWithClothes = unit1.getArmor();

                Assertions.assertThat(armorWithClothes).isGreaterThan(armorNoClothes);
            }

        }.run();
    }

    @Test(invocationCount = 1)
    public void test_UnitFullEquipped() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.getPlayerTeam(), WorldSteps.getNextStandaloneCell());

                Unit unit1 = humanSquad.getUnits().get(0);
                unit1.setSpecialization(Unit.UnitSpecialization.RANGE);
                WorldSteps.addUnitToAreaObject(unit1, humanSquad);
                WorldSteps.makeUnitCouldEquipItemsFromDefaultEquipmentPackage(unit1);
                WorldSteps.addDefaultEquipmentPackageToInventory(humanSquad.getMilitaryInventory());

                WorldAsserts.assertInventoryDoesntContainDefaultEquipmentPackage(humanSquad.getMilitaryInventory());
                WorldAsserts.assertUnitHasEquippedDefaultEquipmentPackage(unit1);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_NewSwitchedRangerWillTakeRangeWeapon() {
        new AAATest() {

            @Override
            public void body() {
                HumanSquad humanSquad1 = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());
                Assert.assertEquals(humanSquad1.getUnits().size, 1);
                Unit unit = humanSquad1.getUnits().get(0);
                WorldSteps.makeUnitCouldEquipItem(unit, BowItem.class);
                WorldSteps.makeUnitCouldEquipItem(unit, ArrowItem.class);
                unit.setSpecialization(Unit.UnitSpecialization.MELEE);

                // act 1
                humanSquad1.getMilitaryInventory().addItem(BowItem.class, 1);
                humanSquad1.getMilitaryInventory().addItem(ArrowItem.class, 1);

                // assert 1
                WorldAsserts.assertUnitHasNoEquippedRangeWeapon(unit);
                WorldAsserts.assertUnitHasNoEquippedRangeBullets(unit);

                // act 2
                unit.setSpecialization(Unit.UnitSpecialization.RANGE);

                // assert 2
                WorldAsserts.assertUnitHasEquipedRangeWeapon(unit, BowItem.class);
                WorldAsserts.assertUnitHasEquipedRangeBullets(unit, ArrowItem.class);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_OldSwitchedRangerShouldDropRangeWeapon() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad1 = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());
                Assert.assertEquals(humanSquad1.getUnits().size, 1);
                Unit unit = humanSquad1.getUnits().get(0);
                WorldSteps.makeUnitCouldEquipItem(unit, BowItem.class);
                WorldSteps.makeUnitCouldEquipItem(unit, ArrowItem.class);
                unit.setSpecialization(Unit.UnitSpecialization.RANGE);

                // act 1
                humanSquad1.getMilitaryInventory().addItem(BowItem.class, 1);
                humanSquad1.getMilitaryInventory().addItem(ArrowItem.class, 1);

                // assert 1
                WorldAsserts.assertUnitHasEquipedRangeWeapon(unit, BowItem.class);
                WorldAsserts.assertUnitHasEquipedRangeBullets(unit, ArrowItem.class);

                // act 2
                unit.setSpecialization(Unit.UnitSpecialization.MELEE);

                // assert 2
                WorldAsserts.assertUnitHasNoEquippedRangeWeapon(unit);
                WorldAsserts.assertUnitHasNoEquippedRangeBullets(unit);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_NewAddedRangerUnitWillTakeRangeWeapon() {
        new AAATest() {
            @Override
            public void body() {
                Team humanTeam = World.createHumanTeam(false);
                HumanSquad humanSquad1 = WorldServices.createHumanSquadWithSomeUnit(humanTeam, WorldSteps.getNextStandaloneCell());
                Unit unit1 = humanSquad1.getUnits().get(0);
                unit1.setSpecialization(Unit.UnitSpecialization.MELEE);

                actSection();
                humanSquad1.getMilitaryInventory().addItem(BowItem.class, 1);
                humanSquad1.getMilitaryInventory().addItem(ArrowItem.class, 1);

                assertSection();
                WorldAsserts.assertInventoryContainsItem(humanSquad1.getMilitaryInventory(), BowItem.class, 1);
                WorldAsserts.assertInventoryContainsItem(humanSquad1.getMilitaryInventory(), ArrowItem.class, 1);

                actSection();
                HumanSquad humanSquad2 = WorldServices.createHumanSquadWithSomeUnit(humanTeam, WorldSteps.getNextStandaloneCell());
                Unit unit2 = humanSquad2.getUnits().get(0);
                WorldSteps.makeUnitCouldEquipItem(unit2, BowItem.class);
                WorldSteps.makeUnitCouldEquipItem(unit2, ArrowItem.class);
                unit2.setSpecialization(Unit.UnitSpecialization.RANGE);
                WorldAsserts.assertUnitHasNoEquippedRangeWeapon(unit2);
                WorldAsserts.assertUnitHasNoEquippedRangeBullets(unit2);
                unit2.moveInto(humanSquad1);
                humanSquad1.validate();

                assertSection();
                Assert.assertTrue(humanSquad2.couldBeDefeated());
                WorldAsserts.assertUnitHasEquipedRangeWeapon(unit2, BowItem.class);
                WorldAsserts.assertUnitHasEquipedRangeBullets(unit2, ArrowItem.class);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_RangeUnitEquippingPriority() {
        new AAATest() {

            static final int FIRST_UNIT_BULLETS = 2;
            static final int SECOND_UNIT_BULLETS = 1;

            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.getPlayerTeam(), WorldSteps.getNextStandaloneCell());

                // 1st unit by power
                Unit unit1 = humanSquad.getUnits().get(0);
                unit1.setSpecialization(Unit.UnitSpecialization.RANGE);

                // 2nd unit by power
                Unit unit2 = WorldServices.createHumanUnit(Propliopithecus.class);
                WorldSteps.addUnitToAreaObject(unit2, humanSquad);
                unit2.setSpecialization(Unit.UnitSpecialization.RANGE);

                WorldSteps.makeUnitCouldEquipItem(unit1, BowItem.class);
                WorldSteps.makeUnitCouldEquipItem(unit1, ArrowItem.class);
                WorldSteps.makeUnitCouldEquipItem(unit2, BowItem.class);
                WorldSteps.makeUnitCouldEquipItem(unit2, ArrowItem.class);

                WorldSteps.addItemToInventory(BowItem.class, 2, humanSquad.getMilitaryInventory());
                WorldSteps.addItemToInventory(ArrowItem.class, FIRST_UNIT_BULLETS + SECOND_UNIT_BULLETS,
                        humanSquad.getMilitaryInventory());

                WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), BowItem.class);
                WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), ArrowItem.class);
                WorldAsserts.assertUnitHasEquipedRangeWeapon(unit1, BowItem.class);
                WorldAsserts.assertUnitHasEquipedRangeBullets(unit1, ArrowItem.class, FIRST_UNIT_BULLETS);
                WorldAsserts.assertUnitHasEquipedRangeWeapon(unit2, BowItem.class);
                WorldAsserts.assertUnitHasEquipedRangeBullets(unit2, ArrowItem.class, SECOND_UNIT_BULLETS);

            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_BulletEquipmentLimit() {
        new AAATest() {

            final int MAX_QTY_EQUIP = InventoryItemStaticParams.ARROW.getEquipQuantityLimit();
            final int EXTRA_ITEMS = 1;

            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.getPlayerTeam(), WorldSteps.getNextStandaloneCell());

                Unit unit1 = humanSquad.getUnits().get(0);
                unit1.setSpecialization(Unit.UnitSpecialization.RANGE);
                WorldSteps.makeUnitCouldEquipItem(unit1, BowItem.class);
                WorldSteps.makeUnitCouldEquipItem(unit1, ArrowItem.class);

                WorldSteps.addItemToInventory(BowItem.class, 1, humanSquad.getMilitaryInventory());
                WorldSteps.addItemToInventory(ArrowItem.class, MAX_QTY_EQUIP + EXTRA_ITEMS,
                        humanSquad.getMilitaryInventory());

                WorldAsserts.assertInventoryContainsItem(humanSquad.getMilitaryInventory(), ArrowItem.class, EXTRA_ITEMS);
                WorldAsserts.assertUnitHasEquipedRangeBullets(unit1, ArrowItem.class, MAX_QTY_EQUIP);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_DropEquippedWeaponAndBulletDueToNewWeaponType() {
        new AAATest() {

            Class<? extends RangeWeaponItem> NEW_BETTER_RANGE_WEAPON = BowItem.class;

            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.getPlayerTeam(), WorldSteps.getNextStandaloneCell());

                Unit unit1 = humanSquad.getUnits().get(0);
                unit1.setSpecialization(Unit.UnitSpecialization.RANGE);
                WorldSteps.makeUnitCouldEquipItem(unit1, AtlatlItem.class);
                WorldSteps.makeUnitCouldEquipItem(unit1, ArrowItem.class);
                WorldSteps.makeUnitCouldEquipItem(unit1, JavelinItem.class);
                WorldSteps.makeUnitCouldEquipItem(unit1, NEW_BETTER_RANGE_WEAPON);

                WorldSteps.addItemToInventory(AtlatlItem.class, 1, humanSquad.getMilitaryInventory());
                WorldSteps.addItemToInventory(ArrowItem.class, 1, humanSquad.getMilitaryInventory());
                WorldSteps.addItemToInventory(JavelinItem.class, 1, humanSquad.getMilitaryInventory());

                // pre-asserts
                WorldAsserts.assertUnitHasEquipedRangeWeapon(unit1, AtlatlItem.class);
                WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), JavelinItem.class);
                WorldAsserts.assertInventoryContainsItem(humanSquad.getMilitaryInventory(), ArrowItem.class, 1);

                // new better weapon
                WorldSteps.addItemToInventory(NEW_BETTER_RANGE_WEAPON, 1, humanSquad.getMilitaryInventory());

                // ASSERTS
                WorldAsserts.assertUnitHasEquipedRangeWeapon(unit1, NEW_BETTER_RANGE_WEAPON);
                WorldAsserts.assertUnitHasEquipedRangeBullets(unit1, ArrowItem.class, 1);

                WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), NEW_BETTER_RANGE_WEAPON);
                WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), ArrowItem.class);
                WorldAsserts.assertInventoryContainsItem(humanSquad.getMilitaryInventory(), AtlatlItem.class, 1);
                WorldAsserts.assertInventoryContainsItem(humanSquad.getMilitaryInventory(), JavelinItem.class, 1);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_RangeUnitCouldEquipBulletsOnly() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.getPlayerTeam(), WorldSteps.getNextStandaloneCell());

                Unit unit1 = humanSquad.getUnits().get(0);
                Unit unit2 = WorldServices.createHumanUnit(Propliopithecus.class);
                WorldSteps.addUnitToAreaObject(unit2, humanSquad);

                unit1.setSpecialization(Unit.UnitSpecialization.RANGE);
                unit2.setSpecialization(Unit.UnitSpecialization.RANGE);
                WorldSteps.makeUnitCouldEquipItem(unit1, BowItem.class);
                WorldSteps.makeUnitCouldEquipItem(unit1, ArrowItem.class);
                WorldSteps.makeUnitCouldEquipItem(unit1, JavelinItem.class);
                WorldSteps.makeUnitCouldEquipItem(unit2, BowItem.class);
                WorldSteps.makeUnitCouldEquipItem(unit2, ArrowItem.class);
                WorldSteps.makeUnitCouldEquipItem(unit2, JavelinItem.class);

                // weapon
                WorldSteps.addItemToInventory(BowItem.class, 1, humanSquad.getMilitaryInventory());
                WorldAsserts.assertUnitHasEquipedRangeWeapon(unit1, BowItem.class);

                // bullets
                WorldSteps.addItemToInventory(ArrowItem.class, 2, humanSquad.getMilitaryInventory());
                WorldAsserts.assertUnitHasEquipedRangeBullets(unit1, ArrowItem.class, 1);
                WorldAsserts.assertUnitHasEquipedRangeBullets(unit2, ArrowItem.class, 1);

                // equip better bullet, drop worse
                WorldSteps.addItemToInventory(JavelinItem.class, 1, humanSquad.getMilitaryInventory());
                WorldAsserts.assertUnitHasEquipedRangeBullets(unit2, JavelinItem.class, 1);
                WorldAsserts.assertUnitHasEquipedRangeBullets(unit1, ArrowItem.class, 2);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_NoItemsDuplicationOnEquip() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());

                Unit unit1 = WorldServices.createHumanUnit(ArdipithecusKadabba.class);
                WorldSteps.addUnitToAreaObject(unit1, humanSquad);
                Unit unit2 = WorldServices.createHumanUnit(Propliopithecus.class);
                WorldSteps.addUnitToAreaObject(unit2, humanSquad);

                unit1.setSpecialization(Unit.UnitSpecialization.RANGE);
                unit2.setSpecialization(Unit.UnitSpecialization.RANGE);
                WorldSteps.makeUnitCouldEquipItem(unit1, BowItem.class);
                WorldSteps.makeUnitCouldEquipItem(unit2, BowItem.class);

                WorldSteps.addItemToInventory(BowItem.class, 1, humanSquad.getMilitaryInventory());

                WorldAsserts.assertUnitHasEquipedRangeWeapon(unit1, BowItem.class);
                WorldAsserts.assertUnitHasNoEquippedRangeWeapon(unit2);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_MeleeVsRangeUnitEquippingPriority_minimumRangeEquip() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.getPlayerTeam(), WorldSteps.getNextStandaloneCell());

                Unit unit1 = humanSquad.getUnits().get(0);
                Unit unit2 = WorldServices.createHumanUnit(Propliopithecus.class);
                WorldSteps.addUnitToAreaObject(unit2, humanSquad);

                unit1.setSpecialization(Unit.UnitSpecialization.MELEE);
                unit2.setSpecialization(Unit.UnitSpecialization.RANGE);
                WorldSteps.makeUnitCouldEquipItemsFromDefaultEquipmentPackage(unit1);
                WorldSteps.makeUnitCouldEquipItemsFromDefaultEquipmentPackage(unit2);

                WorldSteps.addDefaultEquipmentPackageToInventory(humanSquad.getMilitaryInventory());

                // melee
                WorldAsserts.assertUnitHasEquipedMeleeWeapon(unit1,
                        (Class<? extends MeleeWeaponItem>) WorldSteps.DefaultPackageItem.MELEE.getInventoryItemClass());
                WorldAsserts.assertUnitHasEquipedClothes(unit1,
                        (Class<? extends ClothesItem>) WorldSteps.DefaultPackageItem.CLOTHES.getInventoryItemClass());

                // range
                WorldAsserts.assertUnitHasEquipedRangeWeapon(unit2,
                        (Class<? extends RangeWeaponItem>) WorldSteps.DefaultPackageItem.RANGE.getInventoryItemClass());
                WorldAsserts.assertUnitHasEquipedRangeBullets(unit2,
                        (Class<? extends RangeBulletItem>) WorldSteps.DefaultPackageItem.BULLETS.getInventoryItemClass(), 1);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_MeleeVsRangeUnitEquippingPriority_fullRangeEquip() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.getPlayerTeam(), WorldSteps.getNextStandaloneCell());

                Unit unit1 = humanSquad.getUnits().get(0);
                Unit unit2 = WorldServices.createHumanUnit(Propliopithecus.class);
                WorldSteps.addUnitToAreaObject(unit2, humanSquad);

                unit1.setSpecialization(Unit.UnitSpecialization.MELEE);
                unit2.setSpecialization(Unit.UnitSpecialization.RANGE);
                WorldSteps.makeUnitCouldEquipItemsFromDefaultEquipmentPackage(unit1);
                WorldSteps.makeUnitCouldEquipItemsFromDefaultEquipmentPackage(unit2);

                WorldSteps.addDefaultEquipmentPackageToInventory(humanSquad.getMilitaryInventory());
                WorldSteps.addDefaultEquipmentPackageToInventory(humanSquad.getMilitaryInventory());

                // melee
                WorldAsserts.assertUnitHasEquipedMeleeWeapon(unit1,
                        (Class<? extends MeleeWeaponItem>) WorldSteps.DefaultPackageItem.MELEE.getInventoryItemClass());
                WorldAsserts.assertUnitHasEquipedClothes(unit1,
                        (Class<? extends ClothesItem>) WorldSteps.DefaultPackageItem.CLOTHES.getInventoryItemClass());

                // range
                WorldAsserts.assertUnitHasEquipedMeleeWeapon(unit2,
                        (Class<? extends MeleeWeaponItem>) WorldSteps.DefaultPackageItem.MELEE.getInventoryItemClass());
                WorldAsserts.assertUnitHasEquipedClothes(unit2,
                        (Class<? extends ClothesItem>) WorldSteps.DefaultPackageItem.CLOTHES.getInventoryItemClass());
                WorldAsserts.assertUnitHasEquipedRangeWeapon(unit2,
                        (Class<? extends RangeWeaponItem>) WorldSteps.DefaultPackageItem.RANGE.getInventoryItemClass());
                WorldAsserts.assertUnitHasEquipedRangeBullets(unit2,
                        (Class<? extends RangeBulletItem>) WorldSteps.DefaultPackageItem.BULLETS.getInventoryItemClass(), 2);
            }
        }.run();
    }

}
