package tests.acceptance.world.team

import conversion7.game.stages.world.objects.unit.AbstractSquad
import shared.BaseGdxgSpec

class TeamsRelationsTest extends BaseGdxgSpec {

    void 'neutral team does not attack player by default'() {
        when:
        AbstractSquad squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        AbstractSquad squad2 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        def world = squad1.team.world

        then:
        assert world.getRelationBalance(squad1.team, squad2.team) == 0
    }


}
