package conversion7.game.stages.test.terrain;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.utils.Disposable;
import conversion7.engine.geometry.grid.TriangleGrid;
import conversion7.engine.geometry.grid.TriangleVertex;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public class TriangleGridWithSharedVertices extends TriangleGrid implements Disposable {
    private static final Logger LOG = Utils.getLoggerForClass();

    public TriangleGridWithSharedVertices(int width, int height, boolean isStatic, VertexAttribute[] attributes) {
        super(width, height, isStatic, attributes);
    }

    public short addVertex(float x, float y, float z) {
        TriangleVertex vertex = new TriangleVertex();
        vertex.position.set(x, y, z);
        return addVertex(vertex);
    }

    public short addVertex(TriangleVertex vertex) {
        currentVertex = vertex;
        this.verticesList.add(currentVertex);
        return vertexIndex++;
    }


}
