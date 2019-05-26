package conversion7.game.stages.world.team.skills;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.interfaces.HintProvider;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.TeamSkillsManager;
import conversion7.game.ui.UiLogger;
import org.slf4j.Logger;

import static java.lang.String.format;

public abstract class AbstractSkill implements HintProvider {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static final String HAS_NOT_BEEN_LEARNED_YET = format("Has not been learned yet");
    private SkillType skillType;

    public TeamSkillsManager skillsManager;
    protected int currentLevel;

    public AbstractSkill(TeamSkillsManager skillsManager) {
        this.skillsManager = skillsManager;
    }


    public String getName() {
        return skillType.name;
    }

    public boolean isPassive() {
        return skillType.passive;
    }

    public int getLevels() {
        return skillType.levels;
    }

    public boolean isMultiLevel() {
        return skillType.isMultiLevel();
    }

    public Array<AbstractSkill> getParentSkills() {
        Array<AbstractSkill> skills = new Array<>();
        for (SkillType parentSkillType : skillType.parentSkillTypes) {
            skills.add(skillsManager.skills.get(parentSkillType));
        }
        return skills;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public boolean isFullyLearned() {
        return currentLevel == getLevels();
    }

    /** For multi-level */
    public boolean isPartiallyLearned() {
        return isLearnStarted() && !isFullyLearned();
    }

    @Deprecated
    public boolean isNotStartedLearn() {
        return !isLearnStarted();
    }

    public boolean isLearnStarted() {
        return currentLevel > 0;
    }

    @Override
    public String getHint() {
        StringBuilder hint = new StringBuilder(getName()).append("\n")
                .append(isPassive() ? "-Passive skill-" : "-Active skill-")
                .append("\n\n");

        if (isFullyLearned()) {
            hint.append(getDescription(currentLevel));
        } else if (isPartiallyLearned()) {
            hint.append("Level: ").append(currentLevel)
                    .append("\n")
                    .append(getDescription(currentLevel))
                    .append("\n")
                    .append("Next level: ")
                    .append("\n")
                    .append(getDescription(currentLevel + 1))
                    .append("\n\n---\n")
                    .append("Learn cost: ").append(getLearnCost()).append(" evolution point(s)");
        } else {
            // not learned yet:
            if (isMultiLevel()) {
                hint.append("Level 1:\n");
            }
            hint.append(getDescription(1));
            hint.append("\n\n---\n");
            hint.append(HAS_NOT_BEEN_LEARNED_YET);
            hint.append("\n");
            hint.append("Learn cost: ").append(getLearnCost()).append(" evolution point(s)");

            boolean skillReq = false;
            Array<AbstractSkill> parentSkills = getParentSkills();
            StringBuilder skillReqBuilder = null;
            if (parentSkills.size > 0) {
                skillReqBuilder = new StringBuilder("\n\nSkill(s) required: ");
                for (AbstractSkill parentSkill : parentSkills) {
                    if (parentSkill.isNotStartedLearn()) {
                        skillReqBuilder.append("\n").append(parentSkill.getName());
                        skillReq = true;
                    }
                }
            }
            PoolManager.ARRAYS_POOL.free(parentSkills);

            if (skillReq) {
                hint.append(skillReqBuilder.toString());
            }
        }
        return hint.toString();
    }

    public boolean isAvailableForLearn() {
        if (isFullyLearned()) {
            return false;
        }

        if (!skillsManager.hasEnoughPointsToLearn(this)) {
            return false;
        }

        boolean noParentNotLearnedSkills = true;
        Array<AbstractSkill> parentSkills = getParentSkills();
        if (parentSkills != null) {
            for (AbstractSkill parentSkill : parentSkills) {
                if (parentSkill.isNotStartedLearn()) {
                    noParentNotLearnedSkills = false;
                    break;
                }
            }
        }
        PoolManager.ARRAYS_POOL.free(parentSkills);

        return noParentNotLearnedSkills;
    }

    public int getLearnCost() {
        return currentLevel + 1;
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
    }

    public String getDescription(int level) {
        return skillType.description;
    }

    public void learn() {
        Team team = skillsManager.getTeam();
        team.updateEvolutionPointsOn(-getLearnCost(), null);
        currentLevel++;
        team.validateObjectActions();
        if (team.isHumanPlayer()) {
            UiLogger.addInfoLabel("Learn " + getClass().getSimpleName());
        }
        LOG.info("{} learns {}", team, this);
    }
}
