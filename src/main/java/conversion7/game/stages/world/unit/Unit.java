package conversion7.game.stages.world.unit;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.world.Climate;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.effects.IncreaseBattleParametersEffect;
import conversion7.game.stages.world.unit.effects.UnitEffectManager;
import conversion7.game.stages.world.unit.effects.items.Childbearing;
import conversion7.game.ui.UiLogger;
import conversion7.game.ui.utils.UiUtils;
import org.fest.assertions.api.Assertions;
import org.slf4j.Logger;

import java.util.Iterator;

/**
 * Base class for all units
 */

// TODO validate/invalidate modificators when: add/remove effect from army, move from army, add skill to team
// and better if areaobject will just send modificator value to units
public abstract class Unit {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final int FOOD_LIMIT = 10;
    public static final int WATER_LIMIT = FOOD_LIMIT / 3;

    public static final int HEALTHY_TEMPERATURE_MIN = 10;
    public static final int EAT_FOOD_QUANTITY = 1;

    private UnitParameters params = new UnitParameters();
    private UnitEffectManager effectManager = new UnitEffectManager(this);
    private UnitEquipment equipment = new UnitEquipment(this);
    private UnitSpecialization specialization;

    private boolean alive = true;
    private boolean gender;
    private byte level;
    private String name;
    public int id;

    private Unit mother;
    private AreaObject areaObject;

    private int temperature;
    private int food;
    private int water;
    private int actionPoints;

    private float battleParametersEffectMultiplier;
    private float locomotionSkillMultiplier;
    private float handsAsToolSkillMultiplier;
    private float weaponMasterySkillMultiplier;
    private AreaObject previousAreaObject;

    @Deprecated
    public Unit create(boolean gender) {
        return create(gender, UnitSpecialization.getRandom());
    }

    public Unit create(boolean gender, UnitSpecialization specialization) {
        this.id = Utils.getNextId();
        this.specialization = specialization;
        this.gender = gender;
        this.name = this.getClass().getSimpleName() + id;

        temperature = Climate.TEMPERATURE_MAX;
        food = FOOD_LIMIT;
        water = WATER_LIMIT;

        resetActionPoints();

        return this;
    }

    public void setSpecialization(UnitSpecialization specialization) {
        this.specialization = specialization;
        if (specialization.equals(UnitSpecialization.MELEE)) {
            equipment.dropRangeWeapon();
            equipment.dropRangeBullets();
        }
        areaObject.getReadyRangeUnitsController().invalidate();
        areaObject.getMilitaryInventory().invalidate();
        areaObject.validate();
    }

    public UnitSpecialization getSpecialization() {
        return specialization;
    }

    public boolean isAlive() {
        return alive;
    }

    public UnitParameters getParams() {
        return params;
    }

    public UnitEffectManager getEffectManager() {
        return effectManager;
    }

    public boolean getGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public byte getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public Unit getMother() {
        return mother;
    }

    public AreaObject getAreaObject() {
        return areaObject;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getFood() {
        return food;
    }

    public int getWater() {
        return water;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public void setMother(Unit mother) {
        this.mother = mother;
    }

    public UnitEquipment getEquipment() {
        return equipment;
    }

    public int getStrength() {
        return effectManager.getStrength();
    }

    public int getAgility() {
        return effectManager.getAgility();
    }

    public int getVitality() {
        return effectManager.getVitality();
    }

    public int getMaxHealth() {
        return effectManager.getVitality();
    }

    public int getHealthDamagedByEffects() {
        return params.getHealth() - effectManager.getEffectsDamage();
    }

    private void updateBattleParametersEffectMultiplier() {
        if (areaObject.hasEffect(IncreaseBattleParametersEffect.class)) {
            battleParametersEffectMultiplier = 1.2f;
        } else {
            battleParametersEffectMultiplier = 1f;
        }
    }

    private void updateLocomotionMultiplier() {
        locomotionSkillMultiplier = areaObject.getTeam().getTeamSkillsManager().getLocomotionSkill().getEffectMultiplier();
    }

    private void updateHandsAsToolSkillMultiplier() {
        handsAsToolSkillMultiplier = areaObject.getTeam().getTeamSkillsManager().getHandsAsAToolSkill().getEffectMultiplier();
    }

    private void updateWeaponMasterySkillMultiplier() {
        weaponMasterySkillMultiplier = areaObject.getTeam().getTeamSkillsManager().getWeaponMasterySkill().getEffectMultiplier();
    }

    public int getMeleeDamage() {
        float damageMultipliers = updateDamageMultiplier();
        return Math.round(
                equipment.getMeleeDamage()
                        * (getStrength() / 4f)
                        * damageMultipliers);
    }

    public boolean isRangeAttackPossible() {
        return specialization.equals(Unit.UnitSpecialization.RANGE) && equipment.getRangeBulletsItem() != null;
    }

    public int getRangeDamage() {
        float damageMultipliers = updateDamageMultiplier();
        return Math.round(
                equipment.getRangeDamage()
                        * ((getAgility() + getStrength()) / 8f)
                        * damageMultipliers);
    }

    public int getRangeAttackChance() {
        return equipment.getRangeAttackChance();
    }

    private float updateDamageMultiplier() {
        updateBattleParametersEffectMultiplier();
        updateLocomotionMultiplier();
        updateHandsAsToolSkillMultiplier();
        updateWeaponMasterySkillMultiplier();
        return battleParametersEffectMultiplier * locomotionSkillMultiplier
                * handsAsToolSkillMultiplier * weaponMasterySkillMultiplier;
    }

    public int getDefence() {
        updateBattleParametersEffectMultiplier();
        updateLocomotionMultiplier();
        return Math.round(
                (getAgility() / 2f)
                        * battleParametersEffectMultiplier * locomotionSkillMultiplier);
    }

    public int getArmor() {
        updateBattleParametersEffectMultiplier();
        return Math.round(
                (equipment.getArmor() +
                        (getVitality() / 7f)
                )
                        * battleParametersEffectMultiplier);
    }

    public Unit assignParams(UnitParameters p) {
        this.params = p;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()).append(" ")
                .append("id = ").append(id).append("; ")
                .append("male? = ").append(gender).append("; ")
                .append("areaObject.id = ").append(areaObject != null ? areaObject.getId() : null).append("; ")
                .append(params.toString());
        return sb.toString();
    }

    public String getTemperatureString() {
        return UiUtils.getTemperatureString(temperature);
    }

    public void assignToAreaObject(AreaObject object) {
        this.areaObject = object;
    }

    public TextureRegion getClassIcon() {
        TextureRegion textureRegion = Assets.CLASS_ICONS.get(this.getClass());
        Assertions.assertThat(textureRegion).isNotNull().as("icon was not defined for class: " + this.getClass());
        return textureRegion;
    }

    public void birth() {
        LOG.debug("birth. mother: {}", mother);
        World.validateNewUnitBirth(this);
        mother.areaObject.getUnitsController().addUnit(this);
        temperature = (areaObject.getCell().getTemperature() + mother.temperature) / 2;
        mother.areaObject.validate();
        LOG.debug("born: {}", this);
    }

    public void heal() {
        if (LOG.isDebugEnabled()) LOG.debug("heal: " + this);
        if (params.getHealth() < getMaxHealth()) {
            params.updateHealth(+1);
        }
    }

    public void hit(Unit targetUnit, int damage) {
        LOG.info(String.format("%s hits [%d damage]: %s", this, damage, targetUnit));
        int damageMinusArmor = damage - targetUnit.getArmor();
        UiLogger.addInfoLabel("Successful damage: " + damageMinusArmor);
        if (damageMinusArmor > 0) {
            boolean killed = targetUnit.hurt(damageMinusArmor);
            if (killed) {
                targetUnit.diesInBattle();
            }
        } else {
            LOG.info("Damage was absorbed.");
        }
    }

    /** Returns true if killed */
    public boolean hurt(int damage) {
        params.updateHealth(-damage);
        return params.getHealth() <= 0;
    }

    public void killedByTeammates() {
        areaObject.getUnitsController().removeAndValidate(this);
        onDeath();
        areaObject.updateMoral(-1);
    }

    public void dies(Iterator<Unit> unitIterator) {
        unitIterator.remove();
        areaObject.getUnitsController().invalidate();
        areaObject.getUnitsController().afterUnitRemoved(this);
        onDeath();
    }

    public void diesInBattle() {
        areaObject.getUnitsController().remove(this);
        onDeath();
    }

    private void onDeath() {
        LOG.info("World unit dies: " + this);
        alive = false;
        equipment.drop();
    }

    public boolean isPregnant() {
        return effectManager.getEffect(Childbearing.class) != null;
    }

    public float getCalculatedPower() {
        return (params.getStrength() + params.getAgility() + params.getVitality()) * params.getHealth();
    }

    public float getFoodPriority() {
        return getCalculatedPower() * (isPregnant() ? 5 : 1);
    }

    public float getEquipPriority() {
        return getCalculatedPower() * (specialization == UnitSpecialization.MELEE ? 2 : 1);
    }

    /** Will temperature increase, decrease or stay? */
    public int getTemperatureStepBalance() {
        int temperatureAround = areaObject.getCell().getTemperature() + equipment.getHeat();
        if (this.temperature < temperatureAround) {
            return 1;
        } else if (this.temperature > temperatureAround) {
            return -1;
        } else {
            return 0;
        }
    }

    /** Assume units were sorted */
    public boolean willEat() {
        return areaObject.getCell().getFood() > areaObject.getUnits().indexOf(this, false);
    }

    /** Assume units were sorted */
    public boolean willDrink() {
        return areaObject.getCell().getWater() > areaObject.getUnits().indexOf(this, false);
    }

    public boolean hasHealthyTemperature() {
        return temperature + equipment.getHeat() >= Unit.HEALTHY_TEMPERATURE_MIN;
    }

    public boolean hasPerfectConditions() {
        return hasHealthyTemperature() && food == FOOD_LIMIT && water == WATER_LIMIT;
    }

    public boolean hasNegativeCondition() {
        return !hasHealthyTemperature() || food == 0 || water == 0;
    }

    public int getActionPoints() {
        return actionPoints;
    }

    public void updateActionPoints(int onValue) {
        actionPoints += onValue;
    }

    public void resetActionPoints() {
        actionPoints = 1;
    }

    public void updateTemperature(int onValue) {
        temperature += onValue;
    }

    public void updateFood(int onValue) {
        food += onValue;
    }

    public void updateWater(int onValue) {
        water += onValue;
    }

    public void moveInto(AreaObject moveInto) {
        setPreviousAreaObject(areaObject);
        areaObject.getUnitsController().remove(this);
        moveInto.getUnitsController().addUnit(this);
        updateActionPoints(-1);
    }

    public void moveIntoAndValidate(AreaObject moveIntoObj) {
        moveInto(moveIntoObj);
        previousAreaObject.validate();
        moveIntoObj.validate();
    }

    public void setPreviousAreaObject(AreaObject previousAreaObject) {
        this.previousAreaObject = previousAreaObject;
    }

    public enum UnitSpecialization {
        MELEE, RANGE;

        public static UnitSpecialization getRandom() {
            return Utils.RANDOM.nextBoolean() ? Unit.UnitSpecialization.MELEE : Unit.UnitSpecialization.RANGE;
        }
    }
}
