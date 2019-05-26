package tests.acceptance.world.town

import conversion7.game.unit_classes.UnitClassConstants
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitParameterType
import shared.BaseGdxgSpec

class UnitInCampEffectTest extends BaseGdxgSpec {

    public void 'test no effect in not-completed town'() {
        given:
        Cell cell = worldSteps.getNextStandaloneCell();
        worldSteps.makePerfectConditionsOnCell(cell);
        def team = worldSteps.createHumanTeam()
        def squad = worldSteps.createUnit(team,
                cell);
        Unit unit = squad.unit

        def squad2 = worldSteps.createUnit(worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());

        def inAgi = unit.getTotalParam(UnitParameterType.AGILITY)
        assert inAgi == UnitClassConstants.BASE_PARAMS.get(UnitParameterType.AGILITY)
        def inDodgeChance = unit.dodgeChance(squad2.unit)

        when:
        worldSteps.startCampConstruction(team, cell)

        then: "unit params++"
        assert unit.getTotalParam(UnitParameterType.AGILITY) == inAgi
        assert inDodgeChance == unit.dodgeChance(squad2.unit)
    }

    public void 'test increase params in town (when create town)'() {
        given:
        Cell cell = worldSteps.getNextStandaloneCell();
        worldSteps.makePerfectConditionsOnCell(cell);
        def team = worldSteps.createHumanTeam()
        def squad = worldSteps.createUnit(team,
                cell);
        Unit unit = squad.unit

        def squad2 = worldSteps.createUnit(worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());

        def inAgi = unit.getTotalParam(UnitParameterType.AGILITY)
        assert inAgi == UnitClassConstants.BASE_PARAMS.get(UnitParameterType.AGILITY)
        def inDodgeChance = unit.dodgeChance(squad2.unit)

        when:
        worldSteps.createAndCompleteCampConstruction(team, cell)

        then: "unit params++"
        assert unit.getTotalParam(UnitParameterType.AGILITY) == 15
        assert inDodgeChance < unit.dodgeChance(squad2.unit)
    }

    public void 'test increase params in town (when move on town)'() {
        given:
        Cell cell = worldSteps.getNextStandaloneCell();
        worldSteps.makePerfectConditionsOnCell(cell);
        def team = worldSteps.createHumanTeam()
        def squad = worldSteps.createUnit(team,
                cell);
        Unit unit = squad.unit

        Cell cell2 = worldSteps.nextNeighborCell
        worldSteps.createAndCompleteCampConstruction(team, cell2)

        def squad2 = worldSteps.createUnit(worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());

        def inAgi = unit.getTotalParam(UnitParameterType.AGILITY)
        assert inAgi == UnitClassConstants.BASE_PARAMS.get(UnitParameterType.AGILITY)
        def inDodgeChance = unit.dodgeChance(squad2.unit)

        when:
        worldSteps.moveOnCell(squad, cell2)
        then: "unit params++"
        assert unit.getTotalParam(UnitParameterType.AGILITY) == 15
        assert inDodgeChance < unit.dodgeChance(squad2.unit)
    }


}
