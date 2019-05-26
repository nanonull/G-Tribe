package tests.acceptance.world.team.skills

import conversion7.game.stages.world.inventory.items.SkinRobeItem
import conversion7.game.stages.world.inventory.items.weapons.HammerItem
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.skills.SkillType
import conversion7.game.stages.world.team.skills.items.LocomotionSkill
import conversion7.game.stages.world.unit.Unit
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

class LocomotionSkillTest extends BaseGdxgSpec {
    public void test_LocomotionSkill_unitProducedParameters() {
        given:
        Team team1 = worldSteps.createHumanTeam();
        AbstractSquad army1 = worldSteps.createUnit(team1, worldSteps.getNextStandaloneCell());

        Unit unit = army1.unit;
        unit.getEquipment().equipMeleeWeaponItem(new HammerItem());
        unit.getEquipment().equipClothesItem(new SkinRobeItem());

        int damageBeforeSkill = unit.getMeleeDamage();
        int defenceBeforeSkill = unit.getDefence();
        LocomotionSkill locomotionSkill = team1.getTeamSkillsManager().getSkill(SkillType.LOCOMOTION, LocomotionSkill);
        locomotionSkill.learn();

        WorldAsserts.assertUnitHasDamageGreaterThan(unit, damageBeforeSkill);
        WorldAsserts.assertUnitHasDefenceGreaterThan(unit, defenceBeforeSkill);
    }


}
