package conversion7.game.ai.team.tasks;

import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.skills.AbstractSkill;

// use tasks only for limited actions to find and execute best priority action only
// tribe can learn all skill while have EP and there are no other actions which consumes EP
// so this task is extra
@Deprecated
public class LearnSkillTask extends AbstractTeamTask {

    private AbstractSkill skill;

    public LearnSkillTask(Team owner, AbstractSkill skill) {
        super(owner);
        this.skill = skill;
    }

    public static boolean isApplicable(AbstractSkill skill) {
        return skill.isAvailableForLearn();
    }

    @Override
    public boolean isValid() {
        return isApplicable(skill);
    }

    @Override
    public void run() {
        skill.learn();
    }
}
