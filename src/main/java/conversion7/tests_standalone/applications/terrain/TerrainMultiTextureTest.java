package conversion7.tests_standalone.applications.terrain;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.CustomStage;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.geometry.grid.TriangleGrid;
import conversion7.engine.geometry.terrain.TerrainAttribute;
import conversion7.engine.geometry.terrain.TerrainShader;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.test.TestScene;
import conversion7.game.stages.test.terrain.TerrainGridTestModeler;
import org.slf4j.Logger;

// TODO multi-texture
public class TerrainMultiTextureTest extends ClientCore {

    private static final Logger LOG = Utils.getLoggerForClass();
    ModelActor terrainModelActor;
    ModelActor modelActor2;
    ModelActor modelActor;
    ModelActor modelActor1;
    ModelBatch terrainBatch;
    CustomStage customStage;


    public static void main(String[] args) {
        TerrainMultiTextureTest application = new TerrainMultiTextureTest();
        ClientCore.initWorldFromCore = false;
        ClientApplication.start(application);
    }

    @Override
    public void create() {
        super.create();

        terrainBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                if (TerrainShader.isApplicableTo(renderable)) {
                    return new TerrainShader(renderable);
                }
                throw new GdxRuntimeException(".!.");
            }
        });

        terrainModelActor = getTerrain(terrainBatch);
        terrainModelActor.translate(0, -2, -10);

        customStage = new CustomStage("terra", Gdxg.graphic.getCamera());
        customStage.addNode(terrainModelActor);

        modelActor = PoolManager.ARMY_MODEL_POOL.obtain();
        modelActor.setName("modelActor");
        modelActor1 = PoolManager.ARMY_MODEL_POOL.obtain();
        modelActor1.setName("modelActor1");
        modelActor2 = PoolManager.ARMY_MODEL_POOL.obtain();
        modelActor2.setName("modelActor2");

        TestScene testScene = new TestScene(Gdxg.graphic.getCamera());
        testScene.getCustomStage().addNode(modelActor2);
        testScene.getCustomStage().addNode(modelActor);
        modelActor.translate(1, 0, 0);
        testScene.getCustomStage().addNode(modelActor1);
        core.activateStage(testScene);
    }

    @Override
    public void render() {
        if (modelActor2.globalPosition.x < 10) {
            modelActor2.translate(0.08f, 0, -0.08f);
        }

        terrainBatch.begin(Gdxg.graphic.getCamera());
        super.render();
        customStage.draw();
        terrainBatch.end();
    }

    public static ModelActor getTerrain(ModelBatch modelBatch) {
        Material terrainMaterial = new Material("TerrainMaterial",
//                TextureAttribute.createDiffuse(Assets.grass)
                new TerrainAttribute()
        );
        TriangleGrid terrainGridWithSegmentation = TerrainGridTestModeler.createDetailedTerrainGridWithSegmentation();
        Mesh mesh = terrainGridWithSegmentation.getMesh();

        return Modeler.createModelActorFromMesh(mesh, terrainMaterial,
                terrainGridWithSegmentation.getVertexUnitsTotalAmount(), modelBatch);
    }


    public static ModelActor getTerrain() {
        return getTerrain(Gdxg.modelBatch);
    }
}
