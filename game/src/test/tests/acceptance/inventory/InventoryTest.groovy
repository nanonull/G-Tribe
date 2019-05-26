package tests.acceptance.inventory

import conversion7.engine.utils.Utils
import conversion7.game.stages.world.inventory.BasicInventory
import conversion7.game.stages.world.inventory.items.AppleItem
import conversion7.game.stages.world.inventory.items.MammothTuskItem
import conversion7.game.stages.world.inventory.items.StoneItem
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.unit.Unit
import org.slf4j.Logger
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

class InventoryTest extends BaseGdxgSpec {

    private static final Logger LOG = Utils.getLoggerForClass()

    Team defaultTeam
    AbstractSquad defaultHumanSquad

    @Override
    def setup() {
        defaultTeam = worldSteps.createHumanTeam()
        defaultHumanSquad = worldSteps.createUnit(defaultTeam
                , worldSteps.getNextStandaloneCell())
    }

    void 'test Add Item To Inventory'() {
        given:
        final int ITEM_QTY = 1

        when:
        worldSteps.addItemToInventory(StoneItem.class, ITEM_QTY, defaultHumanSquad.getInventory())
        then:
        WorldAsserts.assertInventoryContainsItem(defaultHumanSquad.getInventory(), StoneItem.class, ITEM_QTY)

        when: 'add more items'
        worldSteps.addItemToInventory(StoneItem.class, ITEM_QTY, defaultHumanSquad.getInventory())
        then:
        WorldAsserts.assertInventoryContainsItem(defaultHumanSquad.getInventory(), StoneItem.class, 2 * ITEM_QTY)
    }

    void 'test remove qty from inventory'() {
        given:
        worldSteps.addItemToInventory(AppleItem, 2, defaultHumanSquad.getInventory())

        when:
        defaultHumanSquad.getInventory().addItem(AppleItem, -1)
        then:
        WorldAsserts.assertInventoryContainsItem(defaultHumanSquad.getInventory(), AppleItem, 1)

        when: 'qty == 0'
        defaultHumanSquad.getInventory().addItem(AppleItem, -1)
        then:
        WorldAsserts.assertInventoryDoesntContainItem(defaultHumanSquad.getInventory(), AppleItem)
    }

    void 'test Cell Inventory After Army Defeated'() {
        given:

        final int ITEM_QTY = 1

        Team team = defaultTeam
        AbstractSquad army1 = worldSteps.createUnit(team, worldSteps.getNextStandaloneCell())
        Unit unit1 = army1.unit
        worldSteps.createUnitGarantsTeamNotDefeated(team)

        worldSteps.addItemToInventory(MammothTuskItem.class, ITEM_QTY, army1.getInventory())
        worldSteps.addDefaultEquipmentPackageToInventory(army1.inventory)

        BasicInventory defeatedArmyCellInventory = army1.getLastCell().getInventory()
        defeatedArmyCellInventory.clearItems()

        when:
        worldSteps.defeatKillUnit(unit1)

        then:
        WorldAsserts.assertUnitDead(unit1)
        WorldAsserts.assertAreaObjectDefeated(army1, true, false)
        WorldAsserts.assertInventoryContainsDefaultEquipmentPackage(defeatedArmyCellInventory)
        WorldAsserts.assertInventoryContainsItem(defeatedArmyCellInventory, MammothTuskItem.class, ITEM_QTY)
    }

}
