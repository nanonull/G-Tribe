package conversion7.game.stages.world.team.skills.items;

import conversion7.game.stages.world.team.TeamSkillsManager;
import conversion7.game.stages.world.team.skills.AbstractSkill;

public class WeaponMasterySkill extends AbstractSkill {

    public static int ADD_DMG = 1;
    public static int MAX_LEVEL = 3;

    public WeaponMasterySkill(TeamSkillsManager teamSkillsManager) {
        super(teamSkillsManager);
    }

    public int getDmgAdd() {
        return ADD_DMG * currentLevel;
    }
}
