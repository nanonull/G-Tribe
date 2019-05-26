package conversion7.game.stages.test;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;

public class TestModeler {

    public static void main(String[] args) {
        createSimpleMesh();
    }

    public static Mesh createSimpleMesh() {
        Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.TexCoords(0));
        float[] vertices = {-0.5f, -0.5f, 0, 1, 1, 1, 1, 0, 1,
                0.5f, -0.5f, 0, 1, 1, 1, 1, 1, 1,
                0.5f, 0.5f, 0, 1, 1, 1, 1, 1, 0,
                -0.5f, 0.5f, 0, 1, 1, 1, 1, 0, 0};
        mesh.setVertices(vertices);
        short[] indices = {0, 1, 2, 2, 3, 0};
        mesh.setIndices(indices);
        return mesh;
    }

    public static Mesh createSimpleMesh_2_noColor() {
        Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.TexCoords(0));
        float[] vertices = {-0.5f, -0.5f, 0, 0, 1,
                0.5f, -0.5f, 0, 1, 1,
                0.5f, 0.5f, 0, 1, 0,
                -0.5f, 0.5f, 0, 0, 0};
        mesh.setVertices(vertices);
        short[] indices = {0, 1, 2, 2, 3, 0};
        mesh.setIndices(indices);
        return mesh;
    }

    public static Mesh createMeshFromLibgdxTest() {
        Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.TexCoords(0));
        mesh.setVertices(new float[]{-0.5f, -0.5f, 0, 1, 1, 1, 1, 0, 1, 0.5f, -0.5f, 0, 1, 1, 1, 1, 1, 1, 0.5f, 0.5f, 0, 1, 1, 1,
                1, 1, 0, -0.5f, 0.5f, 0, 1, 1, 1, 1, 0, 0});
        mesh.setIndices(new short[]{0, 1, 2, 2, 3, 0});
        return mesh;
    }

}
