package conversion7.engine.geometry.terrain;

public class VertexHeightMap {
    private final int width;
    private final int height;
    public final int verticesAmountX;
    public final int verticesAmountY;
    public float[][] heights;

    public VertexHeightMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.verticesAmountX = width + 1;
        this.verticesAmountY = height + 1;
        heights = new float[verticesAmountX][verticesAmountY];
    }
}
