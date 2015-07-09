package conversion7.acceptance_tests;

import conversion7.AAATest;
import conversion7.AbstractTests;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.items.ArrowItem;
import conversion7.game.stages.world.objects.HumanSquad;
import conversion7.game.stages.world.objects.actions.FireAction;
import conversion7.game.stages.world.objects.actions.RangeAttackAction;
import conversion7.game.stages.world.objects.actions.RitualAction;
import conversion7.game.stages.world.objects.actions.ShareFoodAction;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.skills.FireSkill;
import conversion7.game.stages.world.unit.Unit;
import conversion7.test_steps.WorldSteps;
import conversion7.test_steps.asserts.WorldAsserts;
import org.testng.annotations.Test;

public class AreaObjectActionsTest extends AbstractTests {

    @Test(invocationCount = 1)
    public void test_FoodRelatedActionAppearance() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());

                actSection();
                humanSquad.getFoodStorage().setFoodAndValidate(0);

                assertSection();
                WorldAsserts.assertAreaObjectHasNoAction(humanSquad, RitualAction.class);
                WorldAsserts.assertAreaObjectHasNoAction(humanSquad, ShareFoodAction.class);

                actSection();
                humanSquad.getFoodStorage().setFoodAndValidate(1);

                assertSection();
                WorldAsserts.assertAreaObjectHasAction(humanSquad, RitualAction.class);
                WorldAsserts.assertAreaObjectHasAction(humanSquad, ShareFoodAction.class);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_FireSkillShouldAppearInExistingObject() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());

                assertSection();
                WorldAsserts.assertAreaObjectHasNoAction(humanSquad, FireAction.class);

                actSection();
                WorldSteps.teamLearnsSkill(humanSquad.getTeam(), FireSkill.class);

                assertSection();
                WorldAsserts.assertAreaObjectHasAction(humanSquad, FireAction.class);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_FireSkillShouldAppearInNewObject() {
        new AAATest() {
            @Override
            public void body() {
                Team humanTeam = World.createHumanTeam(false);
                WorldSteps.teamLearnsSkill(humanTeam, FireSkill.class);

                actSection();
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(humanTeam, WorldSteps.getNextStandaloneCell());

                assertSection();
                WorldAsserts.assertAreaObjectHasAction(humanSquad, FireAction.class);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_RangeAttackActionAppear_afterRangeUnitEquipsWeapon() {
        new AAATest() {
            @Override
            public void body() {
                WorldServices.nextUnitSpecialization = Unit.UnitSpecialization.RANGE;
                Unit someHumanUnit = WorldServices.createSomeHumanUnit();

                actSection();
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell(), someHumanUnit);

                assertSection();
                WorldAsserts.assertAreaObjectHasNoAction(humanSquad, RangeAttackAction.class);

                actSection();
                WorldSteps.makeUnitCouldEquipItem(someHumanUnit, ArrowItem.class);
                WorldSteps.addItemToInventory(ArrowItem.class, 1, humanSquad.getMilitaryInventory());

                assertSection();
                WorldAsserts.assertAreaObjectHasAction(humanSquad, RangeAttackAction.class);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_RangeAttackAction_afterRangeUnitAddedAndRemoved() {
        new AAATest() {
            @Override
            public void body() {
                WorldServices.nextUnitSpecialization = Unit.UnitSpecialization.RANGE;
                Unit rangeUnit = WorldServices.createSomeHumanUnit();
                HumanSquad humanSquad1 = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell(), rangeUnit);
                WorldSteps.makeUnitCouldEquipItem(rangeUnit, ArrowItem.class);
                WorldSteps.addItemToInventory(ArrowItem.class, 1, humanSquad1.getMilitaryInventory());
                // add extra dummy unit before move ranger
                WorldServices.nextUnitSpecialization = Unit.UnitSpecialization.MELEE;
                Unit meleeUnitInSquad1 = WorldServices.createSomeHumanUnit();
                humanSquad1.getUnitsController().addUnitAndValidate(meleeUnitInSquad1);
                WorldAsserts.assertAreaObjectHasAction(humanSquad1, RangeAttackAction.class);

                actSection();
                WorldServices.nextUnitSpecialization = Unit.UnitSpecialization.MELEE;
                Unit meleeUnitInSquad2 = WorldServices.createSomeHumanUnit();
                HumanSquad humanSquad2 = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell(), meleeUnitInSquad2);
                rangeUnit.moveIntoAndValidate(humanSquad2);

                assertSection();
                WorldAsserts.assertAreaObjectHasNoAction(humanSquad1, RangeAttackAction.class);
                WorldAsserts.assertAreaObjectHasAction(humanSquad2, RangeAttackAction.class);

            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_RangeAttackAction_addedWhenUnitChangedSpecializationAndEquipWeapon() {
        new AAATest() {
            @Override
            public void body() {
                WorldServices.nextUnitSpecialization = Unit.UnitSpecialization.MELEE;
                Unit futureRangeUnit = WorldServices.createSomeHumanUnit();
                HumanSquad humanSquad1 = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell(), futureRangeUnit);
                WorldSteps.makeUnitCouldEquipItem(futureRangeUnit, ArrowItem.class);
                WorldSteps.addItemToInventory(ArrowItem.class, 1, humanSquad1.getMilitaryInventory());
                WorldAsserts.assertAreaObjectHasNoAction(humanSquad1, RangeAttackAction.class);

                actSection();
                futureRangeUnit.setSpecialization(Unit.UnitSpecialization.RANGE);

                assertSection();
                WorldAsserts.assertAreaObjectHasAction(humanSquad1, RangeAttackAction.class);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_RangeAttackAction_removedAfterAttackIfItWasLastBullet() {
        new AAATest() {
            @Override
            public void body() {
                WorldServices.nextUnitSpecialization = Unit.UnitSpecialization.RANGE;
                Unit rangeUnit = WorldServices.createSomeHumanUnit();
                HumanSquad humanSquad1 = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell(), rangeUnit);
                WorldSteps.makeUnitCouldEquipItem(rangeUnit, ArrowItem.class);
                WorldSteps.addItemToInventory(ArrowItem.class, 1, humanSquad1.getMilitaryInventory());
                WorldAsserts.assertAreaObjectHasAction(humanSquad1, RangeAttackAction.class);

                HumanSquad humanSquad2 = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());

                actSection();
                humanSquad1.executeRangeAttack(humanSquad2);

                assertSection();
                WorldAsserts.assertAreaObjectHasNoAction(humanSquad1, RangeAttackAction.class);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_RangeAttackAction_addedAfterRequiredSkillLearned_andUnitEquipsWeapon() {
        new AAATest() {
            @Override
            public void body() {
                WorldServices.nextUnitSpecialization = Unit.UnitSpecialization.RANGE;
                Unit rangeUnit = WorldServices.createSomeHumanUnit();
                HumanSquad humanSquad1 = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell(), rangeUnit);
                WorldSteps.addItemToInventory(ArrowItem.class, 1, humanSquad1.getMilitaryInventory());
                WorldAsserts.assertAreaObjectHasNoAction(humanSquad1, RangeAttackAction.class);

                actSection();
                WorldSteps.makeUnitCouldEquipItem(rangeUnit, ArrowItem.class);

                assertSection();
                WorldAsserts.assertAreaObjectHasAction(humanSquad1, RangeAttackAction.class);

            }
        }.run();
    }

}
