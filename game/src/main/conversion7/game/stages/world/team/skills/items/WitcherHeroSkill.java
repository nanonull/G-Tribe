package conversion7.game.stages.world.team.skills.items;

import conversion7.game.stages.world.team.TeamSkillsManager;
import conversion7.game.stages.world.team.skills.AbstractSkill;
import conversion7.game.stages.world.unit.hero_classes.HeroClass;

public class WitcherHeroSkill extends AbstractSkill {

    public WitcherHeroSkill(TeamSkillsManager skillsManager) {
        super(skillsManager);
    }

    @Override
    public void learn() {
        skillsManager.team.getAvaiableHeroClasses().add(HeroClass.WITCH);
        super.learn();
    }
}
