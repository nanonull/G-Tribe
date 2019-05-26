package conversion7.tests_standalone.applications.mesh_part_issue;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import conversion7.game.Assets;

public class RenderMeshWithDefaultShader_meshPartCreation implements ApplicationListener, InputProcessor {

    private static Texture TEXTURE;

    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Model model;
    private ModelInstance modelInstance1;
    private ModelInstance modelInstance2;
    private Environment environment;

    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "RenderModelInstanceTest";
        cfg.vSyncEnabled = true;
        cfg.width = 1024;
        cfg.height = 768;
        new LwjglApplication(new RenderMeshWithDefaultShader_meshPartCreation(), cfg);
    }

    @Override
    public void create() {
        TEXTURE = new Texture(Assets.IMAGES_FOR_ATLAS_FOLDER + "/grass_128.png");

        camera = new PerspectiveCamera(
                75,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());

        camera.position.set(-5f, 5f, 7f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 300.0f;
        modelBatch = new ModelBatch();

        Mesh mesh1x1 = getMeshOneXOne();
        Mesh mesh2x1 = getMeshTwoXOneQuad();

        modelInstance1 = createModelInstanceFromMesh(mesh1x1, new Material(TextureAttribute.createDiffuse(TEXTURE)));
        modelInstance2 = createModelInstanceFromMesh(mesh2x1, new Material(TextureAttribute.createDiffuse(TEXTURE)));
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
        Gdx.input.setInputProcessor(this);
    }

    private Mesh getMeshOneXOne() {
        float[] verts = {
                2.0f, -5.0f, 1.0f, /*Tex*/ 0, 1,
                1.0f, -5.0f, 1.0f, /*Tex*/ 1, 1,
                2.0f, -5.0f, 2.0f, /*Tex*/ 1, 0,
                1.0f, -5.0f, 2.0f, /*Tex*/ 0, 0,

        };
        short[] inds = {0, 1, 2, 2, 1, 3};
        Mesh mesh = new Mesh(false, verts.length, inds.length, VertexAttribute.Position(),
                VertexAttribute.TexCoords(0));
        mesh.setVertices(verts);
        mesh.setIndices(inds);
        return mesh;
    }

    private Mesh getMeshTwoXOneQuad() {
        float[] verts = {
                2.0f, 0.0f, 1.0f,  /*Tex*/ 0, 1,
                1.0f, 0.0f, 1.0f,  /*Tex*/ 1, 1,
                2.0f, 0.0f, 2.0f,  /*Tex*/ 1, 0,
                1.0f, 0.0f, 2.0f,  /*Tex*/ 0, 0,
                3.0f, 0.0f, 1.0f,  /*Tex*/ 0, 1,
                2.0f, 0.0f, 1.0f,  /*Tex*/ 1, 1,
                3.0f, 0.0f, 2.0f,  /*Tex*/ 1, 0,
                2.0f, 0.0f, 2.0f,  /*Tex*/ 0, 0,
        };
        short[] inds = {0, 1, 2, 2, 1, 3, 4, 5, 6, 6, 5, 7};
        Mesh mesh = new Mesh(false, verts.length, inds.length, VertexAttribute.Position(),
                VertexAttribute.TexCoords(0));
        mesh.setVertices(verts);
        mesh.setIndices(inds);
        return mesh;
    }

    public static ModelInstance createModelInstanceFromMesh(Mesh mesh, Material material) {
        Node node = new Node();

        MeshPart meshPart = new MeshPart("createModelInstanceFromMesh", mesh, 0,
                // switch these lines to see difference
//                mesh.getNumVertices()
                mesh.getMaxVertices()
                , GL20.GL_TRIANGLES);
        NodePart nodePart = new NodePart(meshPart, material);
        node.parts.add(nodePart);

        Model model = new Model();
        model.nodes.add(node);

        return new ModelInstance(model);
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camera.update();

        modelBatch.begin(camera);
        modelBatch.render(modelInstance1, environment);
        if (modelInstance2 != null) {
            modelBatch.render(modelInstance2, environment);
        }
        modelBatch.end();

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public boolean keyDown(int keycode) {
        // 
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // 
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // If the user hits a key, take a screen shot.
        boolean screenShot = true;
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // 
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // 
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // 
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // 
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // 
        return false;
    }
}
