package conversion7.engine.geometry.terrain;

import conversion7.engine.geometry.grid.TriangleVertex;

/**
 * Stores several triangle-vertices as one terrain vertex.<br>
 * Use it when you want manage position for all triangle-vertices at certain terrain position.
 */
@Deprecated
public class TerrainGrid {

    private final int width;
    private final int height;
    private final int verticesAmountX;
    private final int verticesAmountY;
    public TerrainVertexController[][] vertices;


    public TerrainGrid(int width, int height) {
        this.width = width;
        this.height = height;
        this.verticesAmountX = width + 1;
        this.verticesAmountY = height + 1;
        vertices = new TerrainVertexController[verticesAmountX][verticesAmountY];
        for (int x = 0; x < verticesAmountX; x++) {
            for (int y = 0; y < verticesAmountY; y++) {
                vertices[x][y] = new TerrainVertexController();
            }
        }
    }

    public void addVertex(TriangleVertex vertex, int x, int y) {
        vertices[x][y].addTriangleVertex(vertex);
    }
}
