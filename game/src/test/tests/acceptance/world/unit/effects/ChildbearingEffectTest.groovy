package tests.acceptance.world.unit.effects

import com.badlogic.gdx.utils.Array
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.effects.items.ChildbearingEffect
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

class ChildbearingEffectTest extends BaseGdxgSpec {

    public void 'test Childbearing'() {
        given:
        def humanSquad1 = worldSteps.createFertilizedUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());
        Unit mother = humanSquad1.unit;
        ChildbearingEffect childbearing = mother.getEffectManager().getEffect(ChildbearingEffect.class);
        Unit futureChild = childbearing.getChild();
        worldSteps.makePerfectConditionsOnCell(humanSquad1.getLastCell());

        when:
        worldSteps.rewindTeamsToStartNewWorldStep();

        then:
        WorldAsserts.assertUnitEffectHasTickCounter(childbearing, 1);

        when:
        worldSteps.rewindWorldSteps(ChildbearingEffect.PREGNANCY_DURATION - 1);

        then:
        assert mother.getEffectManager().getEffect(ChildbearingEffect) == null

        and: "futureChild was born"
        Array<Cell> cells = new Array<>(mother.squad.getLastCell().getCellsAround());
        for (Cell cell : cells) {
            if (cell.hasSquad() && cell.squad.unit == futureChild) {
                return
            }
        }
        throw new AssertionError("Cells:\n $cells doesnt contain futureChild:\n $futureChild")
    }


}
