package tests.acceptance.world.team


import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitAge
import shared.BaseGdxgSpec

class TeamExperienceTest extends BaseGdxgSpec {

    def 'test evolution points UP'() {
        when:
        lockCore()
        AbstractSquad squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        def team = squad.team

        releaseCoreAndWaitNextCoreStep()

        then:
        assert team.evolutionExperience == 0

        when:
        int one = 1
        team.updateEvolutionExp(one)
        then:
        assert team.evolutionExperience == one
        assert team.evolutionPoints == Team.START_EVOLUTION_POINTS

        when:
        def updToOneBelowUp = Team.EVOLUTION_EXP_PER_EVOLUTION_POINT - 1 - one
        team.updateEvolutionExp(updToOneBelowUp)

        then:
        assert team.evolutionExperience == updToOneBelowUp + one
        assert team.evolutionPoints == Team.START_EVOLUTION_POINTS

        when: "evolution points == exactly amount to UP"
        team.updateEvolutionExp(one)

        then:
        assert team.evolutionExperience == 0
        assert team.evolutionPoints == 1 + Team.START_EVOLUTION_POINTS

        when: "evolution points == exactly amount to UP"
        team.updateEvolutionExp(Team.EVOLUTION_EXP_PER_EVOLUTION_POINT + one)

        then:
        assert team.evolutionExperience == one
        assert team.evolutionPoints == 2 + Team.START_EVOLUTION_POINTS
    }

    void 'test unit gets exp from seized food'() {
        given:
        lockCore()
        def cell = worldSteps.getNextNeighborCell()
        def team = worldSteps.createHumanTeam()
        def squad = worldSteps.createUnit(
                team,
                cell)
        squad.unit.updateExperience(Unit.BASE_EXP_FOR_LEVEL - 1)

        and:
        int wantExp = 1
        cell.food = (int) Math.round(wantExp / Team.EVOLUTION_EXP_PER_1_CLAIMED_FOOD)
        assert cell.food == 10
        releaseCoreAndWaitNextCoreStep()

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        assert squad.unit.age == UnitAge.YOUNG
        assert squad.unit.experience == Unit.BASE_EXP_FOR_LEVEL
    }

}
