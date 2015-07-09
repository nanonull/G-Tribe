package conversion7.tests_standalone.applications;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import conversion7.engine.AbstractClientCore;
import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.test.TestScene;
import org.testng.annotations.Test;

public class MeshToModelTest {

    @Test
    public void run() {
        ClientApplication.start(new Run());
        AbstractClientCore.waitCoreCreated();
        Utils.infinitySleepThread();
    }

    class Run extends ClientCore {
        @Override
        public void create() {
            super.create();
            // test body:
            Mesh meshFromLibgdxTest = null; // fixme
            Material material = new Material(TextureAttribute.createDiffuse(Assets.grass));
            ModelActor modelActorFromMesh = Modeler.createModelActorFromMesh(meshFromLibgdxTest, material, -1);
            modelActorFromMesh.translate(0, 0, -10);

            // render it:
            TestScene testScene = new TestScene(Gdxg.graphic.getCamera());
            testScene.getCustomStage().addNode(modelActorFromMesh);
            core.activateStage(testScene);
        }
    }
}
