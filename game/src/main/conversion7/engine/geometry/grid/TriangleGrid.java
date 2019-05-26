package conversion7.engine.geometry.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.geometry.terrain.SoilData;
import conversion7.engine.geometry.terrain.TerrainAttribute;
import conversion7.engine.utils.Utils;
import org.fest.assertions.api.Fail;
import org.slf4j.Logger;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Based on Fabula engine's version */
public class TriangleGrid extends AbstractTriangleGrid implements Disposable {
    private static final Logger LOG = Utils.getLoggerForClass();
    private final int verticesAmount;
    private final int quadsAmount;
    private final int vertexSize;
    private int rows;
    private int columns;

    protected short vertexIndex;
    private short indicesCursor;

    protected ArrayList<TriangleVertex> verticesList;

    private float[] vertexUnits;
    private short[] indices;
    private Mesh mesh;
    private int vertexUnitsTotalAmount;
    public TriangleVertex currentVertex;
    private List<VertexAttribute> vertexAttributes;

    // TODO sort attributes in order filling vertexUnits
    public TriangleGrid(int width, int height, boolean isStatic, VertexAttribute[] attributes) {
        this.rows = height;
        this.columns = width;
        this.quadsAmount = rows * columns;
        this.verticesAmount = quadsAmount * 4;

        verticesList = new ArrayList<>(verticesAmount);
        vertexAttributes = new ArrayList<>();
        Collections.addAll(vertexAttributes, attributes);
        if (isUsing(Usage.ColorPacked) && isUsing(Usage.ColorUnpacked)) {
            throw new GdxRuntimeException("Both ColorPacked & ColorUnpacked is not supported in one mesh!");
        }

        this.vertexSize = getVertexSizeWithAttributes(this);
        this.vertexUnitsTotalAmount = verticesAmount * vertexSize;

        this.indices = new short[quadsAmount * 6];
    }

    public int getVertexAttributesAmount() {
        return vertexAttributes.size();
    }

    public int getVertexSize() {
        return vertexSize;
    }

    public int getQuadsAmount() {
        return quadsAmount;
    }

    public int getVerticesAmount() {
        return verticesAmount;
    }

    int end() {
        Assert.assertEquals(verticesList.size(), verticesAmount);
        Assert.assertEquals(indicesCursor, indices.length);

        this.vertexUnits = new float[vertexUnitsTotalAmount];
        boolean usingTextCord = isUsing(Usage.TextureCoordinates);
        boolean usingNormals = isUsing(Usage.Normal);
        boolean usingColorPacked = isUsing(Usage.ColorPacked);
        boolean usingColorUnpacked = isUsing(Usage.ColorUnpacked);
        boolean usingSoil = isUsing(Usage.Generic, TerrainAttribute.VERTEX_ALIAS);
        if (usingNormals) {
            calculateNormals();
        }

        int vertexCursor = 0;
        for (TriangleVertex vertex : this.verticesList) {
            this.vertexUnits[vertexCursor++] = vertex.position.x;
            this.vertexUnits[vertexCursor++] = vertex.position.y;
            this.vertexUnits[vertexCursor++] = vertex.position.z;

            if (usingNormals) {
                vertex.normal.nor();
                this.vertexUnits[vertexCursor++] = vertex.normal.x;
                this.vertexUnits[vertexCursor++] = vertex.normal.y;
                this.vertexUnits[vertexCursor++] = vertex.normal.z;
            }

            if (usingColorPacked) {
                this.vertexUnits[vertexCursor++] = Color.toFloatBits(vertex.color.r, vertex.color.g, vertex.color.b, vertex.color.a);
            }

            if (usingColorUnpacked) {
                this.vertexUnits[vertexCursor++] = vertex.color.r;
                this.vertexUnits[vertexCursor++] = vertex.color.g;
                this.vertexUnits[vertexCursor++] = vertex.color.b;
                this.vertexUnits[vertexCursor++] = vertex.color.a;
            }

            if (usingTextCord) {
                this.vertexUnits[vertexCursor++] = vertex.textureCordinates.x;
                this.vertexUnits[vertexCursor++] = vertex.textureCordinates.y;
            }

            if (usingSoil) {
                this.vertexUnits[vertexCursor++] = vertex.soil.dirt;
                this.vertexUnits[vertexCursor++] = vertex.soil.sand;
                this.vertexUnits[vertexCursor++] = vertex.soil.stone;
                this.vertexUnits[vertexCursor++] = vertex.soil.reserved;
            }

        }

        return vertexCursor;
    }

    private boolean isUsing(int attributeUsage, String alias) {
        for (VertexAttribute vertexAttribute : vertexAttributes) {
            if (vertexAttribute.usage == attributeUsage
                    && (alias == null || alias.equals(vertexAttribute.alias))) {
                return true;
            }
        }
        return false;
    }

    private boolean isUsing(int attributeUsage) {
        return isUsing(attributeUsage, null);
    }

    public Mesh getMesh() {
        end();
        Assert.assertNotNull(vertexUnits);
//        testOnNan();

        mesh = new Mesh(true, this.verticesAmount, this.indices.length,
                vertexAttributes.toArray(new VertexAttribute[vertexAttributes.size()])
        );
        mesh.setVertices(this.vertexUnits);
        mesh.setIndices(this.indices);
        // TODO pool array
        this.verticesList.clear();
        return mesh;
    }

    public static int getVertexSizeWithAttributes(TriangleGrid triangleGrid) {
        int size = 0;
        for (VertexAttribute vertexAttribute : triangleGrid.vertexAttributes) {
            switch (vertexAttribute.usage) {
                case Usage.Position:
                    size += 3;
                    break;
                case Usage.Normal:
                    size += 3;
                    break;
                case Usage.TextureCoordinates:
                    size += 2;
                    break;
                case Usage.ColorPacked:
                    size += 1;
                    break;
                case Usage.ColorUnpacked:
                    size += 4;
                    break;
                case Usage.Generic:
                    if (vertexAttribute.alias.equals(TerrainAttribute.VERTEX_ALIAS)) {
                        size += 4;
                        break;
                    }
                default:
                    throw new GdxRuntimeException("unknown VertexAttribute!");
            }
        }
        return size;
    }

    public void calculateNormals() {
        if (indices.length <= 3) {
            Fail.fail("indices.length <= 3");
        }

        int normalsCount = indices.length / 3;
        // TODO i in loop definition should be as tripleIndex (i +=3)
        int tripleIndex = 0;
        for (int i = 0; i < normalsCount; i++) {
            tripleIndex = i * 3;
            int index1 = indices[tripleIndex];
            int index2 = indices[tripleIndex + 1];
            int index3 = indices[tripleIndex + 2];

            TriangleVertex vertex1 = verticesList.get(index1);
            Vector3 side1 = vertex1.position.cpy().sub(verticesList.get(index3).position);
            Vector3 side2 = vertex1.position.cpy().sub(verticesList.get(index2).position);
            Vector3 normal = side1.crs(side2);

            this.verticesList.get(index1).normal.add(normal);
            this.verticesList.get(index2).normal.add(normal);
            this.verticesList.get(index3).normal.add(normal);
        }
    }

    public short addVertex(float x, float y, float z) {
        currentVertex = new TriangleVertex();
        currentVertex.position.set(x, y, z);
        this.verticesList.add(currentVertex);
        return vertexIndex++;
    }

    public void addNormal() {
        this.addNormal(0.0f, 0.0f, 0.0f);
    }

    public void addNormal(float x, float y, float z) {
        currentVertex.normal.set(x, y, z);
    }

    public void addColorToVertex(float r, float g, float b, float a) {
        currentVertex.color.set(r, g, b, a);
    }

    public void addUVMap(float u, float v) {
        currentVertex.textureCordinates.set(u, v);
    }

    public void addSoil(float dirt, float sand, float stone, float dummy) {
        addSoil(new SoilData(dirt, sand, stone, dummy));
    }

    public void addSoil(SoilData soilData) {
        currentVertex.soil = soilData;
    }

    public void addIndices(short n1, short n2, short n3) {
        this.indices[indicesCursor++] = n1;
        this.indices[indicesCursor++] = n2;
        this.indices[indicesCursor++] = n3;
    }

    @Override
    public void dispose() {
        if (this.mesh != null) {
            this.mesh.dispose();
        }
        this.mesh = null;
        verticesList = null;
    }

    public int getColumns() {
        return this.columns;
    }

    public int getRows() {
        return this.rows;
    }


    public float[] getVertexUnits() {
        return vertexUnits;
    }

    public short[] getIndices() {
        return indices;
    }

    public int getVertexUnitsTotalAmount() {
        return vertexUnitsTotalAmount;
    }

}
