package tests.acceptance.world.unit.actions

import conversion7.engine.utils.Utils
import conversion7.game.stages.world.objects.actions.items.FortifyAction
import conversion7.game.stages.world.objects.actions.items.RitualAction
import conversion7.game.stages.world.objects.unit.AbstractSquad
import org.slf4j.Logger
import org.testng.Assert
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts
import spock.lang.Ignore

public class CommonActionsTest extends BaseGdxgSpec {

    private static final Logger LOG = Utils.getLoggerForClass(CommonActionsTest);

    @Ignore
    def 'test FoodRelatedActionAppearance'() {
        given:
        lockCore()
        def humanSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        releaseCoreAndWaitNextCoreStep()

        when:
        humanSquad.unit.setFoodAndValidate(0);

        then:
        WorldAsserts.assertAreaObjectHasNoAction(humanSquad, RitualAction.class);

        when:
        humanSquad.getFoodStorage().setFoodAndValidate(1);

        then:
        WorldAsserts.assertAreaObjectHasAction(humanSquad, RitualAction.class);
    }


    def 'test CouldMakeActionsOnNewStep'() {
        given:
        AbstractSquad squad = null
        commonCoreStep {
            worldSteps.createHumanTeam(true);
            squad = worldSteps.createUnit(
                    worldSteps.createHumanTeam(),
                    worldSteps.getNextNeighborCell());
        }
        def unit1 = squad.unit

        Assert.assertTrue(unit1.canFortify());
        WorldAsserts.assertAreaObjectHasAction(squad, FortifyAction.class);

        when:
        worldSteps.setUnitAp(squad.unit, ActionPoints.FORTIFY)
        worldSteps.doFortify(squad)

        then:
        WorldAsserts.assertAreaObjectHasNoAction(squad, FortifyAction.class);

        when:
        worldSteps.rewindTeamsToStartNewWorldStep();

        then:
        WorldAsserts.assertAreaObjectHasNoAction(squad, FortifyAction.class);
    }

}
