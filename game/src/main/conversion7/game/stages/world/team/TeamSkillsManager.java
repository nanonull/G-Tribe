package conversion7.game.stages.world.team;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.game.stages.world.team.skills.AbstractSkill;
import conversion7.game.stages.world.team.skills.SkillType;

public class TeamSkillsManager {

    public Team team;

    public ObjectMap<SkillType, AbstractSkill> skills = new ObjectMap<>();

    public TeamSkillsManager(Team team) {
        this.team = team;

        for (SkillType skillType : SkillType.values()) {
            try {
                AbstractSkill skill = skillType.skillClass.getDeclaredConstructor(TeamSkillsManager.class).newInstance(this);
                skills.put(skillType, skill);
                skill.setSkillType(skillType);
            } catch (Throwable e) {
                throw new GdxRuntimeException(e);
            }
        }
    }

    public Team getTeam() {
        return team;
    }

    public boolean hasEnoughPointsToLearn(AbstractSkill skill) {
        return team.getEvolutionPoints() >= skill.getLearnCost();
    }

    public AbstractSkill getSkill(SkillType skill) {
        return skills.get(skill);
    }

    public <C extends AbstractSkill> C getSkill(SkillType skill, Class<C> asClass) {
        return (C) skills.get(skill);
    }

    public boolean hasAllSkillLearnStarted(Array<SkillType> skillsReq) {
        for (SkillType type : skillsReq) {
            if (!getSkill(type).isLearnStarted()) {
                return false;
            }
        }
        return true;
    }
}
