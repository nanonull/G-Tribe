package tests.acceptance.inventory

import conversion7.game.PackageReflectedConstants
import conversion7.game.stages.world.inventory.items.*
import conversion7.game.stages.world.inventory.items.weapons.*
import conversion7.game.stages.world.team.events.NewStepStartedEvent
import org.mockito.internal.util.reflection.Whitebox
import shared.BaseGdxgSpec

class CellInventoryPickupTest extends BaseGdxgSpec {

    void 'test item classes were collected'() {
        given:
        List expInvItems = [
                AppleItem, ArrowItem, AtlatlItem, BowItem, CudgelItem, HammerItem, JavelinItem,
                MammothTuskItem, PikeItem, SkinItem, SkinRobeItem, SpearItem, StickItem,
                StoneItem, StringItem
        ]
        assert PackageReflectedConstants.INVENTORY_ITEM_CLASSES.toList().sort() == expInvItems.sort()
    }

    void 'test items are picked up when unit created'() {
        given:
        lockCore()
        def cell = worldSteps.getNextNeighborCell()
        cell.inventory.clearItems()
        def stickItem = new StickItem()
        stickItem.quantity = 2
        cell.inventory.addItem(stickItem)

        and:
        worldSteps.turnOffResourceGenerator(cell)
        def team = worldSteps.createHumanTeam()
        releaseCoreAndWaitNextCoreStep()

        when:
        def squad = worldSteps.createUnit(
                team,
                cell)

        then:
        assert cell.inventory.itemsIterator.size() == 0
        assert squad.inventory.itemsIterator.size() == 1
        assert squad.inventory.getItem(StickItem)
        assert squad.team.gatheringStatistic.items.get(StoneItem) == 0
        assert squad.team.gatheringStatistic.items.get(StickItem) == 2
    }

    void 'test items are picked up when unit moved on new cell'() {
        given:
        lockCore()
        def cell = worldSteps.getNextNeighborCell()
        cell.inventory.clearItems()
        def cell2 = worldSteps.getNextNeighborCell()
        cell.inventory.clearItems()
        cell2.inventory.addItem(new StickItem())
        def team = worldSteps.createHumanTeam()
        releaseCoreAndWaitNextCoreStep()

        and:
        def squad = worldSteps.createUnit(
                team,
                cell)
        assert squad.inventory.itemsIterator.size() == 0
        assert cell.inventory.itemsIterator.size() == 0

        when:
        squad.moveOn(cell2)

        then:
        assert cell2.inventory.itemsIterator.size() == 0
        assert squad.inventory.itemsIterator.size() == 1
        assert squad.inventory.getItem(StickItem)
        assert squad.team.gatheringStatistic.items.get(StickItem) == 1
    }

    void 'test items are picked up when new step started'() {
        given:
        lockCore()
        def cell = worldSteps.getNextNeighborCell()
        cell.inventory.clearItems()
        worldSteps.turnOffResourceGenerator(cell)
        def team = worldSteps.createHumanTeam()
        releaseCoreAndWaitNextCoreStep()

        and:
        def squad = worldSteps.createUnit(
                team,
                cell)
        cell.inventory.addItem(new StickItem())
        assert cell.inventory.itemsIterator.size() == 1
        assert squad.inventory.itemsIterator.size() == 0
        worldSteps.turnOffResourceGenerator(squad.lastCell)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        assert cell.inventory.itemsIterator.size() == 0
        assert squad.inventory.itemsIterator.size() >= 1
        assert squad.inventory.getItem(StickItem)

        def nodeVisible = Whitebox.getInternalState(cell.inventoryItemsIndicatorNode, "visible")
        assert !nodeVisible

        and:
        NewStepStartedEvent event = worldSteps.getEvent(NewStepStartedEvent, squad.team)
        assert event.getHint().contains(StickItem.getSimpleName() + " +1")

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then: 'is not displayed on 2nd turn'
        NewStepStartedEvent event2 = worldSteps.getEvent(NewStepStartedEvent, squad.team)
        assert !event2.getHint().contains(StickItem.getSimpleName())
    }
}
