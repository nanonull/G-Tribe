package tests.acceptance.world.unit.actions

import conversion7.game.stages.world.inventory.items.ArrowItem
import conversion7.game.stages.world.inventory.items.weapons.BowItem
import conversion7.game.stages.world.objects.AreaObject
import conversion7.game.stages.world.objects.actions.items.RangeAttackAction
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitParameterType
import org.testng.Assert
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

public class RangeAttackActionTest extends BaseGdxgSpec {


    public void 'test RangeAttackActionAppear after RangeBulletsItem equipped'() {
        given:
        def squad = worldSteps.createUnit(
                worldSteps.createHumanTeam()
                , worldSteps.getNextStandaloneCell());
        squad.unit.setSpecialization(AreaObject.UnitSpecialization.RANGE)
        WorldAsserts.assertAreaObjectHasNoAction(squad, RangeAttackAction.class);

        when:
        def arrowItem = new ArrowItem()
        arrowItem.quantity = 1
        squad.unit.equipment.equipRangeBulletsItem(arrowItem)

        then:
        WorldAsserts.assertAreaObjectHasAction(squad, RangeAttackAction.class);
    }

    public void 'test RangeAttackAction removed after bullets end'() {
        given:
        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam()
                , worldSteps.getNextStandaloneCell());

        and: "range unit exist"
        Unit rangeUnit = squad1.unit;
        def arrowItem = new ArrowItem()
        arrowItem.quantity = 1
        rangeUnit.equipment.equipRangeBulletsItem(arrowItem)
        rangeUnit.updateActionPointsAndValidate(+999)
        WorldAsserts.assertAreaObjectHasAction(squad1, RangeAttackAction.class);

        when:
        def squad2 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());
        worldSteps.prepareForSuccessfulHit(squad1, squad2)
        squad1.unit.executeRangeAttack(squad2);

        then:
        WorldAsserts.assertAreaObjectHasNoAction(squad1, RangeAttackAction.class);
    }

    public void 'test rangeAttack IsNot Possible IfNo ActionPoints'() {
        when:
        def humanSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        and: "range unit exist"
        Unit rangeUnit = humanSquad.unit;
        def arrowItem = new ArrowItem()
        arrowItem.quantity = 9
        rangeUnit.equipment.equipRangeBulletsItem(arrowItem)

        then:
        WorldAsserts.assertRangeAttackPossible(humanSquad);

        when:
        rangeUnit.updateActionPointsAndValidate(-rangeUnit.actionPoints)
        then:
        WorldAsserts.assertRangeAttackNotPossible(humanSquad);
    }

    public void 'test isRangeAttackPossible'() {
        when:
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        Unit unit = humanSquad1.unit
        then:
        assert !humanSquad1.unit.canRangeAttack()

        when: "add range weapon"
        unit.equipment.equipRangeWeaponItem(new BowItem())
        then: "still not enough for range - need bullets"
        assert !humanSquad1.unit.canRangeAttack()

        when:
        unit.equipment.equipRangeBulletsItem(new ArrowItem())
        then:
        assert humanSquad1.unit.canRangeAttack()
    }

    public void 'test ExecuteRangeAttack'() {
        given:
        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        Unit unitAttacker = squad1.unit
        unitAttacker.equipment.equipRangeBulletsItem(new ArrowItem())

        def squad2 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        Unit unitUnderAttack = squad2.unit
        int healthBeforeAttack = unitUnderAttack.getBaseParams().get(UnitParameterType.HEALTH);

        when:
        assert squad1.unit.canRangeAttack()
        worldSteps.prepareForSuccessfulHit(squad1, squad2)
        squad1.unit.executeRangeAttack(squad2);

        then:
        // damage
        assert (unitUnderAttack.getBaseParams().get(UnitParameterType.HEALTH) < healthBeforeAttack);
        // attacker spends bullets
        WorldAsserts.assertUnitHasNoEquippedRangeBullets(unitAttacker);
        // target stateBodyText gets bullets
        WorldAsserts.assertInventoryContainsItem(squad2.getLastCell().getInventory(), ArrowItem.class, 1);
        // no bullets
        assert !(squad1.unit.canRangeAttack());
    }

    public void 'test TargetArmy Dies UnderRangeAttack'() {
        given:
        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        Unit unitAttacker = squad1.unit
        unitAttacker.equipment.equipRangeBulletsItem(new ArrowItem())
        worldSteps.makeUnitStrong(unitAttacker)

        def squad2 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        when:
        Assert.assertTrue(squad1.unit.canRangeAttack());
        squad2.unit.getBaseParams().put(UnitParameterType.HEALTH, 1);
        worldSteps.prepareForSuccessfulHit(squad1, squad2)
        squad1.unit.executeRangeAttack(squad2);

        then:
        assert !(squad2.unit.isAlive());
        WorldAsserts.assertAreaObjectDefeated(squad2);
        WorldAsserts.assertUnitDead(squad2.unit);
    }

}
