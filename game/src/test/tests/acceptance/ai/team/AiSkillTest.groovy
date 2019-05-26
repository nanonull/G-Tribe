package tests.acceptance.ai.team

import conversion7.game.GdxgConstants
import conversion7.game.stages.world.team.Team
import shared.BaseGdxgSpec

class AiSkillTest extends BaseGdxgSpec {

    @Override
    def setup() {
        GdxgConstants.AREA_OBJECT_AI = true
    }

    void 'test learn skill'() {
        given:
        lockCore()
        worldSteps.createHumanTeam()

        def cell1 = worldSteps.nextNeighborCell
        def squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(false),
                cell1);
        Team team = squad.getTeam();
        worldSteps.makeUnitInvincible(squad.unit)

        team.updateEvolutionPointsOn(10, null)
        team.tribeAiEvaluator = new AiLearnSkillTestEvaluator()
        releaseCoreAndWaitNextCoreStep()

        assert team.teamSkillsManager.getSkill(AiLearnSkillTestEvaluator.TARGET_TEST_SKILL_TYPE).isAvailableForLearn()

        when: "1st turn"
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        assert !team.teamSkillsManager.getSkill(AiLearnSkillTestEvaluator.TARGET_TEST_SKILL_TYPE).isAvailableForLearn()
        assert team.teamSkillsManager.getSkill(AiLearnSkillTestEvaluator.TARGET_TEST_SKILL_TYPE).isFullyLearned()
    }

}
