package conversion7.game.stages.world.team.skills.items;

import conversion7.game.stages.world.team.TeamSkillsManager;
import conversion7.game.stages.world.team.skills.AbstractSkill;
import conversion7.game.stages.world.unit.effects.items.ConcentrationEffect;

public class LocomotionSkill extends AbstractSkill {

    public static int DMG_PER_LVL = 1;
    public static int MAX_LEVEL = 3;
    public static final int DODGE_PERC_PER_LVL = ConcentrationEffect.DODGE_PERC / 2 / MAX_LEVEL;

    public LocomotionSkill(TeamSkillsManager skillsManager) {
        super(skillsManager);
    }

    public int getDmgAdd() {
        return DMG_PER_LVL * currentLevel;
    }
}
