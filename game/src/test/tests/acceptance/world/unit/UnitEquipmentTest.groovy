package tests.acceptance.world.unit

import com.badlogic.gdx.utils.Array
import conversion7.game.stages.world.inventory.items.ArrowItem
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem
import conversion7.game.stages.world.inventory.items.weapons.StickItem
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.team.Team
import shared.BaseGdxgSpec

class UnitEquipmentTest extends BaseGdxgSpec {

    public void 'test equip single-qty item (melee)'() {
        when:
        Team team = worldSteps.createHumanTeam();
        def cell = worldSteps.getNextNeighborCell()
        AbstractSquad squad = worldSteps.createUnit(team, cell);
        def item = new StickItem()
        squad.inventory.addItem(item)
        squad.unit.equipment.equip(squad.inventory, item)

        and: "prep for assert"
        def items = new Array<AbstractInventoryItem>()
        squad.inventory.getItems(items)
        def actItem = squad.inventory.getItem(item.class)

        then: "item removed from inventory"
        assert items.size == 0
        assert actItem == null

        when: "prep for assert"
        def actEquipItem = squad.unit.equipment.meleeWeaponItem

        then: "item equipped"
        assert actEquipItem
        assert actEquipItem.class == StickItem
        assert actEquipItem.quantity == 1
    }

    public void 'test equip more arrows'() {
        when:
        Team team = worldSteps.createHumanTeam();
        def cell = worldSteps.getNextNeighborCell()
//        AbstractSquad squad = WorldServices.createUnit(team, cell, SahelanthropusTchadensis);
        squad.getInventory.clearItems()

        and: "equip 1st arrow"
        def item = new ArrowItem()
        squad.getInventory.addItem(item)
        squad.unit.equipment.equip(squad.getInventory, item)

        then:
        assert squad.unit.equipment.rangeBulletsItem
        assert squad.unit.equipment.rangeBulletsItem.quantity == 1

        when: "equip 2nd arrow"
        item = new ArrowItem()
        squad.getInventory.addItem(item)
        squad.unit.equipment.equip(squad.getInventory, item)

        then:
        assert squad.unit.equipment.rangeBulletsItem
        assert squad.unit.equipment.rangeBulletsItem.quantity == 2

        when: "check inventory"
        def items = new Array<AbstractInventoryItem>()
        squad.getInventory.getItems(items)
        def actItem = squad.getInventory.getItem(item.class)

        then: "item removed from inventory"
        assert items.size == 0
        assert actItem == null

    }

    def 'test equip all arrows from inventory'() {
        when:
        Team team = worldSteps.createHumanTeam();
        def cell = worldSteps.getNextNeighborCell()
//        AbstractSquad squad = WorldServices.createUnit(team, cell, SahelanthropusTchadensis);

        and: "equip arrows"
        def item = new ArrowItem()
        item.quantity = 2
        squad.getInventory.addItem(item)
        squad.unit.equipment.equip(squad.getInventory, item)

        then:
        assert squad.getInventory.getItem(ArrowItem) == null
        assert squad.unit.equipment.rangeBulletsItem
        assert squad.unit.equipment.rangeBulletsItem.quantity == 2
    }

}
