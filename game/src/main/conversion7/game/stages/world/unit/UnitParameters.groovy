package conversion7.game.stages.world.unit

import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.ObjectMap
import conversion7.engine.utils.AbstractEnumMap
import conversion7.engine.utils.MathUtils
import conversion7.game.unit_classes.UnitClassConstants
import groovy.transform.ToString

@ToString(includeFields = true, includeNames = true, includePackage = false, excludes = ['metaClass'])
public class UnitParameters extends AbstractEnumMap<UnitParameterType, Integer> {

    public static final int MAX_DISPERSION_PERCENTS = 5;
    // test
    public static int _negativeDispersion = 0;
    public static int _positiveDispersion = 0;
    public static final int START_HEALTH = UnitClassConstants.BASE_POWER
    private ObjectMap<UnitParameterType, Integer> parametersStorage = new ObjectMap<>();

    public UnitParameters(int height, int strength, int agility, int vitality) {
        put(UnitParameterType.HEIGHT, height);
        put(UnitParameterType.STRENGTH, strength);
        put(UnitParameterType.AGILITY, agility);
        put(UnitParameterType.VITALITY, vitality);

        updateHealthToVitality();
    }

    public UnitParameters() {
    }

    public ObjectMap<UnitParameterType, Integer> getParametersStorage() {
        return parametersStorage;
    }

    @Override
    public void put(UnitParameterType key, Integer value) {
        parametersStorage.put(key, value);
    }

    /** Only for initialization. Effects will be taken into account only in-world */
    public UnitParameters updateHealthToVitality() {
        put(UnitParameterType.HEALTH, get(UnitParameterType.VITALITY));
        return this;
    }

    @Override
    public Integer get(UnitParameterType key) {
        return parametersStorage.get(key);
    }

    @Override
    public String toString() {
        return UnitParameters.class.getSimpleName() + " " + parametersStorage.toString();
    }

    @Override
    public void update(UnitParameterType key, Integer value) {
        Integer oldVal = parametersStorage.get(key);
        if (oldVal == null) {
            put(key, value);
        } else {
            put(key, oldVal + value);
        }
    }

    @Override
    public void remove(UnitParameterType key) {
        throw new GdxRuntimeException("Not supported!");
    }

    public UnitParameters setTestParams() {
        return setDefault();
    }

    public UnitParameters setDefault() {
        put(UnitParameterType.HEIGHT, 60);
        put(UnitParameterType.STRENGTH, 20);
        put(UnitParameterType.AGILITY, 20);
        put(UnitParameterType.VITALITY, 20);
        return updateHealthToVitality();
    }

    public UnitParameters copyFrom(UnitParameters copy) {
        for (ObjectMap.Entry<UnitParameterType, Integer> entry : copy.parametersStorage) {
            parametersStorage.put(entry.key, entry.value)
        }
        return this;
    }

    public UnitParameters mixWith(UnitParameters mixWith) {
        for (ObjectMap.Entry<UnitParameterType, Integer> myParam : parametersStorage) {
            Integer otherParamValue = mixWith.get(myParam.key);
            if (otherParamValue != null) {
                parametersStorage.put(myParam.key, randomDivideOnTwo(myParam.value + otherParamValue))
            }
        }
        return this;
    }

    private static int randomDivideOnTwo(int source) {
        byte additionForRounded = 0;
        // random round: more often cut-off addition
        // random round is used in mutations
        if (source % 2 != 0 && MathUtils.RANDOM.nextInt(10) < 3) {
            additionForRounded = 1;
        }
        return source / 2 + additionForRounded;
    }

    public UnitParameters minus(UnitParameters diffFrom) {
        for (ObjectMap.Entry<UnitParameterType, Integer> myParam : parametersStorage) {
            Integer otherParamValue = diffFrom.get(myParam.key);
            if (otherParamValue != null) {
                parametersStorage.put(myParam.key, myParam.value - otherParamValue)
            }
        }
        return this;
    }

    public UnitParameters plus(UnitParameters diff) {
        for (ObjectMap.Entry<UnitParameterType, Integer> myParam : parametersStorage) {
            Integer otherParamValue = diff.get(myParam.key);
            if (otherParamValue != null) {
                parametersStorage.put(myParam.key, myParam.value + otherParamValue)
            }
        }
        return this;
    }

    public UnitParameters mutate() {
        for (ObjectMap.Entry<UnitParameterType, Integer> myParam : parametersStorage) {
            Integer paramValue = get(myParam.key);
            if (paramValue != null) {
                update(myParam.key, getDispersion(paramValue))
            }
        }
        return this;
    }

    private static int getDispersion(int source) {

        int dispersionLimit = Math.round(source * MAX_DISPERSION_PERCENTS / 100f);
        int dispersionValue = 0;

        int dispersionChance = 50;

        while (MathUtils.RANDOM.nextInt(100) < dispersionChance && dispersionValue < dispersionLimit) {
            dispersionValue++;
            dispersionChance -= 5;
        }

        if (MathUtils.RANDOM.nextBoolean()) { // revert to negative
            dispersionValue *= -1;
            _negativeDispersion++;
        } else {
            _positiveDispersion++;
        }

        return dispersionValue;
    }
}
