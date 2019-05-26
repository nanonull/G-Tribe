package tests.acceptance.world.team.events

import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.team.Team
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

class UnitDefeatedEventTest extends BaseGdxgSpec {

    public void 'test SquadDefeatedEvent'() {
        given:

        Team team1 = worldSteps.createHumanTeam()
        AbstractSquad squad1 = worldSteps.createUnit(team1,
                worldSteps.getNextStandaloneCell());
        worldSteps.createUnitGarantsTeamNotDefeated(team1)

        when:
        worldSteps.makeUnitWillBeKilledOnWorldEndStepSimulation(squad1.unit)

        then:
        assert worldSteps.getSquadDefeatedEvent(squad1) == null: "team events " + squad1.getTeam().getEvents()

        when:
        worldSteps.rewindTeamsToStartNewWorldStep();

        then:
        WorldAsserts.assertAreaObjectDefeated(squad1, true, true);
        WorldAsserts.assertTeamDefeated(team1, false, false);
        and: "event added"
        assert worldSteps.getSquadDefeatedEvent(squad1): "team events " + squad1.getTeam().getEvents()
    }

}
