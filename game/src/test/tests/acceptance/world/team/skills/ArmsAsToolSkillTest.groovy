package tests.acceptance.world.team.skills

import conversion7.game.stages.world.inventory.items.StoneItem
import conversion7.game.stages.world.inventory.items.weapons.StickItem
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.skills.items.ArmsAsToolSkill
import conversion7.game.stages.world.unit.Unit
import shared.BaseGdxgSpec

class ArmsAsToolSkillTest extends BaseGdxgSpec {

    public void 'test HoldWeaponSkill equip'() {
        given:
        Team humanTeam = worldSteps.createHumanTeam();
        def humanSquad = worldSteps.createUnit(
                humanTeam,
                worldSteps.getNextNeighborCell());
        Unit unit1 = humanSquad.unit

        assert !unit1.equipment.couldEquip(new StickItem())
        assert !unit1.equipment.couldEquip(new StoneItem())

        when:
        worldSteps.teamLearnsSkill(humanTeam, ArmsAsToolSkill.class);
        then:
        assert unit1.equipment.couldEquip(new StickItem())
        assert unit1.equipment.couldEquip(new StoneItem())
    }


}
