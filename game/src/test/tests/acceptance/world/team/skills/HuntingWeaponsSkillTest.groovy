package tests.acceptance.world.team.skills

import conversion7.game.stages.world.inventory.items.ArrowItem
import conversion7.game.stages.world.inventory.items.weapons.AtlatlItem
import conversion7.game.stages.world.inventory.items.weapons.BowItem
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.skills.items.HuntingWeaponsSkill
import shared.BaseGdxgSpec

class HuntingWeaponsSkillTest extends BaseGdxgSpec {

    public void 'test HuntingWeaponsSkill_equipment, BowItem'() {
        when:
        Team humanTeam = worldSteps.createHumanTeam();
        def cell = worldSteps.getNextNeighborCell()
//        AbstractSquad humanSquad = WorldServices.createUnit(humanTeam
//                , cell
//                , SahelanthropusTchadensis);
        def unit = humanSquad.unit

        then: "unit could not use items"
        assert !unit.equipment.couldEquip(new BowItem())
        assert !unit.equipment.couldEquip(new ArrowItem())

        when:
        worldSteps.teamLearnsSkill(humanTeam, HuntingWeaponsSkill.class);

        then: "unit could use items"
        assert unit.equipment.couldEquip(new BowItem())
        assert unit.equipment.couldEquip(new ArrowItem())
    }

    public void 'test HuntingWeaponsSkill_equipment, AtlatlItem'() {
        given:
        Team humanTeam = worldSteps.createHumanTeam();
        def cell = worldSteps.getNextNeighborCell()
//        AbstractSquad humanSquad = WorldServices.createUnit(humanTeam, cell, SahelanthropusTchadensis);
        def unit = humanSquad.unit

        assert !unit.equipment.couldEquip(new AtlatlItem())

        when:
        worldSteps.teamLearnsSkill(humanTeam, HuntingWeaponsSkill.class);
        then:
        assert unit.equipment.couldEquip(new AtlatlItem())
    }
}
