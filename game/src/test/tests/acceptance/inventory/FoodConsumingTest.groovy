package tests.acceptance.inventory

import conversion7.game.stages.world.inventory.items.AppleItem
import conversion7.game.stages.world.inventory.items.types.FoodItem
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitParameterType
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

class FoodConsumingTest extends BaseGdxgSpec {

    Team defaultTeam
    AbstractSquad humanSquad1

    @Override
    def setup() {
        defaultTeam = worldSteps.createHumanTeam()
        humanSquad1 = worldSteps.createUnit(defaultTeam
                , worldSteps.getNextStandaloneCell())
    }

    void 'test consume food'() {
        given:
        worldSteps.addItemToInventory(AppleItem, 2, humanSquad1.getInventory())
        def actionPoints1 = humanSquad1.unit.actionPoints

        humanSquad1.unit.hurt(Unit.FOOD_CONSUMING_HEALS + 1)
        def hp1 = humanSquad1.unit.getTotalParam(UnitParameterType.HEALTH)

//        humanSquad1.unit.updateFood(-(Unit.FOOD_CONSUMING_ADDS_FOOD + 1))
//        def food1 = humanSquad1.unit.food
        when:
        humanSquad1.unit.consumeFood((FoodItem) humanSquad1.inventory.getItem(AppleItem))

        then:
        WorldAsserts.assertInventoryContainsItem(humanSquad1.getInventory(), AppleItem, 1)
        assert humanSquad1.unit.actionPoints == actionPoints1 - ActionPoints.CONSUME
        assert humanSquad1.unit.getTotalParam(UnitParameterType.HEALTH) == hp1 + Unit.FOOD_CONSUMING_HEALS
//        assert humanSquad1.unit.food == food1 + Unit.FOOD_CONSUMING_ADDS_FOOD

    }

}
