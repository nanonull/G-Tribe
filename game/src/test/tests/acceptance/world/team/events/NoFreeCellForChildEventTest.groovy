package tests.acceptance.world.team.events

import conversion7.game.GdxgConstants
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.team.events.NoFreeCellForChildEvent
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.effects.items.ChildbearingEffect
import org.powermock.reflect.Whitebox
import shared.BaseGdxgSpec

import static org.fest.assertions.api.Assertions.assertThat

class NoFreeCellForChildEventTest extends BaseGdxgSpec {

    public void 'test 1'() {
        given:
        GdxgConstants.AREA_OBJECT_AI = false

        lockCore()
        worldSteps.createTeamTempGarantNoZeroTeamsInWorld()
        def team = worldSteps.createHumanTeam()
        def squad1 = worldSteps.createFertilizedUnit(team,
                worldSteps.getNextStandaloneCell());
        Unit mother = squad1.unit;
        ChildbearingEffect childbearing = mother.getEffectManager().getEffect(ChildbearingEffect.class);
        Unit futureChild = childbearing.getChild();

        worldSteps.transformCells(squad1.getLastCell().getCellsAround(), { Cell cell ->
            cell.landscapeController.setWaterCell()
        });
        worldSteps.makePerfectConditionsOnCell(squad1.getLastCell());
        releaseCoreAndWaitNextCoreStep()

        when:
        Whitebox.setInternalState(childbearing, "tickCounter", ChildbearingEffect.PREGNANCY_DURATION - 1);
        worldSteps.rewindTeamsToStartNewWorldStep()

        then: "ChildbearingEffect was completed"
        assert !mother.getEffectManager().containsEffect(ChildbearingEffect.class)

        and: "unit was not born"
        assertThat(futureChild.getSquad()).isNull();

        and: "notification is created"
        NoFreeCellForChildEvent event = squad1.getTeam().getEvents().toList()
                .stream().find { it instanceof NoFreeCellForChildEvent } as NoFreeCellForChildEvent
        assert event
        assert event.getSquad().equals(squad1): "actual events ${squad1.getTeam().getEvents()}"
    }

}
