package conversion7.game.stages.world.team.skills;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.interfaces.HintProvider;
import conversion7.game.stages.world.team.TeamSkillsManager;
import conversion7.game.stages.world.team.skills.statics.SkillStaticParams;
import org.slf4j.Logger;

import static java.lang.String.format;

public abstract class AbstractSkill implements HintProvider {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static final String HAS_NOT_BEEN_LEARNED_YET = format("Has not been learned yet");
    private final SkillStaticParams skillStaticParams;

    protected TeamSkillsManager skillsManager;
    protected int currentLevel;

    public AbstractSkill(TeamSkillsManager skillsManager) {
        this.skillsManager = skillsManager;
        skillStaticParams = SkillStaticParams.SKILL_STATIC_PARAMS.get(getClass());
        if (skillStaticParams == null) {
            Utils.error("skillStaticParams == null for class: " + getClass());
        }
    }

    public SkillStaticParams getSkillStaticParams() {
        return skillStaticParams;
    }

    public String getName() {
        return skillStaticParams.getName();
    }

    public String getDescription(int level) {
        return skillStaticParams.getDescription();
    }

    public boolean isPassive() {
        return skillStaticParams.isPassive();
    }

    public int getLevels() {
        return skillStaticParams.getLevels();
    }

    public boolean isMultiLevel() {
        return skillStaticParams.isMultiLevel();
    }

    public Array<AbstractSkill> getParentSkills() {
        Array<AbstractSkill> skills = PoolManager.ARRAYS_POOL.obtain();
        for (SkillStaticParams staticParams : skillStaticParams.getParentSkillParams()) {
            skills.add(skillsManager.getSkill(staticParams.getSkillClass()));
        }
        return skills;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public boolean isLearned() {
        return currentLevel == getLevels();
    }

    /** For multi-level */
    public boolean isPartiallyLearned() {
        return isLearnStarted() && !isLearned();
    }

    public boolean isNotLearnStarted() {
        return currentLevel == 0;
    }

    public boolean isLearnStarted() {
        return currentLevel > 0;
    }

    @Override
    public String getHint() {
        StringBuilder hint = new StringBuilder(getName()).append("\n")
                .append(isPassive() ? "-Passive skill-" : "-Active skill-")
                .append("\n\n");

        if (isLearned()) {
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
            if (parentSkills != null) {
                skillReqBuilder = new StringBuilder("\n\nSkill(s) required: ");
                for (AbstractSkill parentSkill : parentSkills) {
                    if (parentSkill.isNotLearnStarted()) {
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
        if (isLearned()) {
            return false;
        }

        if (!skillsManager.hasEnoughPointsToLearn(this)) {
            return false;
        }

        boolean noParentNotLearnedSkills = true;
        Array<AbstractSkill> parentSkills = getParentSkills();
        if (parentSkills != null) {
            for (AbstractSkill parentSkill : parentSkills) {
                if (parentSkill.isNotLearnStarted()) {
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

    public void learn() {
        LOG.info("learn " + getClass().getSimpleName());
        skillsManager.getTeam().updateEvolutionPointsOn(-getLearnCost());
        currentLevel++;
        skillsManager.getTeam().validateObjectActions();
    }
}
