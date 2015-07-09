package conversion7.game.stages.world.objects;

import conversion7.engine.utils.FastAsserts;

public class FoodStorage {

    private static final int MAX_FOOD_STATIC_LIMIT = 999;
    public static final int FOOD_LOSSES_PER_STEP = 1;
    public static final int FOOD_STORAGE_PER_UNIT = 5;
    private int food;
    private AreaObject areaObject;

    public FoodStorage(AreaObject areaObject) {

        this.areaObject = areaObject;
    }

    public int getFood() {
        return food;
    }

    public int getFoodMax() {
        if (areaObject.isTownFragment()) {
            return MAX_FOOD_STATIC_LIMIT;
        } else {
            return areaObject.getUnits().size * FOOD_STORAGE_PER_UNIT;
        }
    }

    public void updateFoodOnValue(int value) {
        int newFoodValue = food + value;
        if (newFoodValue < 0) {
            newFoodValue = 0;
        }
        setFood(newFoodValue);
    }

    public void setFood(int newFoodValue) {
        FastAsserts.assertMoreThanOrEqual(newFoodValue, 0);

        if (newFoodValue > MAX_FOOD_STATIC_LIMIT) {
            if (food == MAX_FOOD_STATIC_LIMIT) {
                return;
            }
            food = MAX_FOOD_STATIC_LIMIT;
        } else {
            food = newFoodValue;
        }
        areaObject.getActionsController().invalidate();
    }

    public void updateFoodOnValueAndValidate(int value) {
        updateFoodOnValue(value);
        validateDependencies();
    }

    public void setFoodAndValidate(int newFoodValue) {
        setFood(newFoodValue);
        validateDependencies();
    }

    public void validateDependencies() {
        areaObject.getActionsController().validate();
    }

    public void trimCollectedFoodByUnitsLimit() {
        int storageLimit = getFoodMax();
        if (food > storageLimit) {
            setFood(storageLimit);
        }
    }
}
