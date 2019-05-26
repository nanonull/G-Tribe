package conversion7.engine.geometry.water;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import conversion7.game.Assets;
import conversion7.game.stages.world.view.AreaViewer;

public class Water {

    /** Set bigger to cull more */
    private static final float CULLED_VIEW_SPACE_MAGIC = 1.4f;
    public static final float VIEWER_BACK_OFFSET = 14;

    private float waterAnimationSpeed = 0.05f;
    private float amplitudeWave = 0.1f;
    private float angleWave = 0.0f;
    private float angleWaveSpeed = 2.0f;
    private float alpha = 0.7f;
    private float mix = 0.6f;
    private Animation<TextureRegion> animation;
    private Material waterMaterial;

    private int planeWidthInTiles;
    private int planeHeightInTiles;
    private int planeWidth;
    private int planeHeight;
    private int waterTileSize;

    // TODO create full set of animation frames
    public Water() {

        this.animation = new Animation<>(waterAnimationSpeed, Assets.liquidAtlas.findRegions(Assets.WATER_REGIONS));
        this.animation.setPlayMode(Animation.PlayMode.LOOP);
        this.waterMaterial = new Material(TextureAttribute.createDiffuse(getTexture()), new WaterAttribute());

        waterTileSize = 5;
        planeWidthInTiles = (int) (AreaViewer.WIDTH_IN_CELLS / waterTileSize / CULLED_VIEW_SPACE_MAGIC);
        planeHeightInTiles = (int) (AreaViewer.HEIGHT_IN_CELLS / waterTileSize / CULLED_VIEW_SPACE_MAGIC);

        planeWidth = planeWidthInTiles * waterTileSize;
        planeHeight = planeHeightInTiles * waterTileSize;
    }

    public Material getMaterial() {
        return waterMaterial;
    }

    public float getAlpha() {
        return alpha;
    }

    public float getMix() {
        return mix;
    }

    public float getAmplitudeWave() {
        return amplitudeWave;
    }

    public float getAngleWave() {
        return angleWave;
    }

    public float getAngleWaveSpeed() {
        return angleWaveSpeed;
    }

    public TextureRegion getCurrentRegion() {
//        return this.animation.getKeyFrame(0);
        return this.animation.getKeyFrame(angleWave);
    }

    public Texture getTexture() {
        return this.animation.getKeyFrame(0).getTexture();
    }

    public TextureDescriptor getWaterTextureId() {
        TextureAttribute textureAttr = (TextureAttribute) waterMaterial.get(TextureAttribute.Diffuse);
        return textureAttr.textureDescription;
    }

    public int getPlaneHeight() {
        return planeHeight;
    }

    public int getPlaneWidth() {
        return planeWidth;
    }

    public int getWaterTileSize() {
        return waterTileSize;
    }

    public int getPlaneWidthInTiles() {
        return planeWidthInTiles;
    }

    public int getPlaneHeightInTiles() {
        return planeHeightInTiles;
    }

    public void update(float delta) {
        // no delta to avoid lags on new area shift
        this.angleWave += 0.01f * angleWaveSpeed;
    }
}
