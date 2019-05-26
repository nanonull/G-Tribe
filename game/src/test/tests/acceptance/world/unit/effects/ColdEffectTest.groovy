package tests.acceptance.world.unit.effects

import conversion7.engine.utils.Utils
import conversion7.game.stages.world.inventory.InventoryItemStaticParams
import conversion7.game.stages.world.inventory.items.SkinItem
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitParameterType
import conversion7.game.stages.world.unit.effects.items.ColdEffect
import org.slf4j.Logger
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

class ColdEffectTest extends BaseGdxgSpec {

    private static final Logger LOG = Utils.getLoggerForClass();

    public void 'test ColdEffect NO Clothes'() {
        given:
        Team team = worldSteps.createHumanTeam();
        def cell = worldSteps.getNextNeighborCell()
//        AbstractSquad squad = WorldServices.createUnit(team, cell, SahelanthropusTchadensis);
        Unit unit = squad.unit;
        LOG.info("Test unit " + unit);
        int equipHeat = 0;

        when: "the lowest healthy T"
        Cell nextCell = worldSteps.getNextNeighborCell();
        nextCell.setTemperature(Unit.HEALTHY_TEMPERATURE_MIN - equipHeat);
        worldSteps.moveOnCell(squad, nextCell);
        then:
        WorldAsserts.assertUnitHasNoEffect(unit, ColdEffect.class);

        when:
        worldSteps.rewindTeamsToStartNewWorldStep();
        then:
        WorldAsserts.assertUnitHasNoEffect(unit, ColdEffect.class);

        when: "the highest NOT-healthy T"
        nextCell = worldSteps.getNextNeighborCell();
        nextCell.setTemperature(Unit.HEALTHY_TEMPERATURE_MIN - equipHeat - 1);
        worldSteps.moveOnCell(squad, nextCell);

        then:
        WorldAsserts.assertUnitHasEffect(unit, ColdEffect.class);

        when:
        worldSteps.rewindTeamsToStartNewWorldStep();
        then:
        WorldAsserts.assertUnitHasEffect(unit, ColdEffect.class);
    }

    public void 'test ColdEffect With Clothes'() {
        given:
        Team team = worldSteps.createHumanTeam();
        def cell = worldSteps.getNextNeighborCell()
//        AbstractSquad squad = WorldServices.createUnit(team, cell, SahelanthropusTchadensis);
        Unit unit = squad.unit;
        LOG.info("Test unit " + unit);
        unit.getEquipment().equipClothesItem(new SkinItem());
        int equipHeat = InventoryItemStaticParams.SKIN.getHeat();

        when: "the lowest healthy T with equipment"
        Cell nextCell = worldSteps.getNextNeighborCell();
        nextCell.setTemperature(Unit.HEALTHY_TEMPERATURE_MIN - equipHeat);
        worldSteps.moveOnCell(squad, nextCell);
        then:
        WorldAsserts.assertUnitHasNoEffect(unit, ColdEffect.class);

        when:
        worldSteps.rewindTeamsToStartNewWorldStep();
        then:
        WorldAsserts.assertUnitHasNoEffect(unit, ColdEffect.class);

        when: "the highest NOT-healthy T with equipment"
        nextCell = worldSteps.getNextNeighborCell();
        nextCell.setTemperature(Unit.HEALTHY_TEMPERATURE_MIN - equipHeat - 1);
        worldSteps.moveOnCell(squad, nextCell);

        then:
        WorldAsserts.assertUnitHasEffect(unit, ColdEffect.class);

        when:
        worldSteps.rewindTeamsToStartNewWorldStep();
        then:
        WorldAsserts.assertUnitHasEffect(unit, ColdEffect.class);
    }

    public void 'test Unit got Cold Effect when created on cold cell'() {
        given: "stateBodyText baseTemperature is deadly"
        def cell = worldSteps.getNextStandaloneCell()
        int coldTemperature = Unit.HEALTHY_TEMPERATURE_MIN - 1;
        worldSteps.makePerfectConditionsOnCell(cell);
        cell.setTemperature(coldTemperature);

        when: "unit is created on that stateBodyText"
        worldSteps.createTeamTempGarantNoZeroTeamsInWorld()
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                cell);

        Unit unit = humanSquad1.unit
        def preHealth = unit.getTotalParam(UnitParameterType.HEALTH)

        then: "at once create ColdEffect effect"
        WorldAsserts.assertUnitHasEffect(unit, ColdEffect.class);
        assert unit.getTotalParam(UnitParameterType.HEALTH) == preHealth

        when:
        worldSteps.rewindTeamsToStartNewWorldStep();
        Utils.sleepThread(1000)

        then: "ColdEffect effect still exist"
        WorldAsserts.assertUnitAlive(unit);
        WorldAsserts.assertUnitHasEffect(unit, ColdEffect.class);

        and: "health-- on the same step"
        assert unit.getTotalParam(UnitParameterType.HEALTH) == preHealth - 1
    }

    public void 'test UnitDiesDueToCold'() {
        given:
        worldSteps.createTeamTempGarantNoZeroTeamsInWorld()
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());


        Cell cell = humanSquad1.getLastCell();
        int coldTemperature = Unit.HEALTHY_TEMPERATURE_MIN - 1;
        worldSteps.makePerfectConditionsOnCell(cell);
        cell.setTemperature(coldTemperature); // and only baseTemperature is deadly
        Unit unit = humanSquad1.unit
        unit.getBaseParams().put(UnitParameterType.HEALTH, 1);

        when:
        worldSteps.rewindTeamsToStartNewWorldStep();

        then:
        WorldAsserts.assertUnitDead(unit);
        WorldAsserts.assertAreaObjectDefeated(humanSquad1, true, true);

    }

}
