package conversion7.tests_standalone.applications.terrain;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.geometry.grid.TriangleGrid;
import conversion7.engine.geometry.terrain.TerrainAttribute;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.test.TestScene;
import conversion7.game.stages.test.terrain.TerrainGridTestModeler;
import org.testng.annotations.Test;

public class SegmentedTerrainGridTest {

    @Test
    public void runSegmentedTerrainGrid() {
        ClientApplication.startLibgdxCoreApp(new GetSegmentedTerrainGrid());
        Gdxg.core.waitCreated();
        Utils.infinitySleepThread();
    }

    class GetSegmentedTerrainGrid extends ClientCore {
        @Override
        public void create() {
            super.create();
            // test body:

            TriangleGrid terrainGridWithSegmentation = TerrainGridTestModeler.createDetailedTerrainGridWithSegmentation();
            Mesh mesh = terrainGridWithSegmentation.getMesh();
            Material terrainMaterial = new Material("TerrainMaterial",
                    TextureAttribute.createDiffuse(Assets.grass),
                    new TerrainAttribute()
            );
            ModelActor modelActor = Modeler.createModelActorFromMesh(mesh, terrainMaterial,
                    terrainGridWithSegmentation.getVertexUnitsTotalAmount());
            modelActor.translate(0, 0, -10);

            // render it:
            TestScene testScene = new TestScene(Gdxg.graphic.getCamera());
            testScene.getCustomStage().addNode(modelActor);
            Gdxg.core.activateStage(testScene);
        }
    }
}
