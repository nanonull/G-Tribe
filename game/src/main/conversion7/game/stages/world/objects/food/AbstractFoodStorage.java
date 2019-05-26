package conversion7.game.stages.world.objects.food;

import conversion7.engine.utils.FastAsserts;
import conversion7.game.stages.world.objects.AreaObject;

@Deprecated
public abstract class AbstractFoodStorage {

    public static final int MAX_FOOD_STATIC_LIMIT = 99999;
    public static final int FOOD_LOSSES_PER_STEP = 1;
    private int food;
    private AreaObject object;

    public AbstractFoodStorage(AreaObject object) {
        this.object = object;
    }

    public int getFood() {
        return food;
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
        object.getActionsController().invalidate();
    }

    public abstract int getFoodMax();

    public void setFoodAndValidate(int newFoodValue) {
        setFood(newFoodValue);
        object.validate();
    }

    public void updateFoodOnValueAndValidate(int value) {
        updateFoodOnValue(value);
        object.validate();
    }

    public void updateFoodOnValue(int value) {
        int newFoodValue = food + value;
        if (newFoodValue < 0) {
            newFoodValue = 0;
        }
        setFood(newFoodValue);
    }

    public void trimCollectedFoodByLimit() {
        int storageLimit = getFoodMax();
        if (food > storageLimit) {
            setFood(storageLimit);
        }
    }
}
