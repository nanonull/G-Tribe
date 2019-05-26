package conversion7.engine.geometry.terrain;

public class TerrainDataGrid {

    private final int verticesAmountX;
    private final int verticesAmountY;
    public TerrainVertexData[][] vertices;

    public TerrainDataGrid(int width, int height) {
        this.verticesAmountX = width + 1;
        this.verticesAmountY = height + 1;
        vertices = new TerrainVertexData[verticesAmountX][verticesAmountY];
    }
}
