package conversion7.engine.geometry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.MathUtils;
import conversion7.game.Assets;

public class NoiseCubeShader extends DefaultShader {

    private ColorAttribute ambientColorAttribute;
    private DirectionalLight sunLight;

    public NoiseCubeShader(Renderable renderable) {
        super(renderable, new Config(null,
                Gdx.files.internal(Assets.SHADERS_FOLDER + "/cube_noise.fragment").readString()
        ));


        sunLight = Gdxg.graphic.getSunLight();
        ambientColorAttribute = Gdxg.graphic.getAmbientColorAttribute();


        register("time");
        register("resolution");

    }

    public static boolean isApplicableTo(Renderable renderable) {
        return renderable.material.has(NoiseCubeAttribute.ID);
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);

        program.setUniformf("time", MathUtils.random(0, 255) / 255f);
        program.setUniformf("resolution", 10, 10);

    }


//    @Override
//    public void end() {
//        super.end();
//    }

//    @Override
//    public void init() {
//        super.init(program, null);
//    }

//    @Override
//    public int compareTo(Shader other) {
//        return 0;
//    }


    @Override
    public boolean canRender(Renderable instance) {
        return isApplicableTo(instance);
    }


}
