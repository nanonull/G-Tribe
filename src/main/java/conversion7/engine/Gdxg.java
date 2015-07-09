package conversion7.engine;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.bitfire.postprocessing.PostProcessor;
import conversion7.engine.utils.Utils;
import conversion7.game.ui.ClientUi;
import org.slf4j.Logger;

/**
 * God-object storage for links on all interfaces
 */
public class Gdxg {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static ClientGraphic graphic;
    public static ClientUi clientUi;

    // LibGdx
    public static ImmediateModeRenderer20 glPrimitiveRenderer;
    public static ModelBuilder modelBuilder;
    public static SpriteBatch spriteBatch;
    public static DecalBatch decalBatchCommon;
    public static DecalBatch decalBatchTransparentLayer;
    public static ModelBatch modelBatch;
    public static ModelBatch terrainBatch;

    // external
    public static TweenManager tweenManager = new TweenManager();
    public static PostProcessor postProcessor;
    @Deprecated
    public static ShaderManager shaders;
    public static final Engine ENTITY_SYSTEMS_ENGINE = new Engine();

}
