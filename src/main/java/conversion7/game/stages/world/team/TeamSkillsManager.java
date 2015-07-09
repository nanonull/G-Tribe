package conversion7.game.stages.world.team;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.pools.system.PoolManager;
import conversion7.game.stages.world.team.skills.AbstractSkill;
import conversion7.game.stages.world.team.skills.FireSkill;
import conversion7.game.stages.world.team.skills.FlayingSkill;
import conversion7.game.stages.world.team.skills.HandsAsAToolSkill;
import conversion7.game.stages.world.team.skills.HoldWeaponSkill;
import conversion7.game.stages.world.team.skills.HuntingWeaponsSkill;
import conversion7.game.stages.world.team.skills.LocomotionSkill;
import conversion7.game.stages.world.team.skills.PrimitiveClothingSkill;
import conversion7.game.stages.world.team.skills.PrimitiveWeaponsSkill;
import conversion7.game.stages.world.team.skills.StoneWorkSkill;
import conversion7.game.stages.world.team.skills.TameBeastSkill;
import conversion7.game.stages.world.team.skills.TotemsSkill;
import conversion7.game.stages.world.team.skills.WeaponMasterySkill;

public class TeamSkillsManager {

    private Team team;


    private HandsAsAToolSkill handsAsAToolSkill;
    private StoneWorkSkill stoneWorkSkill;
    private HoldWeaponSkill holdWeaponSkill;
    private FlayingSkill flayingSkill;
    private PrimitiveWeaponsSkill primitiveWeaponsSkill;
    private WeaponMasterySkill weaponMasterySkill;
    private PrimitiveClothingSkill primitiveClothingSkill;
    private HuntingWeaponsSkill huntingWeaponsSkill;

    private LocomotionSkill locomotionSkill;

    private FireSkill fireSkill;
    private TameBeastSkill tameBeastSkill;

    private TotemsSkill totemsSkill;

    Array<AbstractSkill> skills = PoolManager.ARRAYS_POOL.obtain();

    public TeamSkillsManager(Team team) {
        this.team = team;

        handsAsAToolSkill = new HandsAsAToolSkill(this);
        skills.add(handsAsAToolSkill);

        stoneWorkSkill = new StoneWorkSkill(this);
        skills.add(stoneWorkSkill);
        holdWeaponSkill = new HoldWeaponSkill(this);
        skills.add(holdWeaponSkill);

        flayingSkill = new FlayingSkill(this);
        skills.add(flayingSkill);
        primitiveWeaponsSkill = new PrimitiveWeaponsSkill(this);
        skills.add(primitiveWeaponsSkill);
        weaponMasterySkill = new WeaponMasterySkill(this);
        skills.add(weaponMasterySkill);

        primitiveClothingSkill = new PrimitiveClothingSkill(this);
        skills.add(primitiveClothingSkill);
        huntingWeaponsSkill = new HuntingWeaponsSkill(this);
        skills.add(huntingWeaponsSkill);

        // new branch
        locomotionSkill = new LocomotionSkill(this);
        skills.add(locomotionSkill);

        fireSkill = new FireSkill(this);
        skills.add(fireSkill);
        tameBeastSkill = new TameBeastSkill(this);
        skills.add(tameBeastSkill);

        totemsSkill = new TotemsSkill(this);
        skills.add(totemsSkill);

    }

    public HandsAsAToolSkill getHandsAsAToolSkill() {
        return handsAsAToolSkill;
    }

    public HoldWeaponSkill getHoldWeaponSkill() {
        return holdWeaponSkill;
    }

    public FlayingSkill getFlayingSkill() {
        return flayingSkill;
    }

    public WeaponMasterySkill getWeaponMasterySkill() {
        return weaponMasterySkill;
    }

    public PrimitiveClothingSkill getPrimitiveClothingSkill() {
        return primitiveClothingSkill;
    }

    public PrimitiveWeaponsSkill getPrimitiveWeaponsSkill() {
        return primitiveWeaponsSkill;
    }

    public HuntingWeaponsSkill getHuntingWeaponsSkill() {
        return huntingWeaponsSkill;
    }

    public LocomotionSkill getLocomotionSkill() {
        return locomotionSkill;
    }

    public FireSkill getFireSkill() {
        return fireSkill;
    }

    public TameBeastSkill getTameBeastSkill() {
        return tameBeastSkill;
    }

    public TotemsSkill getTotemsSkill() {
        return totemsSkill;
    }

    public Team getTeam() {
        return team;
    }

    public StoneWorkSkill getStoneWorkSkill() {
        return stoneWorkSkill;
    }

    public boolean hasEnoughPointsToLearn(AbstractSkill skill) {
        return team.getEvolutionPoints() >= skill.getLearnCost();
    }

    public AbstractSkill getSkill(Class<? extends AbstractSkill> skillClass) {
        for (AbstractSkill skill : skills) {
            if (skillClass.equals(skill.getClass())) {
                return skill;
            }
        }
        throw new GdxRuntimeException("No skill with class: " + skillClass);
    }
}
