package conversion7.tests_standalone.applications.terrain;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.game.Assets;
import conversion7.game.stages.test.TestModeler;
import conversion7.game.stages.test.TestScene;

public class TerrainShaderTest extends ClientCore {

    public static void main(String[] args) {
        ClientCore.initWorldFromCore = false;
        ClientApplication.start(new TerrainShaderTest());
    }

    @Override
    public void create() {
        super.create();

        Mesh mesh = TestModeler.createSimpleMesh_2_noColor();

        Node node = new Node();

        Material material =
                new Material(TextureAttribute.createDiffuse(Assets.grass)
//                new Material("TerrainMaterial", new TerrainAttribute()
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
        core.activateStage(testScene);
    }
}
