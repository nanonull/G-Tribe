package conversion7.engine;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.bitfire.postprocessing.PostProcessor;
import conversion7.game.stages.world.view.AreaViewer;
import conversion7.game.ui.ClientUi;

public class Gdxg {
    public static ClientCore core;
    public static ClientGraphic graphic;
    public static ClientUi clientUi;
    public static ImmediateModeRenderer20 glPrimitiveRenderer;
    public static ModelBuilder modelBuilder;
    public static SpriteBatch spriteBatch;
    public static DecalBatch decalBatchCommon;
    public static DecalBatch decalBatchTransparentLayer;
    public static ModelBatch modelBatch;
    public static ModelBatch terrainBatch;
    public static TweenManager tweenManager = new TweenManager();
    public static PostProcessor postProcessor;
    @Deprecated
    public static ShaderManager shaders;

    public static AreaViewer getAreaViewer() {
        return core.areaViewer;
    }
}
