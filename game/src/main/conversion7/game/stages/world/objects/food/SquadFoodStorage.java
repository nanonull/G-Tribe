package conversion7.game.stages.world.objects.food;

import conversion7.game.stages.world.objects.unit.AbstractSquad;

@Deprecated
public class SquadFoodStorage extends AbstractFoodStorage {

    public static final int FOOD_STORAGE_PER_UNIT = 5;
    private int food;
    private AbstractSquad squad;

    public SquadFoodStorage(AbstractSquad squad) {
        super(squad);
        this.squad = squad;
    }

    @Override
    public int getFoodMax() {
        return FOOD_STORAGE_PER_UNIT;
    }
}
