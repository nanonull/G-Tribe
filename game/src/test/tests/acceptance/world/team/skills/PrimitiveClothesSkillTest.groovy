package tests.acceptance.world.team.skills

import conversion7.game.stages.world.inventory.items.SkinRobeItem
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.skills.items.PrimitiveClothingSkill
import shared.BaseGdxgSpec

class PrimitiveClothesSkillTest extends BaseGdxgSpec {

    public void test_PrimitiveClothesSkill_equipment() {
        when:
        Team humanTeam = worldSteps.createHumanTeam();
        def cell = worldSteps.getNextNeighborCell()
//        AbstractSquad humanSquad = WorldServices.createUnit(humanTeam, cell, SahelanthropusTchadensis);
        def unit = humanSquad.unit
        then:
        assert !unit.equipment.couldEquip(new SkinRobeItem())

        when:
        worldSteps.teamLearnsSkill(humanTeam, PrimitiveClothingSkill.class);
        then:
        assert unit.equipment.couldEquip(new SkinRobeItem())
    }


}
