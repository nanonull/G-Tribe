package conversion7.tests_standalone.applications.mesh_part_issue;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Camera;
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
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import org.testng.Assert;

public class RenderMeshWithSimpleShaderTest implements ApplicationListener, InputProcessor {

    private static Texture TEXTURE;
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Model model;
    private ModelInstance modelInstance;
    private Environment environment;

    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "RenderModelInstanceTest";
        cfg.vSyncEnabled = true;
        cfg.width = GdxgConstants.SCREEN_WIDTH_IN_PX;
        cfg.height = GdxgConstants.SCREEN_HEIGHT_IN_PX;
        new LwjglApplication(new RenderMeshWithSimpleShaderTest(), cfg);
    }

    @Override
    public void create() {
        // add your image here
        TEXTURE = new Texture(Assets.IMAGES_FOR_ATLAS_FOLDER + "/grass_128.png");

        camera = new PerspectiveCamera(
                75,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());

        camera.position.set(-5f, 5f, 7f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 300.0f;
        // DIFFERENCE #1
        modelBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                return new SimpleShaderProgram(renderable);
            }
        });

        //

        Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.TexCoords(0));
        mesh.setVertices(new float[]{
                -0.5f, -0.5f, 0, 0, 1,
                0.5f, -0.5f, 0, 1, 1,
                0.5f, 0.5f, 0, 1, 0,
                -0.5f, 0.5f, 0, 0, 0});
        mesh.setIndices(new short[]{0, 1, 2, 2, 3, 0});

        Node node = new Node();

        MeshPart meshPart = new MeshPart("meshPart", mesh, 0, mesh.getNumVertices(), GL20.GL_TRIANGLES);
        NodePart nodePart = new NodePart(meshPart, new Material(TextureAttribute.createDiffuse(TEXTURE)));
        node.parts.add(nodePart);

        Model model = new Model();
        model.nodes.add(node);

        //

        modelInstance = new ModelInstance(model);
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
        Gdx.input.setInputProcessor(this);
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
        modelBatch.render(modelInstance, environment);
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
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
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
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    ///
    public class SimpleShaderProgram extends BaseShader {

        private final String vert = "attribute vec4 a_position;\n" +
                "attribute vec2 a_texCoord0;\n" +
                "\n" +
                "uniform mat4 u_model_view;\n" +
                "uniform mat4 u_world_trans;\n" +
                "\n" +
                "varying vec2 texCoords;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "  texCoords = a_texCoord0;\n" +
                "  gl_Position = u_model_view * u_world_trans * a_position;\n" +
                "}";
        private final String frag = "uniform sampler2D Texture0;\n" +
                "\n" +
                "varying vec2 texCoords;\n" +
                "\n" +
                "//-------------------\n" +
                "void main()\n" +
                "{\n" +
                " vec4 texel0;\n" +
                " //-------------------\n" +
                " texel0 = texture2D(Texture0, texCoords);\n" +
                "\n" +
                " gl_FragColor = texel0;\n" +
                "}";
        Texture texture0;

        public SimpleShaderProgram(Renderable renderable) {
            super();

            texture0 = TEXTURE;

            program = new ShaderProgram(vert, frag);

            if (!program.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader " + program.getLog());
            String log = program.getLog();
            if (log.length() > 0) Gdx.app.error("TerrainShader", "Shader compilation log: " + log);
        }

        @Override
        public void begin(Camera camera, RenderContext context) {
            super.begin(camera, context);
            program.setUniformMatrix("u_model_view", camera.combined);
        }

        @Override
        public void render(Renderable renderable) {
//            super.render(renderable);

            texture0.bind(0);
            program.begin();
            program.setUniformMatrix("u_world_trans", renderable.worldTransform);
            program.setUniformi("Texture0", 0);
            renderable.meshPart.mesh.render(program, renderable.meshPart.primitiveType);
            program.end();
        }

        @Override
        public void end() {
            super.end();
        }

        @Override
        public void init() {
            super.init(program, null);
        }

        @Override
        public int compareTo(Shader other) {
            return 0;
        }


        @Override
        public boolean canRender(Renderable instance) {
            return true;
        }

        @Override
        public void dispose() {
            super.dispose();
            program.dispose();
        }

    }
}
