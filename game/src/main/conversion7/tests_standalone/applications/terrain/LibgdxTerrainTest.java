package conversion7.tests_standalone.applications.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.geometry.terrain.TerrainAttribute;
import conversion7.game.Assets;
import conversion7.game.stages.test.TestScene;

import java.util.Random;

public class LibgdxTerrainTest extends ClientCore {
    TerrainChunk chunk;
    Mesh mesh;
    PerspectiveCamera camera;
    private Random rand = new Random();
    private float[] heightmap;

    public static void main(String[] args) {
        ClientApplication.startLibgdxCoreApp(new LibgdxTerrainTest());
    }

    @Override
    public void create() {
        super.create();

        chunk = new TerrainChunk(32, 32, 4);
        this.heightmap = chunk.heightMap;

        int len = chunk.vertices.length;
        for (int i = 3; i < len; i += 4) {
            chunk.vertices[i] = Color.toFloatBits(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255), 255);
        }
        mesh = new Mesh(true, chunk.vertices.length / 3, chunk.indices.length, new VertexAttribute(VertexAttributes.Usage.Position,
                3, "a_position"), new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"));

        mesh.setVertices(chunk.vertices);
        mesh.setIndices(chunk.indices);

        Node node = new Node();

        Material material = new Material("TerrainMaterial",
                new TerrainAttribute()
//                TextureAttribute.createDiffuse(Assets.grass)
        );
        MeshPart meshPart = new MeshPart("terrain-mesh", mesh, 0, mesh.getNumVertices(), GL20.GL_TRIANGLES);
        NodePart nodePart = new NodePart(meshPart, material);
        node.parts.add(nodePart);

        Model model = new Model();
        model.nodes.add(node);

        ModelActor modelActor = new ModelActor("Terrain", new ModelInstance(model), Gdxg.modelBatch);
        modelActor.translate(0, 0, -10);

        TestScene testScene = new TestScene(Gdxg.graphic.getCamera());
        testScene.getCustomStage().addNode(modelActor);
        Gdxg.core.activateStage(testScene);
    }

    final static class TerrainChunk {
        public final float[] heightMap;
        public final short width;
        public final short height;
        public final float[] vertices;
        public final short[] indices;
        public final int vertexSize;

        public TerrainChunk(int width, int height, int vertexSize) {
            if ((width + 1) * (height + 1) > Short.MAX_VALUE)
                throw new IllegalArgumentException("Chunk size too big, (width + 1)*(height+1) must be <= 32767");

            this.heightMap = new float[(width + 1) * (height + 1)];
            this.width = (short) width;
            this.height = (short) height;
            this.vertices = new float[heightMap.length * vertexSize];
            this.indices = new short[width * height * 6];
            this.vertexSize = vertexSize;

            buildHeightmap(Assets.IMAGES_FOR_ATLAS_FOLDER + "/grass_test_128_32b.png");
            buildIndices();
            buildVertices();
        }

        public void buildHeightmap(String pathToHeightMap) {
            /** get the heightmap from filesystem... should match width and height from current chunk..otherwise its just flat on
             * missing pixel but no error thrown */

            FileHandle handle = Gdx.files.internal(pathToHeightMap);
            Pixmap heightmapImage = new Pixmap(handle);
            Color color = new Color();
            int idh = 0; // index to iterate

            for (int x = 0; x < this.width + 1; x++) {
                for (int y = 0; y < this.height + 1; y++) {
                    // we need seperated channels..
                    Color.rgba8888ToColor(color, heightmapImage.getPixel(x, y)); // better way to get pixel ?
                    // pick whatever channel..we do have a b/w map
                    this.heightMap[idh++] = color.r;
                }
            }
        }

        public void buildVertices() {
            int heightPitch = height + 1;
            int widthPitch = width + 1;

            int idx = 0;
            int hIdx = 0;
            int inc = vertexSize - 3;
            int strength = 4; // multiplier for heightmap

            for (int z = 0; z < heightPitch; z++) {
                for (int x = 0; x < widthPitch; x++) {
                    vertices[idx++] = x;
                    vertices[idx++] = heightMap[hIdx++] * strength;
                    vertices[idx++] = z;
                    idx += inc;
                }
            }
        }

        private void buildIndices() {
            int idx = 0;
            short pitch = (short) (width + 1);
            short i1 = 0;
            short i2 = 1;
            short i3 = (short) (1 + pitch);
            short i4 = pitch;

            short row = 0;

            for (int z = 0; z < height; z++) {
                for (int x = 0; x < width; x++) {
                    indices[idx++] = i1;
                    indices[idx++] = i2;
                    indices[idx++] = i3;

                    indices[idx++] = i3;
                    indices[idx++] = i4;
                    indices[idx++] = i1;

                    i1++;
                    i2++;
                    i3++;
                    i4++;
                }

                row += pitch;
                i1 = row;
                i2 = (short) (row + 1);
                i3 = (short) (i2 + pitch);
                i4 = (short) (row + pitch);
            }
        }
    }

}