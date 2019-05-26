package conversion7.game.stages.world.objects.food;

import conversion7.game.stages.world.objects.buildings.Camp;

@Deprecated
public class TownFoodStorage extends AbstractFoodStorage {

    private int food;
    private Camp camp;

    public TownFoodStorage(Camp camp) {
        super(camp);
        this.camp = camp;
    }

    @Override
    public int getFoodMax() {
        return MAX_FOOD_STATIC_LIMIT;
    }
}
