package tests.acceptance.world.team.skills

import conversion7.game.stages.world.inventory.items.weapons.HammerItem
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.skills.SkillType
import conversion7.game.stages.world.team.skills.items.WeaponMasterySkill
import conversion7.game.stages.world.unit.Unit
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

class WeaponMasterySkillTest extends BaseGdxgSpec {
    public void test_WeaponMasterySkill_unitProducedParameters() {
        given:
        Team team1 = worldSteps.createHumanTeam();
        AbstractSquad army1 = worldSteps.createUnit(team1, worldSteps.getNextStandaloneCell());

        Unit unit = army1.unit;
        unit.getEquipment().equipMeleeWeaponItem(new HammerItem());

        int damageBeforeSkill = unit.getMeleeDamage();
        WeaponMasterySkill weaponMasterySkill = team1.getTeamSkillsManager().
                getSkill(SkillType.WEAPON_MASTERY, WeaponMasterySkill);
        weaponMasterySkill.learn();

        WorldAsserts.assertUnitHasDamageGreaterThan(unit, damageBeforeSkill);
    }


}
