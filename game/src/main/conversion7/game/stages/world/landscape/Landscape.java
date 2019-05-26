package conversion7.game.stages.world.landscape;

import conversion7.engine.geometry.terrain.TerrainVertexData;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.interfaces.HintProvider;
import conversion7.game.stages.world.objects.BurningForest;
import conversion7.game.stages.world.objects.BurntForest;
import org.slf4j.Logger;

public class Landscape implements HintProvider {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final int WATER_LEVEL = -1;
    public static final int HILL_LEVEL = 1;
    public static final int DESERT_SAND = 50;
    private static final int STONE_LAND = 50;
    public Type type;
    private int level;
    private Addition addition;
    public Soil soil;
    public Cell cell;
    private TerrainVertexData terrainVertexData;

    public Landscape(Type type, int level, Addition addition, Soil soilBase) {
        this.type = type;
        this.level = level;
        this.addition = addition;
        this.soil = soilBase;
        this.terrainVertexData = new TerrainVertexData(this);
    }

    public TerrainVertexData getTerrainVertexData() {
        return terrainVertexData;
    }

    @Override
    public String getHint() {
        return toString();
    }

    /** -1, 0, 1 */
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        terrainVertexData.calcHeight();
    }

    public int getSand() {
        return soil.getSoilTypeValue(Soil.TypeId.SAND);
    }

    public int getStone() {
        return soil.getSoilTypeValue(Soil.TypeId.STONE);
    }

    public int getDirt() {
        return soil.getSoilTypeValue(Soil.TypeId.DIRT);
    }

    public boolean isDesert() {
        return getSand() > DESERT_SAND;
    }

    public boolean isStoneLand() {
        return getStone() > STONE_LAND;
    }

    public void setAddition(Addition addition) {
        this.addition = addition;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public boolean hasFireEffect() {
        return cell.getObject(BurningForest.class) != null ||
                cell.getObject(BurntForest.class) != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName())
                .append("type: ").append(type).append(GdxgConstants.HINT_SPLITTER)
                .append("level: ").append(level).append(GdxgConstants.HINT_SPLITTER)
                .append("addition: ").append(addition).append(GdxgConstants.HINT_SPLITTER)
                .append("soil: ").append(soil).append(GdxgConstants.HINT_SPLITTER);
        return sb.toString();
    }

    public boolean hasForest() {
        return addition == Addition.FOREST;
    }

    public void setForest() {
        addition = Addition.FOREST;
    }

    public boolean hasHill() {
        return level == 1;
    }

    public void setHill() {
        level = 1;
    }

    public boolean hasBog() {
        return addition == Addition.BOG;
    }

    public boolean hasMountain() {
        return type == Type.MOUNTAIN;
    }

    public enum Type {
        COMMON, MOUNTAIN, WATER
    }

    public enum Addition {
        NONE, FOREST, BOG
    }
}
