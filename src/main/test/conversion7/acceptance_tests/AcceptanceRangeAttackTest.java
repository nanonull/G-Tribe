package conversion7.acceptance_tests;

import conversion7.AAATest;
import conversion7.AbstractTests;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.items.ArrowItem;
import conversion7.game.stages.world.inventory.items.BowItem;
import conversion7.game.stages.world.objects.HumanSquad;
import conversion7.game.stages.world.unit.Unit;
import conversion7.test_steps.WorldSteps;
import conversion7.test_steps.asserts.WorldAsserts;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AcceptanceRangeAttackTest extends AbstractTests {


    @Test(invocationCount = 1)
    public void test_isRangeAttackPossible() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad1 = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());
                Unit unit = humanSquad1.getUnits().get(0);

                // 1
                actSection();
                unit.setSpecialization(Unit.UnitSpecialization.MELEE);

                assertSection();
                Assert.assertFalse(humanSquad1.isRangeAttackPossible());

                // 2 - need bullets
                actSection();
                WorldSteps.makeUnitCouldEquipItem(unit, BowItem.class);
                unit.setSpecialization(Unit.UnitSpecialization.RANGE);
                unit.getAreaObject().getMilitaryInventory().addItem(BowItem.class, 1);

                assertSection();
                Assert.assertFalse(humanSquad1.isRangeAttackPossible());

                // 3
                actSection();
                WorldSteps.makeUnitCouldEquipItem(unit, ArrowItem.class);
                unit.getAreaObject().getMilitaryInventory().addItem(ArrowItem.class, 1);

                assertSection();
                Assert.assertTrue(humanSquad1.isRangeAttackPossible());
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_ExecuteRangeAttack() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad1 = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());
                Unit unitAttacker = humanSquad1.getUnits().get(0);
                WorldSteps.makeUnitCouldEquipItem(unitAttacker, ArrowItem.class);
                unitAttacker.getAreaObject().getMilitaryInventory().addItem(ArrowItem.class, 1);
                unitAttacker.setSpecialization(Unit.UnitSpecialization.RANGE);

                HumanSquad humanSquadTarget = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextNeighborCell());
                Unit unitUnderAttack = humanSquadTarget.getUnits().get(0);
                int healthBeforeAttack = unitUnderAttack.getParams().getHealth();

                actSection();
                Assert.assertTrue(humanSquad1.isRangeAttackPossible());
                WorldSteps.executeRangeAttackWithoutMiss(humanSquad1, humanSquadTarget);

                assertSection();
                // damage
                Assert.assertTrue(unitUnderAttack.getParams().getHealth() < healthBeforeAttack);
                // attacker spends bullets
                WorldAsserts.assertUnitHasNoEquippedRangeBullets(unitAttacker);
                // target cell gets bullets
                WorldAsserts.assertInventoryContainsItem(humanSquadTarget.getCell().getInventory(), ArrowItem.class, 1);
                // no bullets
                Assert.assertFalse(humanSquad1.isRangeAttackPossible());
            }

        }.run();
    }

    @Test(invocationCount = 1)
    public void test_ArmyDiesDuringRangeAttack() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad1 = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());
                Unit unitAttacker = humanSquad1.getUnits().get(0);
                WorldSteps.makeUnitCouldEquipItem(unitAttacker, ArrowItem.class);
                unitAttacker.getAreaObject().getMilitaryInventory().addItem(ArrowItem.class, 1);
                unitAttacker.setSpecialization(Unit.UnitSpecialization.RANGE);

                HumanSquad humanSquadTarget = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextNeighborCell());
                Unit unitUnderAttack = humanSquadTarget.getUnits().get(0);

                actSection();
                Assert.assertTrue(humanSquad1.isRangeAttackPossible());
                unitUnderAttack.getParams().setHealth(1);
                WorldSteps.executeRangeAttackWithoutMiss(humanSquad1, humanSquadTarget);

                assertSection();
                Assert.assertFalse(unitUnderAttack.isAlive());
                WorldAsserts.assertAreaObjectDefeated(humanSquadTarget);
            }
        }.run();
    }
}
