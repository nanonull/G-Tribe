package conversion7.engine.geometry.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import conversion7.engine.Gdxg;
import conversion7.game.Assets;

public class TerrainShader extends BaseShader {

    public final int u_normalMatrix;
    private ColorAttribute ambientColorAttribute;
    private DirectionalLight sunLight;

    Texture texture0;
    Texture texture1;
    Texture texture2;

    public TerrainShader(Renderable renderable) {
        super();

        texture0 = new Texture(Assets.IMAGES_FOR_ATLAS_FOLDER + "/grass_128.png");
        texture1 = new Texture(Assets.IMAGES_FOR_ATLAS_FOLDER + "/sand_128.png");
        texture2 = new Texture(Assets.IMAGES_FOR_ATLAS_FOLDER + "/stone_128.png");
        sunLight = Gdxg.graphic.getSunLight();
        ambientColorAttribute = Gdxg.graphic.getAmbientColorAttribute();

        program = new ShaderProgram(
                Gdx.files.internal(Assets.SHADERS_FOLDER + "/terrain.vertex"),
                Gdx.files.internal(Assets.SHADERS_FOLDER + "/terrain.fragment")
        );

        u_normalMatrix = register(DefaultShader.Inputs.normalMatrix, DefaultShader.Setters.normalMatrix);
    }

    public static boolean isApplicableTo(Renderable renderable) {
        return renderable.material.has(TerrainAttribute.ID);
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);
        context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f);
        context.setDepthMask(true);
        program.setUniformMatrix("u_model_view", camera.combined);
        program.setUniformf("u_directLightColor",
                sunLight.color.r,
                sunLight.color.g,
                sunLight.color.b);
        program.setUniformf("u_directLightDirection",
                sunLight.direction.x,
                sunLight.direction.y,
                sunLight.direction.z);
        program.setUniformf("u_ambientColor",
                ambientColorAttribute.color.r,
                ambientColorAttribute.color.g,
                ambientColorAttribute.color.b);
    }

    @Override
    public void render(Renderable renderable) {
        super.render(renderable);

        texture0.bind(0);
        texture1.bind(1);
        texture2.bind(2);
        program.begin();
        program.setUniformMatrix("u_world_trans", renderable.worldTransform);
        program.setUniformi("Texture0", 0);
        program.setUniformi("Texture1", 1);
        program.setUniformi("Texture2", 2);
        renderable.mesh.render(program, renderable.primitiveType);
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
        return isApplicableTo(instance);
    }

    @Override
    public void dispose() {
        super.dispose();
        program.dispose();
    }

}
