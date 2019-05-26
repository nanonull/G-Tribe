package tests.acceptance.world.unit.actions

import conversion7.game.stages.world.objects.actions.items.FireAction
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.skills.items.FireSkill
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

public class FireActionTest extends BaseGdxgSpec {

    public void 'test FireSkillShouldAppearInExistingObject'() {
        when:
        def humanSquad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());

        then:
        WorldAsserts.assertAreaObjectHasNoAction(humanSquad, FireAction.class);

        when:
        worldSteps.teamLearnsSkill(humanSquad.getTeam(), FireSkill.class);

        then:
        WorldAsserts.assertAreaObjectHasAction(humanSquad, FireAction.class);
    }

    public void test_FireSkillShouldAppearInNewObject() {
        given:

        Team humanTeam = worldSteps.createHumanTeam();
        worldSteps.teamLearnsSkill(humanTeam, FireSkill.class);

        when:
        def humanSquad = worldSteps.createUnit(
                humanTeam,
                worldSteps.getNextStandaloneCell());

        then:
        WorldAsserts.assertAreaObjectHasAction(humanSquad, FireAction.class);
    }

}
