package tests.acceptance.ai.team

import com.badlogic.gdx.utils.Array
import conversion7.game.ai.team.TeamAiEvaluator
import conversion7.game.stages.world.team.skills.SkillType

class AiLearnSkillTestEvaluator extends TeamAiEvaluator {

    public static SkillType TARGET_TEST_SKILL_TYPE = SkillType.HANDS_AS_A_TOOL

    AiLearnSkillTestEvaluator() {
        skillTypesShuffled = new Array<>()
        skillTypesShuffled.add(TARGET_TEST_SKILL_TYPE)
    }
}
