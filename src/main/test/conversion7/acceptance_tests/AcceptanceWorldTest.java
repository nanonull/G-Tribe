package conversion7.acceptance_tests;

import com.badlogic.gdx.utils.Array;
import conversion7.AAATest;
import conversion7.AbstractTests;
import conversion7.game.GdxgConstants;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.world.Climate;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.inventory.items.SkinItem;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.FoodStorage;
import conversion7.game.stages.world.objects.HumanSquad;
import conversion7.game.stages.world.objects.actions.RitualAction;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitFertilizer;
import conversion7.game.stages.world.unit.effects.items.Childbearing;
import conversion7.game.stages.world.unit.effects.items.Cold;
import conversion7.game.stages.world.unit.effects.items.Healing;
import conversion7.test_steps.BattleSteps;
import conversion7.test_steps.WorldSteps;
import conversion7.test_steps.asserts.WorldAsserts;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AcceptanceWorldTest extends AbstractTests {

    @BeforeClass
    @Override
    public void beforeClass() {
        super.beforeClass();
        GdxgConstants.AI_AREA_OBJECT_ENABLED = false;
    }

    @Test
    public void test_NewUnitAddedToTeam() {
        new AAATest() {
            @Override
            public void body() {
                Team team = World.createHumanTeam(false);
                HumanSquad army = team.createHumanSquad(WorldSteps.getNextStandaloneCell());

                Unit unit = WorldServices.createSomeHumanUnit();
                WorldSteps.addUnitToAreaObject(unit, army);

                WorldAsserts.assertTeamControllerContainsUnit(team.getTeamClassesManager(), unit);
                WorldAsserts.assertTeamClassesManagerContainsInfoAbout(team.getTeamClassesManager(), unit.getClass(), 1);
            }
        }.run();
    }

    @Test
    public void test_UnitRemovedFromTeam() {
        new AAATest() {
            @Override
            public void body() {

            }
        }.run();
    }

    @Test
    public void test_UnitKilledByCellConditions() {
        new AAATest() {
            @Override
            public void body() {

            }
        }.run();
    }

    @Test
    public void test_ColdEffectNoClothes() {
        new AAATest() {
            @Override
            public void body() {
                Team team = World.createHumanTeam(false);
                HumanSquad army = team.createHumanSquad(WorldSteps.getNextStandaloneCell());

                Unit unit = WorldServices.createSomeHumanUnit();
                WorldSteps.addUnitToAreaObject(unit, army);

                // test positive border
                WorldSteps.setUnitTemperature(unit, Unit.HEALTHY_TEMPERATURE_MIN + 1);
                Cell nextCell = WorldSteps.getNextNeighborCell();
                nextCell.setTemperature(Climate.TEMPERATURE_MIN);
                WorldSteps.moveObjectOnCell(army, nextCell);

                WorldAsserts.assertUnitHasNoEffect(unit, Cold.class);

                // test negative border
                WorldSteps.setUnitTemperature(unit, Unit.HEALTHY_TEMPERATURE_MIN);
                nextCell = WorldSteps.getNextNeighborCell();
                nextCell.setTemperature(Climate.TEMPERATURE_MIN);
                WorldSteps.moveObjectOnCell(army, nextCell);

                WorldAsserts.assertUnitHasEffect(unit, Cold.class);

                // remove cold, positive border#2
                nextCell = WorldSteps.getNextNeighborCell();
                nextCell.setTemperature(Unit.HEALTHY_TEMPERATURE_MIN);
                WorldSteps.moveObjectOnCell(army, nextCell);

                WorldAsserts.assertUnitHasNoEffect(unit, Cold.class);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_TemperatureBalanceWithClothes() {
        new AAATest() {
            @Override
            public void body() {
                Team team = World.createHumanTeam(false);
                HumanSquad army = team.createHumanSquad(WorldSteps.getNextStandaloneCell());

                Unit unit = WorldServices.createSomeHumanUnit();
                LOG.info("Test unit " + unit);
                WorldSteps.addUnitToAreaObject(unit, army);
                unit.getEquipment().equipClothesItem(new SkinItem());
                int additionalHeat = InventoryItemStaticParams.SKIN.getHeat();


                // test positive border
                WorldSteps.setUnitTemperature(unit, Unit.HEALTHY_TEMPERATURE_MIN - additionalHeat + 1);
                Cell nextCell = WorldSteps.getNextNeighborCell();
                nextCell.setTemperature(Climate.TEMPERATURE_MIN);
                WorldSteps.moveObjectOnCell(army, nextCell);

                //+6 unit current temperature
                WorldAsserts.assertUnitHasNoEffect(unit, Cold.class);

                //+5 unit current temperature
                WorldSteps.rewindTeamsToStartNewWorldStep();
                WorldAsserts.assertUnitHasTemperature(unit, Unit.HEALTHY_TEMPERATURE_MIN - additionalHeat);
                WorldAsserts.assertUnitHasEffect(unit, Cold.class);

                //
                // remove cold, positive border#2
                nextCell = WorldSteps.getNextNeighborCell();
                nextCell.setTemperature(Unit.HEALTHY_TEMPERATURE_MIN - additionalHeat);
                WorldSteps.moveObjectOnCell(army, nextCell);

                WorldAsserts.assertUnitHasNoEffect(unit, Cold.class);

                // test negative border
                WorldSteps.setUnitTemperature(unit, Unit.HEALTHY_TEMPERATURE_MIN - additionalHeat);
                nextCell = WorldSteps.getNextNeighborCell();
                nextCell.setTemperature(Climate.TEMPERATURE_MIN);
                WorldSteps.moveObjectOnCell(army, nextCell);

                WorldAsserts.assertUnitHasEffect(unit, Cold.class);


            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_FoodCollectedOverLimitWhenArmyStays() {
        new AAATest() {
            int newCellFood = 100;

            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());
                int previousFood = humanSquad.getFoodStorage().getFood();
                humanSquad.getCell().setFood(newCellFood);

                WorldSteps.rewindTeamsToStartNewWorldStep();

                int howManyFoodEaten = Unit.EAT_FOOD_QUANTITY * humanSquad.getUnits().size;
                WorldAsserts.assertAreaObjectFoodStorageIs(humanSquad, newCellFood + previousFood
                        - howManyFoodEaten - FoodStorage.FOOD_LOSSES_PER_STEP);

                //2
                humanSquad.moveOn(humanSquad.getCell().getCouldBeSeizedNeighborCell());
                WorldAsserts.assertAreaObjectFoodStorageIs(humanSquad,
                        humanSquad.getUnits().size * FoodStorage.FOOD_STORAGE_PER_UNIT);
            }

        }.run();
    }

    @Test(invocationCount = 1)
    public void test_mergeArmies() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad willBeMerged = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());
                HumanSquad mainArmy = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextNeighborCell());

                Cell cellOfMergedArmy = willBeMerged.getCell();

                Array<AbstractInventoryItem> cellInventoryBeforeMerge = new Array<>();
                cellOfMergedArmy.getInventory().getItems(cellInventoryBeforeMerge);

                Array<AbstractInventoryItem> mainInventoryWillBeMerged = new Array<>();
                willBeMerged.getMainInventory().getItems(mainInventoryWillBeMerged);
                // skip military inventory merging test, because items could be get by units... too complex
                // if MainInventory merge passed than military inventory also should be passed
                // willBeMerged.getMilitaryInventory().getItems();

                Array<Unit> unitsWillBeMoved = new Array<>(willBeMerged.getUnits());

                // ACT
                willBeMerged.mergeMeInto(mainArmy);

                // ASSERT
                // check inventories
                WorldAsserts.assertInventoryContainsItemsOnly(cellOfMergedArmy.getInventory(), cellInventoryBeforeMerge);
                WorldAsserts.assertInventoryContainsItemsAtLeast(mainArmy.getMainInventory(), mainInventoryWillBeMerged);

                // check units
                WorldAsserts.assertAreaObjectContainsUnits(mainArmy, unitsWillBeMoved);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_ChildbearingTick() {

        GdxgConstants.AI_AREA_OBJECT_ENABLED = false;

        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad1 = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());
                humanSquad1.setName("test_Childbearing object");
                Unit female = humanSquad1.getUnits().get(0);
                female.setGender(false);

                Unit male = WorldServices.createSomeHumanUnit();
                male.setGender(true);
                humanSquad1.getUnitsController().addUnitAndValidate(male);

                Assert.assertTrue(UnitFertilizer.fertilize(male, female, 100));
                WorldAsserts.assertUnitHasEffect(female, Childbearing.class);
                Childbearing childbearing = female.getEffectManager().getEffectCasted(Childbearing.class);
                WorldAsserts.assertEffectHasTickCounter(childbearing, 0);
                Unit futureChild = childbearing.getChild();

                actSection();
                WorldSteps.rewindTeamsToStartNewWorldStep();

                assertSection();
                WorldAsserts.assertEffectHasTickCounter(childbearing, 1);

                actSection();
                WorldSteps.rewindWorldSteps(Childbearing.PREGNANCY_DURATION - 1);

                assertSection();
                WorldAsserts.assertAreaObjectContainsUnit(humanSquad1, futureChild);
                if (female.getEffectManager().containsEffect(Childbearing.class)) {
                    Childbearing childbearing2 = female.getEffectManager().getEffectCasted(Childbearing.class);
                    Assert.assertNotEquals(childbearing, childbearing2, "it should be new effect");
                    WorldAsserts.assertEffectHasTickCounter(childbearing2, 0);
                }
            }

            @Override
            public void tearDown() {
                GdxgConstants.AI_AREA_OBJECT_ENABLED = true;
            }

        }.run();
    }

    @Test(invocationCount = 1)
    public void test_HealingTick() {

        GdxgConstants.AI_AREA_OBJECT_ENABLED = false;

        new AAATest() {
            @Override
            public void body() {
                Cell nextStandaloneCell = WorldSteps.getNextStandaloneCell();
                WorldSteps.makePerfectConditionsOnCell(nextStandaloneCell);
                HumanSquad humanSquad1 = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), nextStandaloneCell);
                Unit unit = humanSquad1.getUnits().get(0);
                WorldAsserts.assertUnitHasEffect(unit, Healing.class);
                Healing healing = unit.getEffectManager().getEffectCasted(Healing.class);

                unit.getParams().updateHealth(-2);
                int healthBeforeHeal = unit.getParams().getHealth();
                int expHealthAfterHealing = healthBeforeHeal + 1;


                actSection();
                WorldSteps.rewindTeamsToStartNewWorldStep();

                assertSection();
                WorldAsserts.assertUnitHasHealth(unit, healthBeforeHeal);
                WorldAsserts.assertEffectHasTickCounter(healing, 1);

                actSection();
                WorldSteps.rewindWorldSteps(Healing.HEALING_LENGTH_STEPS - 1);

                assertSection();
                WorldAsserts.assertUnitHasHealth(unit, expHealthAfterHealing);
                WorldAsserts.assertUnitHasEffect(unit, Healing.class);
                WorldAsserts.assertEffectHasTickCounter(healing, 0);
            }

            @Override
            public void tearDown() {
                GdxgConstants.AI_AREA_OBJECT_ENABLED = true;
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_TeamDefeatedWhenLastSquadWasJoined() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad1 = WorldServices.createHumanSquadWithSomeUnit(
                        World.createHumanTeam(false),
                        WorldSteps.getNextStandaloneCell());

                HumanSquad humanSquad2 = WorldServices.createHumanSquadWithSomeUnit(
                        World.createHumanTeam(false),
                        WorldSteps.getNextStandaloneCell());

                Team humanSquad1Team = humanSquad1.getTeam();
                Team humanSquad2Team = humanSquad2.getTeam();

                humanSquad2Team.removeSquad(humanSquad2);
                WorldAsserts.assertTeamDefeated(humanSquad2Team);

                humanSquad1Team.addSquad(humanSquad2);
                Assert.assertEquals(humanSquad1Team, humanSquad2.getTeam());
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_TribesSeparationIncreaseOnBattle() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad1 = WorldServices.createHumanSquadWithSomeUnit(
                        World.createHumanTeam(false),
                        WorldSteps.getNextStandaloneCell());

                HumanSquad humanSquad2 = WorldServices.createHumanSquadWithSomeUnit(
                        World.createHumanTeam(false),
                        WorldSteps.getNextStandaloneCell());

                BattleSteps.setAutoBattle(true);

                int preTotalTribesSeparationValue = World.getTotalTribesSeparationValue();
                humanSquad1.attack(humanSquad2);

                Assert.assertEquals(World.getTotalTribesSeparationValue(), preTotalTribesSeparationValue + World.INCREASE_TRIBES_SEPARATION_PER_BATTLE);
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_EvolutionPointFromRitual() {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad1 = WorldServices.createHumanSquadWithSomeUnit(
                        World.createHumanTeam(false),
                        WorldSteps.getNextStandaloneCell());

                int foodPerPoint = Team.EVOLUTION_SUB_POINTS_PER_POINT
                        / RitualAction.EVOLUTION_SUBPOINT_PER_FOOD;
                humanSquad1.getFoodStorage().updateFoodOnValueAndValidate(foodPerPoint);

                int evolutionPoints = humanSquad1.getTeam().getEvolutionPoints();
                RitualAction.complete(foodPerPoint, humanSquad1);
                Assert.assertEquals(humanSquad1.getTeam().getEvolutionPoints(), evolutionPoints + 1);
            }
        }.run();
    }


}
