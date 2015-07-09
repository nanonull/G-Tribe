package conversion7.game.stages.world.landscape;

import conversion7.engine.utils.Utils;
import conversion7.game.interfaces.Validatable;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.items.CudgelItem;
import conversion7.game.stages.world.inventory.items.StickItem;
import conversion7.game.stages.world.inventory.items.StoneItem;
import conversion7.game.stages.world.inventory.items.StringItem;

public class ResourcesGenerator implements Validatable {

    private Cell cell;
    private int resourcesGeneratedOnStep = -1;

    public ResourcesGenerator(Cell cell) {
        this.cell = cell;
    }

    @Override
    public void invalidate() {

    }

    @Override
    public void validate() {
        if (resourcesGeneratedOnStep < World.step) {
            int stepsForGeneration = World.step - resourcesGeneratedOnStep;
            resourcesGeneratedOnStep = World.step;
            generateResources(stepsForGeneration);
        }
    }

    private void generateResources(int stepsForGeneration) {
        int stoneValue = cell.getLandscape().soil.getSoilTypeValue(Soil.TypeId.STONE);
        int stringChance = Math.round(cell.getLandscape().soil.getSoilTypeValue(Soil.TypeId.DIRT) / 10);
        boolean hasForest = cell.getLandscape().hasForest();

        for (int i = 0; i < stepsForGeneration; i++) {
            if (Utils.RANDOM.nextInt(100) < stoneValue) {
                cell.getInventory().addItem(StoneItem.class, 1);
            }

            if (hasForest) {
                if (Utils.RANDOM.nextInt(10) < 8) {
                    cell.getInventory().addItem(StickItem.class, 1);
                }

                if (Utils.RANDOM.nextInt(10) < 2) {
                    cell.getInventory().addItem(CudgelItem.class, 1);
                }
            }

            if (Utils.RANDOM.nextInt(100) < stringChance) {
                cell.getInventory().addItem(StringItem.class, 1);
            }
        }
    }
}
