package conversion7.engine.geometry.terrain;

import com.badlogic.gdx.math.Interpolation;
import conversion7.engine.utils.Normalizer;
import conversion7.engine.utils.NormalizerUtil;
import conversion7.game.stages.world.landscape.Landscape;
import conversion7.game.stages.world.landscape.Soil;

public class TerrainVertexData {

    private SoilData soil = new SoilData();
    private float height;
    private Landscape landscape;

    /** Cell's origin vertex */
    public TerrainVertexData(Landscape landscape) {
        this.landscape = landscape;
        calcHeight();
        soil.dirt = (float) Normalizer.normalize(landscape.soil.getSoilTypeValue(Soil.TypeId.DIRT), 100, 0, 1, 0);
        soil.sand = (float) Normalizer.normalize(landscape.soil.getSoilTypeValue(Soil.TypeId.SAND), 100, 0, 1, 0);
        soil.stone = (float) Normalizer.normalize(landscape.soil.getSoilTypeValue(Soil.TypeId.STONE), 100, 0, 1, 0);
    }

    public void calcHeight() {
        height = landscape.getLevel() / 2.5f;
    }

    /** Interpolated vertex */
    public TerrainVertexData() {
    }

    public TerrainVertexData(TerrainVertexData copyFrom) {
        soil.set(copyFrom.soil);
        height = copyFrom.height;
    }

    public SoilData getSoil() {
        return soil;
    }

    public float getHeight() {
        return height;
    }

    public void append(TerrainVertexData other, double alpha) {
        soil.append(other.soil, alpha);
        height += other.height * alpha;
    }

    public void append(TerrainVertexData other) {
        soil.append(other.soil);
        height += other.height;
    }

    public void minus(TerrainVertexData other) {
        soil.minus(other.soil);
        height -= other.height;
    }

    public void multiply(float onValue) {
        soil.multiply(onValue);
        height *= onValue;
    }

    public TerrainVertexData divide(int onValue) {
        soil.divide(onValue);
        height /= onValue;
        return this;
    }

    public void interpolateWithNeighbors(TerrainVertexData interpWith, float distanceToMainOrigin,
                                         float maxDistanceToMainOrigin) {
        float interpolatedDst = (distanceToMainOrigin / maxDistanceToMainOrigin) * maxDistanceToMainOrigin;
        float neighborAlpha = interpolatedDst / maxDistanceToMainOrigin;
        neighborAlpha /= TerrainChunk.NEIGHBOR_AFFECT_DECREASE_MODIFIER;

        height = Interpolation.linear.apply(height, interpWith.height, neighborAlpha);
        soil.dirt = Interpolation.linear.apply(soil.dirt, interpWith.soil.dirt, neighborAlpha);
        soil.sand = Interpolation.linear.apply(soil.sand, interpWith.soil.sand, neighborAlpha);
        soil.stone = Interpolation.linear.apply(soil.stone, interpWith.soil.stone, neighborAlpha);
        soil.reserved = Interpolation.linear.apply(soil.reserved, interpWith.soil.reserved, neighborAlpha);
    }

}
