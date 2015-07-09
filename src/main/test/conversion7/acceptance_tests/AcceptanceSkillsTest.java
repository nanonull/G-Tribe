package conversion7.acceptance_tests;

import com.badlogic.gdx.utils.Array;
import conversion7.AAATest;
import conversion7.AbstractTests;
import conversion7.game.classes.animals.AbstractAnimalUnit;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.items.ArrowItem;
import conversion7.game.stages.world.inventory.items.AtlatlItem;
import conversion7.game.stages.world.inventory.items.BowItem;
import conversion7.game.stages.world.inventory.items.HammerItem;
import conversion7.game.stages.world.inventory.items.JavelinItem;
import conversion7.game.stages.world.inventory.items.SkinItem;
import conversion7.game.stages.world.inventory.items.SkinRobeItem;
import conversion7.game.stages.world.inventory.items.SpearItem;
import conversion7.game.stages.world.inventory.items.StickItem;
import conversion7.game.stages.world.inventory.items.StoneItem;
import conversion7.game.stages.world.objects.AnimalHerd;
import conversion7.game.stages.world.objects.HumanSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.skills.FlayingSkill;
import conversion7.game.stages.world.team.skills.HandsAsAToolSkill;
import conversion7.game.stages.world.team.skills.HoldWeaponSkill;
import conversion7.game.stages.world.team.skills.HuntingWeaponsSkill;
import conversion7.game.stages.world.team.skills.LocomotionSkill;
import conversion7.game.stages.world.team.skills.PrimitiveClothingSkill;
import conversion7.game.stages.world.team.skills.PrimitiveWeaponsSkill;
import conversion7.game.stages.world.team.skills.WeaponMasterySkill;
import conversion7.game.stages.world.unit.Unit;
import conversion7.test_steps.BattleSteps;
import conversion7.test_steps.WorldSteps;
import conversion7.test_steps.asserts.WorldAsserts;
import org.testng.annotations.Test;

import static conversion7.game.services.WorldServices.createSomeAnimalUnit;
import static conversion7.game.services.WorldServices.createSomeHumanUnit;
import static conversion7.game.services.WorldServices.createSomeHumanUnits;
import static conversion7.test_steps.WorldSteps.addUnitToAreaObject;
import static conversion7.test_steps.WorldSteps.addUnitsToAreaObject;

public class AcceptanceSkillsTest extends AbstractTests {

    @Test
    public void test_FoodWithoutFoodSkills() {
        new AAATest() {

            @Override
            public void body() {
                Team humanTeam1 = World.createHumanTeam(false);
                Team team2 = World.ANIMAL_TEAM;
                HumanSquad army1 = humanTeam1.createHumanSquad(WorldSteps.getNextStandaloneCell());
                AnimalHerd army2 = team2.createAnimalHerd(WorldSteps.getNextNeighborCell());

                Array<Unit> units = createSomeHumanUnits(5);
                addUnitsToAreaObject(units, army1);
                Unit unit2 = createSomeAnimalUnit();
                addUnitToAreaObject(unit2, army2);

                BattleSteps.setAutoBattle(true);
                army1.attack(army2);

                LOG.info("Asserts");
                WorldAsserts.assertWorldIsActiveStage();
                WorldAsserts.assertAreaObjectFoodStorageIs(army1, AbstractAnimalUnit.FOOD_FROM_ONE_UNIT_TOTAL / 2);
            }

        }.run();
    }

    @Test
    public void test_FoodWithStoneWorkSkill() {
        new AAATest() {

            @Override
            public void body() {
                Team humanTeam1 = World.createHumanTeam(false);
                Team team2 = World.ANIMAL_TEAM;
                HumanSquad army1 = humanTeam1.createHumanSquad(WorldSteps.getNextStandaloneCell());
                AnimalHerd army2 = team2.createAnimalHerd(WorldSteps.getNextNeighborCell());

                Array<Unit> units = createSomeHumanUnits(5);
                addUnitsToAreaObject(units, army1);
                Unit unit2 = createSomeAnimalUnit();
                addUnitToAreaObject(unit2, army2);

                humanTeam1.getTeamSkillsManager().getStoneWorkSkill().learn();
                BattleSteps.setAutoBattle(true);
                army1.attack(army2);

                LOG.info("Asserts");
                WorldAsserts.assertWorldIsActiveStage();
                WorldAsserts.assertAreaObjectFoodStorageIs(army1, AbstractAnimalUnit.FOOD_FROM_ONE_UNIT_TOTAL);
            }

        }.run();
    }

    @Test
    public void test_LocomotionSkill_unitProducedParameters() {
        new AAATest() {

            @Override
            public void body() {
                Team team1 = World.createHumanTeam(false);
                HumanSquad army1 = team1.createHumanSquad(WorldSteps.getNextStandaloneCell());

                Unit unit = createSomeHumanUnit();
                addUnitToAreaObject(unit, army1);
                unit.getEquipment().equipMeleeWeaponItem(new HammerItem());
                unit.getEquipment().equipClothesItem(new SkinRobeItem());

                int damageBeforeSkill = unit.getMeleeDamage();
                int defenceBeforeSkill = unit.getDefence();
                LocomotionSkill locomotionSkill = team1.getTeamSkillsManager().getLocomotionSkill();
                locomotionSkill.learn();

                WorldAsserts.assertUnitHasDamageGreaterThan(unit, damageBeforeSkill);
                WorldAsserts.assertUnitHasDefenceGreaterThan(unit, defenceBeforeSkill);
            }

        }.run();
    }

    @Test
    public void test_HandsAsAToolSkill_unitProducedParameters() {
        new AAATest() {

            @Override
            public void body() {
                Team team1 = World.createHumanTeam(false);
                HumanSquad army1 = team1.createHumanSquad(WorldSteps.getNextStandaloneCell());

                Unit unit = createSomeHumanUnit();
                addUnitToAreaObject(unit, army1);
                // to make sure modificator percent will add > 1 damage
                unit.getEquipment().equipMeleeWeaponItem(new HammerItem());

                int damageBeforeSkill = unit.getMeleeDamage();
                HandsAsAToolSkill handsAsAToolSkill = team1.getTeamSkillsManager().getHandsAsAToolSkill();
                handsAsAToolSkill.learn();

                WorldAsserts.assertUnitHasDamageGreaterThan(unit, damageBeforeSkill);
            }

        }.run();
    }

    @Test
    public void test_WeaponMasterySkill_unitProducedParameters() {
        new AAATest() {

            @Override
            public void body() {
                Team team1 = World.createHumanTeam(false);
                HumanSquad army1 = team1.createHumanSquad(WorldSteps.getNextStandaloneCell());

                Unit unit = createSomeHumanUnit();
                addUnitToAreaObject(unit, army1);
                unit.getEquipment().equipMeleeWeaponItem(new HammerItem());

                int damageBeforeSkill = unit.getMeleeDamage();
                WeaponMasterySkill weaponMasterySkill = team1.getTeamSkillsManager().getWeaponMasterySkill();
                weaponMasterySkill.learn();

                WorldAsserts.assertUnitHasDamageGreaterThan(unit, damageBeforeSkill);
            }

        }.run();
    }

    @Test(invocationCount = 1)
    public void test_HoldWeaponSkill_equip() {
        new AAATest() {
            @Override
            public void body() {
                Team humanTeam = World.createHumanTeam(false);
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(humanTeam, WorldSteps.getNextStandaloneCell());

                Unit unit1 = humanSquad.getUnits().get(0);
                unit1.setSpecialization(Unit.UnitSpecialization.RANGE);

                WorldSteps.addItemToInventory(StickItem.class, 1, humanSquad.getMilitaryInventory());
                WorldSteps.addItemToInventory(StoneItem.class, 1, humanSquad.getMilitaryInventory());
                WorldAsserts.assertUnitHasNoEquippedMeleeWeapon(unit1);
                WorldAsserts.assertUnitHasNoEquippedRangeBullets(unit1);

                WorldSteps.teamLearnsSkill(humanTeam, HoldWeaponSkill.class);
                WorldAsserts.assertUnitHasEquipedMeleeWeapon(unit1, StickItem.class);
                WorldAsserts.assertUnitHasEquipedRangeBullets(unit1, StoneItem.class);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_FlayingSkill_in_battle() {
        new AAATest() {
            @Override
            public void body() {
                Team team1 = World.createHumanTeam(false);
                Team team2 = World.getAnimalTeam();
                HumanSquad army1 = team1.createHumanSquad(WorldSteps.getNextStandaloneCell());
                Unit unit1 = WorldServices.createSomeHumanUnit();
                WorldSteps.addUnitToAreaObject(unit1, army1);
                WorldSteps.makeUnitInvincible(unit1);

                // no skill
                AnimalHerd animalHerd = team2.createAnimalHerd(WorldSteps.getNextNeighborCell());
                Unit animalUnit = WorldServices.createSomeAnimalUnit();
                WorldSteps.addUnitToAreaObject(animalUnit, animalHerd);

                BattleSteps.setAutoBattle(true);
                army1.attack(animalHerd);

                WorldAsserts.assertWorldIsActiveStage();
                WorldAsserts.assertAreaObjectDefeated(animalHerd);
                WorldAsserts.assertInventoryDoesntContainItem(army1.getMainInventory(), SkinItem.class);

                // with skill
                WorldSteps.teamLearnsSkill(team1, FlayingSkill.class);

                AnimalHerd animalHerd2 = team2.createAnimalHerd(WorldSteps.getNextNeighborCell());
                Unit animalUnit2 = WorldServices.createSomeAnimalUnit();
                Unit animalUnit3 = WorldServices.createSomeAnimalUnit();
                WorldSteps.addUnitToAreaObject(animalUnit2, animalHerd2);
                WorldSteps.addUnitToAreaObject(animalUnit3, animalHerd2);

                army1.attack(animalHerd2);

                WorldAsserts.assertWorldIsActiveStage();
                WorldAsserts.assertAreaObjectDefeated(animalHerd2);
                WorldAsserts.assertInventoryContainsItem(army1.getMainInventory(),
                        SkinItem.class, AbstractAnimalUnit.SKIN_FROM_ONE_UNIT * 2);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_FlayingSkill_equipment() {
        new AAATest() {
            @Override
            public void body() {
                Team humanTeam = World.createHumanTeam(false);
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(humanTeam, WorldSteps.getNextStandaloneCell());

                Unit unit1 = humanSquad.getUnits().get(0);

                WorldSteps.addItemToInventory(SkinItem.class, 1, humanSquad.getMilitaryInventory());
                WorldAsserts.assertUnitHasNoEquippedClothes(unit1);

                WorldSteps.teamLearnsSkill(humanTeam, FlayingSkill.class);
                WorldAsserts.assertUnitHasEquipedClothes(unit1, SkinItem.class);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_PrimitiveWeaponSkill_equipment() {
        new AAATest() {
            @Override
            public void body() {
                Team humanTeam = World.createHumanTeam(false);
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(humanTeam, WorldSteps.getNextStandaloneCell());

                Array<Unit> units = WorldServices.createSomeHumanUnits(3);
                WorldSteps.addUnitsToAreaObject(units, humanSquad);
                units.get(0).setSpecialization(Unit.UnitSpecialization.RANGE);

                WorldSteps.addItemToInventory(HammerItem.class, 1, humanSquad.getMilitaryInventory());
                WorldSteps.addItemToInventory(SpearItem.class, 1, humanSquad.getMilitaryInventory());
                WorldSteps.addItemToInventory(JavelinItem.class, 1, humanSquad.getMilitaryInventory());
                WorldAsserts.assertInventoryContainsItem(humanSquad.getMilitaryInventory(), HammerItem.class, 1);
                WorldAsserts.assertInventoryContainsItem(humanSquad.getMilitaryInventory(), SpearItem.class, 1);
                WorldAsserts.assertInventoryContainsItem(humanSquad.getMilitaryInventory(), JavelinItem.class, 1);

                WorldSteps.teamLearnsSkill(humanTeam, PrimitiveWeaponsSkill.class);
                WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), HammerItem.class);
                WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), SpearItem.class);
                WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), JavelinItem.class);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_PrimitiveClothesSkill_equipment() {
        new AAATest() {
            @Override
            public void body() {
                Team humanTeam = World.createHumanTeam(false);
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(humanTeam, WorldSteps.getNextStandaloneCell());

                Array<Unit> units = WorldServices.createSomeHumanUnits(1);
                WorldSteps.addUnitsToAreaObject(units, humanSquad);

                WorldSteps.addItemToInventory(SkinRobeItem.class, 1, humanSquad.getMilitaryInventory());
                WorldAsserts.assertInventoryContainsItem(humanSquad.getMilitaryInventory(), SkinRobeItem.class, 1);

                WorldSteps.teamLearnsSkill(humanTeam, PrimitiveClothingSkill.class);
                WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), SkinRobeItem.class);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_HuntingWeaponsSkill_equipment() {
        new AAATest() {
            @Override
            public void body() {
                Team humanTeam = World.createHumanTeam(false);
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(humanTeam, WorldSteps.getNextStandaloneCell());

                Array<Unit> units = WorldServices.createSomeHumanUnits(3);
                WorldSteps.addUnitsToAreaObject(units, humanSquad);
                units.get(0).setSpecialization(Unit.UnitSpecialization.RANGE);
                units.get(1).setSpecialization(Unit.UnitSpecialization.RANGE);

                WorldSteps.addItemToInventory(BowItem.class, 1, humanSquad.getMilitaryInventory());
                WorldSteps.addItemToInventory(ArrowItem.class, 1, humanSquad.getMilitaryInventory());
                WorldSteps.addItemToInventory(AtlatlItem.class, 1, humanSquad.getMilitaryInventory());
                WorldAsserts.assertInventoryContainsItem(humanSquad.getMilitaryInventory(), BowItem.class, 1);
                WorldAsserts.assertInventoryContainsItem(humanSquad.getMilitaryInventory(), ArrowItem.class, 1);
                WorldAsserts.assertInventoryContainsItem(humanSquad.getMilitaryInventory(), AtlatlItem.class, 1);

                WorldSteps.teamLearnsSkill(humanTeam, HuntingWeaponsSkill.class);
                WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), BowItem.class);
                WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), ArrowItem.class);
                WorldAsserts.assertInventoryDoesntContainItem(humanSquad.getMilitaryInventory(), AtlatlItem.class);
            }
        }.run();
    }
}
