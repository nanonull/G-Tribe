package conversion7.engine.geometry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;
import conversion7.engine.Gdxg;
import conversion7.game.Assets;

public class SkyBox implements Disposable {

    public static final String SKYBOX_SHADER = "skybox";
    private static final String CUBEMAP_UNIFORM = "s_cubemap";
    private static final String MODELVIEW_UNIFORM = "u_mvpMatrix";
    private CubeMap cubeMap;
    private Mesh mesh;
    private Matrix4 invView;
    private Matrix4 mvp;

    public SkyBox() {
        cubeMap = new CubeMap(Assets.IMAGES_FOLDER + "skybox/mono512");
        mesh = SkyBox.genSkyBoxMesh();
        invView = new Matrix4();
        mvp = new Matrix4();
    }

    public static Mesh genSkyBoxMesh() {
        Mesh mesh = new Mesh(true, 8, 14, new VertexAttribute(Usage.Position, 3, "a_position"));

        float[] cubeVerts = {-1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f,};

        short[] indices = {0, 1, 2, 3, 7, 1, 5, 4, 7, 6, 2, 4, 0, 1};

        mesh.setVertices(cubeVerts);
        mesh.setIndices(indices);

        return mesh;
    }

    public void render(PerspectiveCamera camera) {
        invView.set(camera.view);

        invView.val[Matrix4.M03] = 0;
        invView.val[Matrix4.M13] = 0;
        invView.val[Matrix4.M23] = 0;

        invView.inv().tra();

        mvp.set(camera.projection);
        mvp.mul(invView);

        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_FRONT);
        Gdx.gl.glFrontFace(GL20.GL_CCW);

        Gdx.gl20.glDisable(GL20.GL_BLEND);
        Gdx.gl20.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl20.glDepthMask(false);

        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
        Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, cubeMap.getTextureId());

        Gdxg.shaders.begin(SKYBOX_SHADER);

        Gdxg.shaders.setUniformMatrix(MODELVIEW_UNIFORM, mvp);
        Gdxg.shaders.setUniformi(CUBEMAP_UNIFORM, 0);

        mesh.render(Gdxg.shaders.getCurrent(), GL20.GL_TRIANGLE_STRIP);

        Gdxg.shaders.end();

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);
    }

    @Override
    public void dispose() {
        this.mesh.dispose();
        this.cubeMap.dispose();
    }

    public CubeMap getCubeMap() {
        return this.cubeMap;
    }
}
