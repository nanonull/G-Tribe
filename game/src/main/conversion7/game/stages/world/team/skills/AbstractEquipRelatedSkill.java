package conversion7.game.stages.world.team.skills;

import conversion7.game.stages.world.team.TeamSkillsManager;

public abstract class AbstractEquipRelatedSkill extends AbstractSkill {
    public AbstractEquipRelatedSkill(TeamSkillsManager skillsManager) {
        super(skillsManager);
    }

    @Override
    public void learn() {
        super.learn();
        if (currentLevel == 1) {
            skillsManager.getTeam().validateInventoriesAndEquipment();
        }
    }
}
