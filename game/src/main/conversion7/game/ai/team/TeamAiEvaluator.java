package conversion7.game.ai.team;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.ai_new.base.AiEvaluator;
import conversion7.engine.ai_new.base.AiTask;
import conversion7.engine.utils.Utils;
import conversion7.game.ai.team.tasks.LearnSkillTask;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.goals.AbstractTribeGoal;
import conversion7.game.stages.world.team.skills.AbstractSkill;
import conversion7.game.stages.world.team.skills.SkillType;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.List;

public class TeamAiEvaluator extends AiEvaluator<Team> {
    public static TeamAiEvaluator instance = new TeamAiEvaluator();
    private static final Logger LOG = Utils.getLoggerForClass();
    protected Array<SkillType> skillTypesShuffled = new Array<>(SkillType.skillTypes);

    protected int getMaxIterations() {
        return 2;
    }

    protected boolean shouldRepeatTick(Team entity) {
        return !entity.defeated;
    }

    public void loop(Team entity) {
        for (int i = 0; i < getMaxIterations() && shouldRepeatTick(entity); i++) {
            entity.getAiTasks().clear();
            List<AiTask> tasks = findSortedTasks(entity);
            for (AiTask task : tasks) {
                try {
                    runTask(task);
                    break;
                } catch (Throwable e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    protected void evalEntityTasks(Team entity) {
        evalSkillsBranch(entity);
        evalTribeGoals(entity);
    }

    private void evalTribeGoals(Team team) {
        Iterator<AbstractTribeGoal> goalIterator = team.goals.iterator();
        while (goalIterator.hasNext()) {
            AbstractTribeGoal goal = goalIterator.next();
            if (goal.isValid()) {
                goal.execute(team);
            } else {
                goalIterator.remove();
            }
        }
    }

    private void evalSkillsBranch(Team team) {
        if (team.getEvolutionPoints() <= 0) {
            return;
        }

        skillTypesShuffled.shuffle();
        for (SkillType skillType : skillTypesShuffled) {
            AbstractSkill skill = team.getTeamSkillsManager().getSkill(skillType);
            if (LearnSkillTask.isApplicable(skill)) {
                LearnSkillTask learnSkillTask = new LearnSkillTask(team, skill);
                team.addAiTask(learnSkillTask);
                break;
            }
        }
    }

    public enum Strategy {
        TRIBE_GOAL_EACH_STEP, TRIBE_GOAL_EACH_ODD
    }
}
