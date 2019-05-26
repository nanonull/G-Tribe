package tests.acceptance.world.team.skills

import conversion7.game.stages.world.inventory.items.weapons.HammerItem
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.skills.SkillType
import conversion7.game.stages.world.team.skills.items.HandsAsAToolSkill
import conversion7.game.stages.world.unit.Unit
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

class HandsAsAToolSkillTest extends BaseGdxgSpec {

    public void test_HandsAsAToolSkill_unitProducedParameters() {
        given:
        Team team1 = worldSteps.createHumanTeam();
        AbstractSquad army1 = worldSteps.createUnit(team1, worldSteps.getNextStandaloneCell());

        Unit unit = army1.unit;
        // to make sure modificator percent will add > 1 damage
        unit.getEquipment().equipMeleeWeaponItem(new HammerItem());

        int damageBeforeSkill = unit.getMeleeDamage();
        HandsAsAToolSkill handsAsAToolSkill = team1.getTeamSkillsManager().
                getSkill(SkillType.HANDS_AS_A_TOOL, HandsAsAToolSkill);
        handsAsAToolSkill.learn();

        WorldAsserts.assertUnitHasDamageGreaterThan(unit, damageBeforeSkill);
    }


}
