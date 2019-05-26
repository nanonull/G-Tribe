package conversion7.tests_standalone.applications.terrain;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Material;
import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.geometry.grid.TriangleGrid;
import conversion7.engine.geometry.terrain.TerrainAttribute;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.test.TestScene;
import conversion7.game.stages.test.terrain.GridsTestModeler;
import org.testng.annotations.Test;

public class TerrainGridWithSharedVerticesTest {

    @Test
    public void run() {
        ClientApplication.startLibgdxCoreApp(new SharedVertices());
        Gdxg.core.waitCreated();
        Utils.infinitySleepThread();
    }

    // it has bugs with shared vertex units such as: textures
    class SharedVertices extends ClientCore {
        @Override
        public void create() {
            super.create();
            // test body:

            TriangleGrid triangleGrid = GridsTestModeler.createGridWithSharedVertices();
            Mesh mesh = triangleGrid.getMesh();
            Material terrainMaterial = new Material("TerrainMaterial",
//                    TextureAttribute.createDiffuse(Assets.grass)
                    new TerrainAttribute()
            );
            ModelActor modelActor = Modeler.createModelActorFromMesh(mesh, terrainMaterial,
                    triangleGrid.getVertexUnitsTotalAmount());
            modelActor.translate(0, 0, -10);

            // render it:
            TestScene testScene = new TestScene(Gdxg.graphic.getCamera());
            testScene.getCustomStage().addNode(modelActor);
            Gdxg.core.activateStage(testScene);
        }
    }
}
