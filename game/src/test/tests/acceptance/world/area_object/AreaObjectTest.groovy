package tests.acceptance.world.area_object

import conversion7.game.stages.world.objects.unit.AbstractSquad
import shared.BaseGdxgSpec

class AreaObjectTest extends BaseGdxgSpec {

    void 'test unit is in getObjectsAround if unit2 created or moves on cell around'() {
        given:
        def team1 = worldSteps.createHumanTeam()
        def team2 = worldSteps.createHumanTeam()

        def squad1 = worldSteps.createUnit(
                team1,
                worldSteps.nextNeighborCell)

        when:
        def squad2 = worldSteps.createUnit(
                team2,
                worldSteps.nextNeighborCell)

        then:
        assert squad1.lastCell.getObjectsAround(AbstractSquad).toList().sort() == [squad2].sort()
        assert squad1.squadsAround.toList().sort() == [squad2].sort()
        assert squad2.lastCell.getObjectsAround(AbstractSquad).toList().sort() == [squad1].sort()
        assert squad2.squadsAround.toList().sort() == [squad1].sort()

        when:
        def squad3 = worldSteps.createUnit(
                team2,
                worldSteps.nextNeighborCell)
        then: 'squad1 doesnt see him'
        assert squad1.lastCell.getObjectsAround(AbstractSquad).toList().sort() == [squad2].sort()
        assert squad1.squadsAround.toList().sort() == [squad2].sort()
        assert !squad3.lastCell.getObjectsAround(AbstractSquad).contains(squad1)
        assert !squad3.squadsAround.contains(squad1)

        when: 'he moves into neib cells'
        squad3.moveOn(squad1.lastCell.getCell(0, 1))
        then: 'squad1 sees him'
        assert squad1.lastCell.getObjectsAround(AbstractSquad).toList().sort() == [squad2, squad3].sort()
        assert squad1.squadsAround.toList().sort() == [squad2, squad3].sort()
        and: 'squad2 sees him'
        assert squad3.lastCell.getObjectsAround(AbstractSquad).toList().sort() == [squad2, squad1].sort()
        assert squad3.squadsAround.toList().sort() == [squad2, squad1].sort()
        and: 'squad3 sees both'
        assert squad2.lastCell.getObjectsAround(AbstractSquad).toList().sort() == [squad1, squad3].sort()
        assert squad2.squadsAround.toList().sort() == [squad1, squad3].sort()
    }

    void 'test unit is not in getObjectsAround if unit2 moves out from cell around'() {
        given:
        def team1 = worldSteps.createHumanTeam()
        def team2 = worldSteps.createHumanTeam()

        def squad1 = worldSteps.createUnit(
                team1,
                worldSteps.nextNeighborCell)

        when:
        def squad2 = worldSteps.createUnit(
                team2,
                worldSteps.nextNeighborCell)
        then:
        assert squad1.lastCell.getObjectsAround(AbstractSquad).toList().sort() == [squad2].sort()
        assert squad1.squadsAround.toList().sort() == [squad2].sort()
        assert squad2.lastCell.getObjectsAround(AbstractSquad).toList().sort() == [squad1].sort()
        assert squad2.squadsAround.toList().sort() == [squad1].sort()

        when: 'he moves out'
        squad2.moveOn(worldSteps.nextStandaloneCell)
        then: 'squad1 not see him'
        assert squad1.lastCell.getObjectsAround(AbstractSquad).toList().sort() == []
        assert squad1.squadsAround.toList().sort() == []
    }
}
