package conversion7.game.stages.world.team.skills.items;

import conversion7.game.stages.world.team.TeamSkillsManager;
import conversion7.game.stages.world.team.skills.AbstractEquipRelatedSkill;
import conversion7.game.stages.world.unit.hero_classes.HeroClass;

public class FlayingSkill extends AbstractEquipRelatedSkill {

    public FlayingSkill(TeamSkillsManager teamSkillsManager) {
        super(teamSkillsManager);
    }

    @Override
    public void learn() {
        skillsManager.team.getAvaiableHeroClasses().add(HeroClass.SHADOW);
        super.learn();
    }
}
