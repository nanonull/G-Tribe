package tests.acceptance.world.team.skills

import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.skills.SkillType
import conversion7.game.stages.world.team.skills.items.FlayingSkill
import conversion7.game.stages.world.unit.Unit
import conversion7.game.unit_classes.animals.BaseAnimalClass
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

class StoneWorkSkillTest extends BaseGdxgSpec {

    public void 'test FoodWithoutFoodSkills'() {
        given:
        Team team1 = worldSteps.createHumanTeam();
        Team team2 = worldSteps.createAnimalTeam();
        AbstractSquad army1 = worldSteps.createUnit(team1, worldSteps.getNextNeighborCell());
        Unit unit1 = army1.unit;
        worldSteps.makeUnitInvincible(unit1);

        AbstractSquad animalHerd = worldSteps.createUnit(team2, worldSteps.getNextNeighborCell());

        when:
//        def preFood = army1.unit.food
        worldSteps.prepareForSuccessfulHit(army1, animalHerd)
        battleSteps.startBattle(army1, animalHerd);

        then:
        WorldAsserts.assertAreaObjectDefeated(animalHerd);
        and: "half food from animal is grab"
        assert army1.unit.foo == preFood + (BaseAnimalClass.FOOD_FROM_ONE_UNIT_TOTAL / 2).toInteger()
    }

    public void 'test FoodWithStoneWorkSkill'() {
        given:
        lockCore()
        Team team1 = worldSteps.createHumanTeam();
        Team team2 = worldSteps.createAnimalTeam();

        AbstractSquad army1 = worldSteps.createUnit(team1, worldSteps.getNextNeighborCell());
        Unit unit1 = army1.unit;
        worldSteps.makeUnitInvincible(unit1);
        worldSteps.teamLearnsSkill(team1, FlayingSkill.class);

        AbstractSquad animalHerd = worldSteps.createUnit(team2, worldSteps.getNextNeighborCell());
        releaseCoreAndWaitNextCoreStep()

        when:
        team1.getTeamSkillsManager().getSkill(SkillType.STONE_WORK).learn();
//        def preFood = army1.unit.food
        worldSteps.prepareForSuccessfulHit(army1, animalHerd)
        battleSteps.startBattle(army1, animalHerd);

        then:
        WorldAsserts.assertAreaObjectDefeated(animalHerd);
        and: "all food is grab"
        assert army1.unit.foo == preFood + BaseAnimalClass.FOOD_FROM_ONE_UNIT_TOTAL

    }

}
