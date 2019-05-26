package tests.acceptance.world.unit.actions

import conversion7.engine.Gdxg
import conversion7.engine.utils.Utils
import conversion7.game.GdxgConstants
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitParameterType
import org.slf4j.Logger
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

class AttackActionTest extends BaseGdxgSpec {

    private static final Logger LOG = Utils.getLoggerForClass()

    @Override
    def setup() {
        GdxgConstants.DEVELOPER_MODE = false
    }

    void 'test AttackRequiresAp'() {
        given:
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell())
        def unit = humanSquad1.unit

        when:
        unit.updateActionPointsAndValidate(-unit.getActionPoints())

        then:
        assert !unit.canMeleeAttack()
    }

    void 'test melee attack'() {
        given:
        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell())
        worldSteps.makeUnitInvincible(squad1.unit)

        def squad2 = worldSteps.createUnit(
                Gdxg.core.world.createHumanTeam(false),
                worldSteps.getNextNeighborCell())

        Unit unit1 = squad1.unit

        when: "ATTACK"
        def preHp2 = squad2.unit.getTotalParam(UnitParameterType.HEALTH)
        assert unit1.getActionPoints() == ActionPoints.UNIT_START_ACTION_POINTS
        def preAP1 = unit1.actionPoints
        worldSteps.prepareForSuccessfulHit(squad1, squad2)
        squad1.unit.executeMeleeAttack(squad2)

        then: "unit2 is damaged"
        assert unit1.getMadeAttacks() == 1
        assert preHp2 > squad2.unit.getTotalParam(UnitParameterType.HEALTH)

        and: "defeated"
        WorldAsserts.assertUnitDead(squad2.unit)

        and: "winner moved"
        assert unit1.getActionPoints() == preAP1 - ActionPoints.MELEE_ATTACK
    }

}
