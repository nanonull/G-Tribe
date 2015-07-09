package conversion7.engine.geometry.terrain;

import conversion7.engine.geometry.grid.TriangleVertex;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class TerrainVertexController {

    private List<TriangleVertex> vertices = new ArrayList<>();

    public void addTriangleVertex(TriangleVertex newVertex) {
        vertices.add(newVertex);
    }

    public void setY(float y) {
        for (TriangleVertex vertice : vertices) {
            vertice.position.y = y;
        }
    }

    public void setSoil(SoilData soil) {
        for (TriangleVertex vertice : vertices) {
            vertice.soil = soil;
        }
    }
}
