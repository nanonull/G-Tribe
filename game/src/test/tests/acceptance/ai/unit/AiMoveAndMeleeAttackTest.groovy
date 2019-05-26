package tests.acceptance.ai.unit

import conversion7.engine.Gdxg
import conversion7.game.GdxgConstants
import org.mockito.Mockito
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

import static org.mockito.Mockito.times

class AiMoveAndMeleeAttackTest extends BaseGdxgSpec {

    @Override
    def setup() {
        GdxgConstants.AREA_OBJECT_AI = true
    }

    void 'test move and attack'() {
        given:
        lockCore()
        def cell1 = worldSteps.getNextNeighborCell()
        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                cell1);
        worldSteps.makeUnitInvincible(squad1.unit)

        def cell2 = worldSteps.getNextNeighborCell()
        def cell3 = worldSteps.getNextNeighborCell()

        def cell4 = worldSteps.getNextNeighborCell()
        def squad2 = worldSteps.createUnit(
                Gdxg.core.world.createHumanTeam(false),
                cell4);
        squad2.unit = Mockito.spy(squad2.unit)
        assert squad2.unit.actionPoints >= ActionPoints.MOVEMENT + ActionPoints.MELEE_ATTACK
        releaseCoreAndWaitNextCoreStep()
        WorldAsserts.assertSquadSeesAnotherOutOfStealth(squad2, squad1)
        worldSteps.makeEnemies(squad1, squad2)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then: "moved"
        assert squad2.lastCell == cell3

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then: "moved & attack"
        assert squad2.lastCell == cell2
        Mockito.verify(squad2.unit, times(1)).hit(squad1.unit, true)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then: "continue attack"
        sleep(1000)
        assert squad2.lastCell == cell2
        Mockito.verify(squad2.unit, times(2)).hit(squad1.unit, true)
    }

    void 'test NO move and attack for neutral'() {
        given:
        lockCore()
        def cell1 = worldSteps.nextNeighborCell
        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                cell1);
        worldSteps.makeUnitInvincible(squad1.unit)

        def cell2 = worldSteps.nextNeighborCell
        def cell3 = worldSteps.nextNeighborCell

        def squad2 = worldSteps.createUnit(
                Gdxg.core.world.createHumanTeam(false),
                cell3);
        squad2.unit = Mockito.spy(squad2.unit)
        assert squad2.unit.actionPoints >= ActionPoints.MOVEMENT + ActionPoints.MELEE_ATTACK
        releaseCoreAndWaitNextCoreStep()
        WorldAsserts.assertSquadSeesAnotherOutOfStealth(squad2, squad1)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then: "no move to attack"
        assert squad2.lastCell != cell2
        assert !squad1.team.isAllyOf(squad2.team)
        assert !squad1.team.isEnemyOf(squad2.team)
        assert !squad1.unit.isEnemyWith(squad2.unit)
        assert !squad2.unit.isEnemyWith(squad1.unit)

    }


}
