package tests.acceptance.inventory

import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.events.NewStepStartedEvent
import shared.BaseGdxgSpec
import spock.lang.Ignore

class GatheringStatisticTest extends BaseGdxgSpec {

    void 'test resource statistic'() {
        given:
        lockCore()
        def cell = worldSteps.getNextNeighborCell()
        cell.food = 1
        cell.water = 2
        def cell2 = worldSteps.getNextNeighborCell()
        cell2.food = 5
        cell2.water = 7
        def team = worldSteps.createHumanTeam()
        releaseCoreAndWaitNextCoreStep()

        and:
        def squad = worldSteps.createUnit(
                team,
                cell)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        NewStepStartedEvent event = worldSteps.getEvent(NewStepStartedEvent, squad.team)
        assert event.getHint().contains("Food: 1")
        assert event.getHint().contains("Water: 2")

        when:
        squad.moveOn(cell2)
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        NewStepStartedEvent event2 = worldSteps.getEvent(NewStepStartedEvent, squad.team)
        assert event2.getHint().contains("Food: 5")
        assert event2.getHint().contains("Water: 7")
    }

    @Ignore
    void 'test experience statistic'() {
        given:
        lockCore()
        def cell = worldSteps.getNextNeighborCell()
        def team = worldSteps.createHumanTeam()
        releaseCoreAndWaitNextCoreStep()

        and:
        def squad = worldSteps.createUnit(
                team,
                cell)
        team.updateEvolutionExp(Team.EVOLUTION_EXP_PER_EVOLUTION_POINT + 5)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        NewStepStartedEvent event = worldSteps.getEvent(NewStepStartedEvent, squad.team)
        assert event.getHint().contains("New evolution points +1")
        assert event.getHint().contains("New evolution experience +" + (Team.EVOLUTION_EXP_PER_EVOLUTION_POINT + 5))

    }


}
