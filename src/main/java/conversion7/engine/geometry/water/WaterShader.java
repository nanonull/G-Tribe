package conversion7.engine.geometry.water;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import conversion7.engine.Gdxg;
import conversion7.engine.geometry.SkyBox;
import conversion7.game.Assets;

// TODO rebase on libgdx shaderProvider and remove shader-manager
public class WaterShader extends BaseShader {

    public static final String SHADER_NAME = "water";
    private static final String UNIFORM_MODEL_VIEW = "u_model_view";
    private static final String U_WORLD_TRANS = "u_world_trans";
    private static final String UNIFORM_TEXTURE_ID = "u_texture";
    private static final String UNIFORM_WAVE_DATA = "u_wave_data";

    private static final String UNIFORM_TEXTURE_CORDINATES = "u_texture_cordinates";
    private static final String UNIFORM_CAMERA_POSITION = "u_camera_position";
    private static final String UNIFORM_WATER_ALPHA = "u_water_alpha";
    private static final String UNIFORM_WATER_MIX = "u_water_mix";

    public WaterShader(Renderable renderable) {
        super();
        program = new ShaderProgram(
                Gdx.files.internal(Assets.SHADERS_FABULA_FOLDER + "/water.vertex"),
                Gdx.files.internal(Assets.SHADERS_FABULA_FOLDER + "/water.fragment")
        );
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);
        Water water = Gdxg.graphic.water;
        SkyBox sky = Gdxg.graphic.skyBox;

        context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);
        if (sky != null) {
            Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
            Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, sky.getCubeMap().getTextureId());
        }

        TextureRegion region = water.getCurrentRegion();

        program.setUniformMatrix(UNIFORM_MODEL_VIEW, camera.combined);
//        Gdxg.shaders.setUniformf(UNIFORM_WAVE_DATA, water.getAngleWave(), water.getAmplitudeWave());
        program.setUniformi(UNIFORM_TEXTURE_ID, context.textureBinder.bind(water.getWaterTextureId()));
        program.setUniformf(UNIFORM_TEXTURE_CORDINATES, region.getU(), region.getV(), region.getU2(), region.getV2());
        program.setUniformf(UNIFORM_CAMERA_POSITION, camera.position.x, camera.position.y, camera.position.z);
        program.setUniformf(UNIFORM_WATER_ALPHA, water.getAlpha());
        program.setUniformf(UNIFORM_WATER_MIX, water.getMix());
    }

    @Override
    public boolean canRender(Renderable renderable) {
        return isApplicableTo(renderable);
    }

    @Override
    public void render(Renderable renderable) {
        program.setUniformMatrix(U_WORLD_TRANS, renderable.worldTransform);
        renderable.mesh.render(program, renderable.primitiveType);
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
    public void dispose() {
        super.dispose();
        program.dispose();
    }


    public static boolean isApplicableTo(Renderable renderable) {
        return renderable.material.has(WaterAttribute.ID);
    }
}
