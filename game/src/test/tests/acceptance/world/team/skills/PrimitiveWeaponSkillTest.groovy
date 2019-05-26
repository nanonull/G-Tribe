package tests.acceptance.world.team.skills

import conversion7.game.stages.world.inventory.items.weapons.HammerItem
import conversion7.game.stages.world.inventory.items.weapons.JavelinItem
import conversion7.game.stages.world.inventory.items.weapons.SpearItem
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.skills.items.PrimitiveWeaponsSkill
import shared.BaseGdxgSpec

class PrimitiveWeaponSkillTest extends BaseGdxgSpec {
    public void test_PrimitiveWeaponSkill_equipment() {
        when:
        Team humanTeam = worldSteps.createHumanTeam();
        def cell = worldSteps.getNextNeighborCell()
//        AbstractSquad humanSquad = WorldServices.createUnit(humanTeam, cell, SahelanthropusTchadensis);
        def unit = humanSquad.unit

        then:
        assert !unit.equipment.couldEquip(itemClass.newInstance())

        when:
        worldSteps.teamLearnsSkill(humanTeam, PrimitiveWeaponsSkill.class);
        then:
        assert unit.equipment.couldEquip(itemClass.newInstance())

        where:
        itemClass << [HammerItem, SpearItem, JavelinItem]
    }


}
