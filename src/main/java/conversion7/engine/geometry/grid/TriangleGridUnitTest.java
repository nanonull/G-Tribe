package conversion7.engine.geometry.grid;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import conversion7.engine.AbstractClientCore;
import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.geometry.terrain.TerrainAttribute;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.test.TestScene;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class TriangleGridUnitTest {

    @Test
    public void oneCellGridHasSize() {
        TriangleGrid triangleGrid = new TriangleGrid(1, 1, false,
                new VertexAttribute[]{VertexAttribute.Position(), VertexAttribute.TexCoords(0)});

        // size
        assertThat(triangleGrid.getRows()).isEqualTo(1);
        assertThat(triangleGrid.getColumns()).isEqualTo(1);
        assertThat(triangleGrid.getQuadsAmount()).isEqualTo(1);
        assertThat(triangleGrid.getVerticesAmount()).isEqualTo(4);

        int expVertexSize = 3 + 2;
        assertThat(triangleGrid.getVertexSize()).isEqualTo(expVertexSize);


        assertThat(triangleGrid.getVertexUnitsTotalAmount()).isEqualTo(expVertexSize * triangleGrid.getVerticesAmount());
        assertThat(triangleGrid.getIndices()).hasSize(6);

        // build
        triangleGrid.addVertex(0, 0, 0);
        triangleGrid.addVertex(0, 0, 0);
        triangleGrid.addVertex(0, 0, 0);
        triangleGrid.addVertex(0, 0, 0);
        triangleGrid.addIndices((short) 0, (short) 0, (short) 0);
        triangleGrid.addIndices((short) 0, (short) 0, (short) 0);


        int endVertexCursor = triangleGrid.end();
        assertThat(triangleGrid.getVertexUnits()).hasSize(triangleGrid.getVertexUnitsTotalAmount());
        assertThat(endVertexCursor).isEqualTo(triangleGrid.getVertexUnits().length);
    }

    @Test
    public void twoCellGridHasSize() {
        int width = 2;
        TriangleGrid triangleGrid = new TriangleGrid(width, 1, false,
                new VertexAttribute[]{VertexAttribute.Position(), VertexAttribute.TexCoords(0)});

        // size
        assertThat(triangleGrid.getRows()).isEqualTo(1);
        assertThat(triangleGrid.getColumns()).isEqualTo(width);
        assertThat(triangleGrid.getQuadsAmount()).isEqualTo(2);
        assertThat(triangleGrid.getVerticesAmount()).isEqualTo(8);

        int expVertexSize = 3 + 2;
        assertThat(triangleGrid.getVertexSize()).isEqualTo(expVertexSize);

        assertThat(triangleGrid.getVertexUnitsTotalAmount()).isEqualTo(expVertexSize * triangleGrid.getVerticesAmount());
        assertThat(triangleGrid.getIndices()).hasSize(triangleGrid.getQuadsAmount() * 6);

        // build
        triangleGrid.addVertex(0, 0, 0);
        triangleGrid.addVertex(0, 0, 0);
        triangleGrid.addVertex(0, 0, 0);
        triangleGrid.addVertex(0, 0, 0);
        triangleGrid.addVertex(0, 0, 0);
        triangleGrid.addVertex(0, 0, 0);
        triangleGrid.addVertex(0, 0, 0);
        triangleGrid.addVertex(0, 0, 0);
        triangleGrid.addIndices((short) 0, (short) 0, (short) 0);
        triangleGrid.addIndices((short) 0, (short) 0, (short) 0);
        triangleGrid.addIndices((short) 0, (short) 0, (short) 0);
        triangleGrid.addIndices((short) 0, (short) 0, (short) 0);

        int endVertexCursor = triangleGrid.end();
        assertThat(triangleGrid.getVertexUnits()).hasSize(triangleGrid.getVertexUnitsTotalAmount());
        assertThat(endVertexCursor).isEqualTo(triangleGrid.getVertexUnits().length);
    }

    @Test
    public void getMesh_textured_orderAsInSimpleMesh() {
        ClientApplication.start(new GetMeshTest());
        AbstractClientCore.waitCoreCreated();
        Utils.infinitySleepThread();
    }

    class GetMeshTest extends ClientCore {
        @Override
        public void create() {
            super.create();

            TriangleGrid triangleGrid = new TriangleGrid(1, 1, false,
                    new VertexAttribute[]{VertexAttribute.Position(), VertexAttribute.TexCoords(0)});

            triangleGrid.addVertex(-0.5f, -0.5f, 0);
            triangleGrid.addUVMap(0, 1);
            triangleGrid.addVertex(0.5f, -0.5f, 0);
            triangleGrid.addUVMap(1, 1);
            triangleGrid.addVertex(0.5f, 0.5f, 0);
            triangleGrid.addUVMap(1, 0);
            triangleGrid.addVertex(-0.5f, 0.5f, 0);
            triangleGrid.addUVMap(0, 0);
            triangleGrid.addIndices((short) 0, (short) 1, (short) 2);
            triangleGrid.addIndices((short) 2, (short) 3, (short) 0);

            Mesh mesh = triangleGrid.getMesh();

//            Assertions.assertThat(mesh.getVertexSize()).isEqualTo(triangleGrid.getVertexSize());
//            // verticesAmount
//            Assertions.assertThat(mesh.getMaxVertices()).isEqualTo(triangleGrid.getVerticesAmount());
//            // indices
//            Assertions.assertThat(mesh.getMaxIndices()).isEqualTo(triangleGrid.getIndices().length);
//            Assertions.assertThat(mesh.getVertexAttributes()).hasSize(triangleGrid.getVertexAttributesAmount());

            ModelActor modelActor = Modeler.createModelActorFromMesh(mesh,
                    new Material("TerrainMaterial", new TerrainAttribute()),
                    -1);
            modelActor.translate(0, 0, -10);

            TestScene testScene = new TestScene(Gdxg.graphic.getCamera());
            testScene.getCustomStage().addNode(modelActor);
            core.activateStage(testScene);
        }
    }

    @Test
    public void getMesh2() {
        ClientApplication.start(new GetMesh2());
        AbstractClientCore.waitCoreCreated();
        Utils.infinitySleepThread();
    }

    class GetMesh2 extends ClientCore {
        @Override
        public void create() {
            super.create();
            // test body:
            TriangleGrid triangleGrid = new TriangleGrid(20, 10, false,
                    new VertexAttribute[]{VertexAttribute.Position(), VertexAttribute.TexCoords(0)});

            short n1, n2, n3;
            float cellHeight = 0;
            float cellSegmentSize = 1;

            for (int xPos = 0; xPos < triangleGrid.getColumns(); xPos++) {
                for (int yPos = 0; yPos < triangleGrid.getRows(); yPos++) {
                    n1 = triangleGrid.addVertex(xPos + cellSegmentSize, cellHeight, yPos);
                    triangleGrid.addUVMap(1, 0);
                    n2 = triangleGrid.addVertex(xPos, cellHeight, yPos);
                    triangleGrid.addUVMap(0, 1);
                    n3 = triangleGrid.addVertex(xPos + cellSegmentSize, cellHeight, yPos + cellSegmentSize);
                    triangleGrid.addUVMap(1, 1);
                    triangleGrid.addIndices(n1, n2, n3);
                    n1 = triangleGrid.addVertex(xPos, cellHeight, yPos + cellSegmentSize);
                    triangleGrid.addUVMap(0, 0);
                    triangleGrid.addIndices(n3, n2, n1);
                }
            }

            Mesh mesh = triangleGrid.getMesh();

            ModelActor modelActor = Modeler.createModelActorFromMesh(mesh,
                    new Material(TextureAttribute.createDiffuse(Assets.grass)),
//                    new Material("TerrainMaterial", new TerrainAttribute())
                    -1);
            modelActor.translate(0, 0, -10);

            // render it:
            TestScene testScene = new TestScene(Gdxg.graphic.getCamera());
            testScene.getCustomStage().addNode(modelActor);
            core.activateStage(testScene);
        }
    }


}
