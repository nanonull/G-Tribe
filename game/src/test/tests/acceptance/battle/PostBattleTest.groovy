package tests.acceptance.battle

import conversion7.engine.Gdxg
import conversion7.game.GdxgConstants
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.objects.actions.items.MeleeAttackAction
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.unit.Unit
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts
import spock.lang.Ignore

import static org.fest.assertions.api.Assertions.assertThat

class PostBattleTest extends BaseGdxgSpec {

    public void 'test BattleTakesAP withWinner - No Move'() {
        given:
        GdxgConstants.DEVELOPER_MODE = false

        lockCore()
        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());
        Unit unit1 = squad1.unit;
        def cell1 = squad1.lastCell
        worldSteps.makeUnitInvincible(unit1);

        def squad2 = worldSteps.createUnit(
                Gdxg.core.world.createHumanTeam(false),
                worldSteps.getNextNeighborCell());
        def cell2 = squad2.lastCell
        releaseCoreAndWaitNextCoreStep()

        when: "ATTACK"
        assert unit1.getActionPoints() == ActionPoints.UNIT_START_ACTION_POINTS
        worldSteps.prepareForSuccessfulHit(squad1, squad2)
        squad1.unit.executeMeleeAttack(squad2);

        then: "unit2 is defeated"
        WorldAsserts.assertUnitDead(squad2.unit)

        and: "winner not moved"
        assert squad1.lastCell == cell1
    }

    // no move anymore after defeating
    @Ignore
    public void 'test BattleTakesAP withWinner - stay (no AP for move)'() {
        GdxgConstants.DEVELOPER_MODE = false
        lockCore()
        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());
        def cell1 = squad1.lastCell
        Unit unit1 = squad1.unit;
        worldSteps.makeUnitInvincible(unit1);

        def squad2 = worldSteps.createUnit(
                Gdxg.core.world.createHumanTeam(false),
                worldSteps.getNextNeighborCell());
        releaseCoreAndWaitNextCoreStep()

        when: "ATTACK"
        worldSteps.setUnitAp(unit1, ActionPoints.MELEE_ATTACK)
        assert !squad1.unit.canMove()
        squad1.meleeAttack(squad2);

        then: "unit2 is defeated"
        WorldAsserts.assertUnitDead(squad2.unit)

        and: "winner not moved"
        assert squad1.lastCell == cell1
    }


    public void 'test BattleTakesAP noWinner - NoMove'() {
        GdxgConstants.DEVELOPER_MODE = false
        lockCore()
        AbstractSquad defSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        Unit defUnit = defSquad.unit
        int inDefAP1 = defUnit.getActionPoints();
        Cell defSquadCell = defSquad.getLastCell();

        AbstractSquad attacker = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        int inAttAP2 = attacker.unit.getActionPoints();
        Cell attackerCell = attacker.getLastCell();
        releaseCore()
        waitForNextCoreStep()

        when:
        GdxgConstants.DEVELOPER_MODE = false
        worldSteps.prepareForSuccessfulHit(attacker, defSquad)
        attacker.unit.executeMeleeAttack(defSquad)

        then:
        WorldAsserts.assertAreaObjectAlive(attacker);
        WorldAsserts.assertAreaObjectAlive(defSquad);

        assertThat(defSquad.getLastCell()).isEqualTo(defSquadCell);
        assertThat(attacker.getLastCell()).isEqualTo(attackerCell);

        WorldAsserts.assertUnitActionPointsIs(defUnit, inDefAP1);
        WorldAsserts.assertUnitActionPointsIs(attacker.unit, inAttAP2 - ActionPoints.MELEE_ATTACK);
    }

    public void 'test unit could attack one time per step'() {
        GdxgConstants.DEVELOPER_MODE = false

        lockCore()
        AbstractSquad defSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        AbstractSquad attacker = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        releaseCore()
        waitForNextCoreStep()


        when: "1st hit"
        worldSteps.prepareForSuccessfulHit(attacker, defSquad)
        attacker.unit.executeMeleeAttack(defSquad)

        then:
        WorldAsserts.assertAreaObjectHasNoAction(attacker, MeleeAttackAction.class);
        WorldAsserts.assertAreaObjectAlive(attacker);
        WorldAsserts.assertAreaObjectAlive(defSquad);
        WorldAsserts.assertAreaObjectHasAction(defSquad, MeleeAttackAction.class);
    }

}
