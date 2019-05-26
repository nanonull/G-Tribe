package conversion7.game.stages.world.landscape;

import conversion7.engine.utils.MathUtils;
import conversion7.game.interfaces.Validatable;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.items.AppleItem;
import conversion7.game.stages.world.inventory.items.RadioactiveIsotopeItem;
import conversion7.game.stages.world.inventory.items.StoneItem;
import conversion7.game.stages.world.inventory.items.StringItem;
import conversion7.game.stages.world.inventory.items.weapons.CudgelItem;
import conversion7.game.stages.world.inventory.items.weapons.StickItem;

public class ResourcesGenerator implements Validatable {

    private Cell cell;
    private int resourcesGeneratedOnStep = -1;
    public World world;

    public ResourcesGenerator(Cell cell) {
        this.cell = cell;
        world = cell.getArea().world;
    }

    @Override
    public void invalidate() {

    }

    @Override
    public void validate() {
        if (resourcesGeneratedOnStep < world.getStep()) {
            int stepsForGeneration = world.getStep() - resourcesGeneratedOnStep;
            resourcesGeneratedOnStep = world.getStep();
            generateResourcesTier1(stepsForGeneration);
        }
    }

    private void generateResourcesTier1(int stepsForGeneration) {
        int stoneValue = cell.getLandscape().soil.getSoilTypeValue(Soil.TypeId.STONE);
        int stoneChance = stoneValue / 2;
        int soilValue = cell.getLandscape().soil.getSoilTypeValue(Soil.TypeId.DIRT);
        int stringChance = Math.round(soilValue / 10);
        boolean hasForest = cell.getLandscape().hasForest();

        for (int i = 0; i < stepsForGeneration; i++) {
            if (testPercentChance(stoneChance)) {
                cell.getInventory().addItem(StoneItem.class, 1);
            }

            if (hasForest) {
                if (MathUtils.testPercentChance(50)) {
                    cell.getInventory().addItem(StickItem.class, 1);
                }

                if (MathUtils.testPercentChance(5)) {
                    cell.getInventory().addItem(CudgelItem.class, 1);
                }

                if (MathUtils.testPercentChance(Math.max(5, soilValue))) {
                    int appleQty = MathUtils.random(1, 5);
                    appleQty = MathUtils.random(1, appleQty);
                    appleQty = MathUtils.random(1, appleQty);
                    cell.getInventory().addItem(AppleItem.class, appleQty);
                }
            }

            if (MathUtils.testPercentChance(stringChance)) {
                cell.getInventory().addItem(StringItem.class, 1);
            }
        }
    }

    private boolean testPercentChance(int value) {
        if (world.settings.resBalance < 0) {
            value *= 0.5f;
        } else if (world.settings.resBalance > 0) {
            value *= 1.5f;
        }
        if (value <= 0) {
            value = 1;
        }
        return MathUtils.testPercentChance(value);
    }

}
