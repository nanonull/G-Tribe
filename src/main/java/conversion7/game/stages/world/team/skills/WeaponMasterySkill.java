package conversion7.game.stages.world.team.skills;

import conversion7.game.stages.world.team.TeamSkillsManager;

import static java.lang.String.format;

public class WeaponMasterySkill extends AbstractSkill {

    /** +% to DAMAGE */
    public static final int[] effectPerLevel = new int[]{0, 10, 20, 30};

    public WeaponMasterySkill(TeamSkillsManager teamSkillsManager) {
        super(teamSkillsManager);
    }

    @Override
    public String getDescription(int level) {
        return format(super.getDescription(level), effectPerLevel[level]);
    }

    public float getEffectMultiplier() {
        if (currentLevel == 0) {
            return 1;
        } else {
            return 1 + effectPerLevel[currentLevel] / 100f;
        }
    }
}
