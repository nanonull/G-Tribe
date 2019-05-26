package conversion7.game.stages.world.climate;

import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.Normalizer;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.Winter;

public class WinterEvent {
    private static final float WINTER_TAKES_WORLD_WIDTH_MLT = 0.33f;
    public static final int MAX_COLD = Climate.TEMPERATURE_RADIUS / 2;
    private World world;
    private int width;
    private final int midX;
    private final int startX;
    private final int endX;
    private Cell startOnCell;

    public WinterEvent(World world) {
        this.world = world;
        width = (int) (world.widthInCells * WINTER_TAKES_WORLD_WIDTH_MLT);
        if (width % 2 == 0) {
            width++;
        }
        startX = 0;
        midX = width / 2;
        endX = width - 1;

        placeFrom(world.getCell(world.widthInCells / 2, 0));
    }

    public Cell getStartOnCell() {
        return startOnCell;
    }

    private void placeFrom(Cell leftmostCell) {
        startOnCell = leftmostCell;
        for (int winterX = startX; winterX < endX; winterX++) {
            for (int worldY = 0; worldY < world.heightInCells; worldY++) {
                Cell winterCell = startOnCell.getCell(winterX, worldY);
                new Winter(winterCell, this);
            }
        }
    }

    public void moveRight() {

        // remove WinterObject from cells in first column
        for (int worldY = 0; worldY < world.heightInCells; worldY++) {
            Cell winterCell = startOnCell.getCell(startX, worldY);
            winterCell.removeObjectIfExist(Winter.class);
        }

        // add WinterObject to cells after last column
        for (int worldY = 0; worldY < world.heightInCells; worldY++) {
            Cell winterCell = startOnCell.getCell(endX, worldY);
            new Winter(winterCell, this);
        }

        // move 1 step
        startOnCell = startOnCell.getCell(1, 0);
    }

    public int getTemperatureOn(Winter winter) {
        int normalizedTemp = (int) Normalizer.normalize(getWinterValue(winter), 1, 0, MAX_COLD, 1);
        return -normalizedTemp;
    }

    /** Low for corner cells, and high for mid winter */
    public float getWinterValue(Winter winter) {
        Point2s diffToStart = startOnCell.getDiffWithCell(winter.getLastCell());
        float winterValue;
        if (diffToStart.x <= midX) {
            // left part
            winterValue = diffToStart.x / (float) midX;
        } else {
            // right part
            winterValue = (width - diffToStart.x - 1) / (float) midX;
        }
        if (winterValue <= 0) {
            winterValue = 0.01f;
        }
        return winterValue;
    }

}
