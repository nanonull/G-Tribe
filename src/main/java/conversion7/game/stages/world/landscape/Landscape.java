package conversion7.game.stages.world.landscape;

import conversion7.engine.geometry.terrain.TerrainVertexData;
import conversion7.game.GdxgConstants;
import conversion7.game.interfaces.HintProvider;

public class Landscape implements HintProvider {

    public static final int WATER_LEVEL = -1;

    public enum TYPE {
        COMMON(),
        MOUNTAINS(),
        WATER(),;
    }

    public TYPE type;
    /** -1, 0, 1 */
    public int level;
    /** 1 - forest */
    public int addition;

    public Soil soil;
    public Cell cell;
    private TerrainVertexData terrainVertexData;


    /**
     * Common cell
     * mountain
     * water. <p></p>
     * forest: addition = 1
     */
    public Landscape(TYPE type, int level, int addition, Soil soilBase) {
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
    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName())
                .append("type: ").append(type).append(GdxgConstants.HINT_SPLITTER)
                .append("level: ").append(level).append(GdxgConstants.HINT_SPLITTER)
                .append("addition: ").append(addition).append(GdxgConstants.HINT_SPLITTER)
                .append("soil: ").append(soil).append(GdxgConstants.HINT_SPLITTER);
        return sb.toString();
    }

    public String getHint() {
        return toString();
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public boolean hasForest() {
        return addition == 1;
    }

    public boolean hasHill() {
        return level == 1;
    }
}
