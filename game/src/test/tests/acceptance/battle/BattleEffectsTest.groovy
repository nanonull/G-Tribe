package tests.acceptance.battle

import conversion7.engine.utils.MathUtils
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitParameterType
import conversion7.game.stages.world.unit.effects.items.ForestDefenceEffect
import conversion7.game.stages.world.unit.effects.items.HillDefenceEffect
import org.testng.Assert
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

import static org.fest.assertions.api.Assertions.assertThat

class BattleEffectsTest extends BaseGdxgSpec {

    public void 'test HillDefenceEffect'() {
        given:
        def squad1Attack = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        Cell defenderCell = worldSteps.getNextStandaloneCell();
        worldSteps.clearAllBattleModificatorsOnCell(defenderCell);
        defenderCell.getLandscape().setHill();
        def squad2Defend = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                defenderCell);
        Unit unit2 = squad2Defend.unit

        unit2.baseParams.put(UnitParameterType.STRENGTH, 100)
        unit2.baseParams.put(UnitParameterType.AGILITY, 100)
        unit2.baseParams.put(UnitParameterType.VITALITY, 100)
        int inStr = unit2.getTotalParam(UnitParameterType.STRENGTH);
        int inAgi = unit2.getTotalParam(UnitParameterType.AGILITY);
        int inVit = unit2.getTotalParam(UnitParameterType.VITALITY);
        int inDefence = unit2.getDefence();
        int inMeleeDamage = unit2.getMeleeDamage();

        int mltPercent = Math.round(HillDefenceEffect.PARAMS_MULTIPLIER * 100f)

        when:
        squad2Defend.unit.switchToDefendingMode(true)

        then:
        assertThat(unit2.getTotalParam(UnitParameterType.STRENGTH)).isEqualTo(MathUtils.multiplyOnPercent(inStr, mltPercent));
        assertThat(unit2.getTotalParam(UnitParameterType.AGILITY)).isEqualTo(MathUtils.multiplyOnPercent(inAgi, mltPercent));
        assertThat(unit2.getTotalParam(UnitParameterType.VITALITY)).isEqualTo(MathUtils.multiplyOnPercent(inVit, mltPercent));
        assertThat(unit2.getDefence()).isGreaterThan(inDefence);
        assertThat(unit2.getMeleeDamage()).isGreaterThan(inMeleeDamage);

        when:
        squad2Defend.unit.switchToDefendingMode(false)
        squad1Attack.meleeAttack(squad2Defend)

        then:
        Assert.assertEquals(unit2.getTotalParam(UnitParameterType.STRENGTH), inStr);
        Assert.assertEquals(unit2.getTotalParam(UnitParameterType.AGILITY), inAgi);
        Assert.assertEquals(unit2.getTotalParam(UnitParameterType.VITALITY), inVit);
        assertThat(unit2.getDefence()).isEqualTo(inDefence);
        assertThat(unit2.getMeleeDamage()).isEqualTo(inMeleeDamage);
    }

    public void 'test ForestDefenceEffect'() {
        given:
        Cell defenderCell = worldSteps.getNextStandaloneCell();
        worldSteps.clearAllBattleModificatorsOnCell(defenderCell);
        defenderCell.getLandscape().setForest();
        def squad1Defend = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                defenderCell);
        Unit unit1 = squad1Defend.unit
        def squad2Attack = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        unit1.baseParams.put(UnitParameterType.STRENGTH, 100)
        unit1.baseParams.put(UnitParameterType.AGILITY, 100)
        unit1.baseParams.put(UnitParameterType.VITALITY, 100)
        int inStr = unit1.getTotalParam(UnitParameterType.STRENGTH);
        int inAgi = unit1.getTotalParam(UnitParameterType.AGILITY);
        int inVit = unit1.getTotalParam(UnitParameterType.VITALITY);
        int inDefence = unit1.getDefence();
        int inMeleeDamage = unit1.getMeleeDamage();

        when:
        unit1.switchToDefendingMode(true)
        then:
        WorldAsserts.assertUnitHasEffect(unit1, ForestDefenceEffect.class);
        assertThat(unit1.getTotalParam(UnitParameterType.STRENGTH)).isEqualTo((int) (inStr * ForestDefenceEffect.PARAMS_MULTIPLIER));
        assertThat(unit1.getTotalParam(UnitParameterType.AGILITY)).isEqualTo((int) (inAgi * ForestDefenceEffect.PARAMS_MULTIPLIER));
        assertThat(unit1.getTotalParam(UnitParameterType.VITALITY)).isEqualTo((int) (inVit * ForestDefenceEffect.PARAMS_MULTIPLIER));
        assertThat(unit1.getDefence()).isGreaterThan(inDefence);
        assertThat(unit1.getMeleeDamage()).isGreaterThan(inMeleeDamage);

        when:
        unit1.switchToDefendingMode(false)
        squad2Attack.meleeAttack(squad1Defend)
        then:
        WorldAsserts.assertUnitHasNoEffect(unit1, ForestDefenceEffect.class);
        Assert.assertEquals(unit1.getTotalParam(UnitParameterType.STRENGTH), inStr);
        Assert.assertEquals(unit1.getTotalParam(UnitParameterType.AGILITY), inAgi);
        Assert.assertEquals(unit1.getTotalParam(UnitParameterType.VITALITY), inVit);
        assertThat(unit1.getDefence()).isEqualTo(inDefence);
        assertThat(unit1.getMeleeDamage()).isEqualTo(inMeleeDamage);
    }


}
