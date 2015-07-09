package conversion7.game.stages.world.unit;

import conversion7.engine.utils.Utils;

/**
 *
 */
public class UnitParameters {

    public static final int MAX_DISPERSION_PERCENTS = 5;

    // base
    private int height = 0;
    private int strength = 0;
    private int agility = 0;
    private int vitality = 0;

    public int health = 0;
    private int moral;

    public UnitParameters(int height, int strength, int agility, int vitality) {
        setHeight(height)
                .setStrength(strength)
                .setAgility(agility)
                .setVitality(vitality)
                .updateHealthToVitality();
    }

    public UnitParameters() {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PARAMETERS: ")
                .append("height=").append(height).append("; ")
                .append("strength=").append(strength).append("; ")
                .append("agility=").append(agility).append("; ")
                .append("vitality=").append(vitality);
        return sb.toString();
    }

    public UnitParameters setHeight(int newHeight) {
        this.height = newHeight;
        return this;
    }

    public UnitParameters setStrength(int newStrength) {
        this.strength = newStrength;
        return this;
    }

    public UnitParameters setAgility(int newAgility) {
        this.agility = newAgility;
        return this;
    }

    public UnitParameters setVitality(int newVitality) {
        this.vitality = newVitality;
        return this;
    }

    public UnitParameters updateHeight(int onValue) {
        this.height += onValue;
        return this;
    }

    public UnitParameters updateStrength(int onValue) {
        this.strength += onValue;
        return this;
    }

    public UnitParameters updateAgility(int onValue) {
        this.agility += onValue;
        return this;
    }

    public UnitParameters updateVitality(int onValue) {
        this.vitality += onValue;
        return this;
    }

    public void updateHealth(int onValue) {
        this.health += onValue;
    }

    public void updateMoral(int onValue) {
        this.moral += onValue;
    }

    /** Only for initialization. Effects should be taken into account in-world */
    public UnitParameters updateHealthToVitality() {
        setHealth(this.vitality);
        return this;
    }

    public UnitParameters setHealth(int newValue) {
        this.health = newValue;
        return this;
    }

    public UnitParameters setDefault() {
        return setHeight(60).setStrength(20).setAgility(20).setVitality(20).updateHealthToVitality();
    }

    public UnitParameters setTestParams() {
        return setDefault();
    }


    public UnitParameters copyFrom(UnitParameters copy) {
        setHeight(copy.height).setStrength(copy.strength).setAgility(copy.agility).setVitality(copy.vitality);
        return this;
    }

    private int divideOnTwo(int source) {
        byte additionForRounded = 0;
        // random round: more often cut-off addition
        // random round is used in mutations
        if (source % 2 != 0 && Utils.RANDOM.nextInt(10) < 3) {
            additionForRounded = 1;
        }
        return source / 2 + additionForRounded;
    }


    public UnitParameters mixWith(UnitParameters mixWith) {
        setHeight(divideOnTwo(height + mixWith.height));
        setStrength(divideOnTwo(strength + mixWith.strength));
        setAgility(divideOnTwo(agility + mixWith.agility));
        setVitality(divideOnTwo(vitality + mixWith.vitality));
        return this;
    }


    public UnitParameters createDiffFrom(UnitParameters diffFrom) {
        height = height - diffFrom.height;
        strength = strength - diffFrom.strength;
        agility = agility - diffFrom.agility;
        setVitality(vitality - diffFrom.vitality);
        return this;
    }

    public UnitParameters applyDiff(UnitParameters diff) {
        height = height + diff.height;
        strength = strength + diff.strength;
        agility = agility + diff.agility;
        setVitality(vitality + diff.vitality);
        return this;
    }

    public UnitParameters mutate(UnitParameters dispersionSource) {

        height = height + getDispersion(dispersionSource.height);
        strength = strength + getDispersion(dispersionSource.strength);
        agility = agility + getDispersion(dispersionSource.agility);
        setVitality(vitality + getDispersion(dispersionSource.vitality));

        return this;
    }

    public UnitParameters mutate() {
        return mutate(this);
    }

    private int getDispersion(int source) {

        int dispersionLimit = Math.round(source * MAX_DISPERSION_PERCENTS / 100f);
        int dispersionValue = 0;

        int dispersionChance = 50;

        while (Utils.RANDOM.nextInt(100) < dispersionChance && dispersionValue < dispersionLimit) {
            dispersionValue++;
            dispersionChance -= 5;
        }

        if (Utils.RANDOM.nextBoolean()) { // revert to negative
            dispersionValue *= -1;
            _negativeDispersion++;
        } else {
            _positiveDispersion++;
        }

        return dispersionValue;
    }

    public static int _negativeDispersion = 0;

    public static int _positiveDispersion = 0;

    public int getHeight() {
        return height;
    }

    public int getStrength() {
        return strength;
    }

    public int getAgility() {
        return agility;
    }

    public int getVitality() {
        return vitality;
    }

    public int getHealth() {
        return health;
    }

    public int getMoral() {
        return moral;
    }
}
