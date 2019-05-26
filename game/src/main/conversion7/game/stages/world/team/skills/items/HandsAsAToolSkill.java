package conversion7.game.stages.world.team.skills.items;

import conversion7.game.stages.world.team.TeamSkillsManager;
import conversion7.game.stages.world.team.skills.AbstractSkill;

import static java.lang.String.format;

public class HandsAsAToolSkill extends AbstractSkill {

    /** +% to DAMAGE */
    public static final int[] effectPerLevel = new int[]{0, 10};

    public HandsAsAToolSkill(TeamSkillsManager skillsManager) {
        super(skillsManager);
    }

    public float getEffectMultiplier() {
        if (currentLevel == 0) {
            return 1;
        } else {
            return 1 + effectPerLevel[currentLevel] / 100f;
        }
    }

    @Override
    public String getDescription(int level) {
        return format(super.getDescription(level), effectPerLevel[level]);
    }
}
