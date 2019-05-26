package tests.acceptance.world.unit

import com.badlogic.gdx.scenes.scene2d.ui.Label
import conversion7.engine.utils.Utils
import conversion7.game.stages.world.objects.food.AbstractFoodStorage
import conversion7.game.stages.world.objects.food.SquadFoodStorage
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitParameterType
import org.slf4j.Logger
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts
import spock.lang.Ignore

class CommonSquadTest extends BaseGdxgSpec {

    private static final Logger LOG = Utils.getLoggerForClass(CommonSquadTest)

    void 'test SquadPowerHint'() {
        given:
        lockCore()
        AbstractSquad humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())
        releaseCore()
        waitForNextCoreStep()

        Unit unit = humanSquad1.unit
        Label powerLabel = humanSquad1.getUnitInWorldHintPanel().getPowerLabel()

        when: "Power changed on units array changes"
        float power1 = humanSquad1.getPowerValue()

        then:
        assert "30" == powerLabel.getText().toString()

        when: "Power changed on single unit parameters change"
        unit.getBaseParams().update(UnitParameterType.STRENGTH, 1)
        float power2 = humanSquad1.getPowerValue()
        waitForNextCoreStep()

        then:
        assert power2 != power1
        assert "31" == powerLabel.getText().toString()
    }

    @Ignore
    def 'test FoodCollectedOverLimitWhenArmyStays'() {
        given:
        int newCellFood = 100

        lockCore()
        AbstractSquad humanSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())
//        int previousFood = humanSquad.unit.getFood()
        humanSquad.getLastCell().setFood(newCellFood)
        releaseCore()
        waitForNextCoreStep()

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()
        int howManyFoodEaten = Unit.HEALTHY_CELL_FOOD_MIN

        then:
        WorldAsserts.assertAreaObjectFoodStorageIs(humanSquad, newCellFood + previousFood
                - howManyFoodEaten - AbstractFoodStorage.FOOD_LOSSES_PER_STEP)

        when:
        worldSteps.moveOnCell(humanSquad, humanSquad.getLastCell().getCouldBeSeizedNeighborCell())

        then:
        WorldAsserts.assertAreaObjectFoodStorageIs(humanSquad,
                SquadFoodStorage.FOOD_STORAGE_PER_UNIT)
    }

}
