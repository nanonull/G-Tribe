package conversion7.engine.geometry;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import conversion7.engine.customscene.DecalActor;
import conversion7.engine.utils.MathUtils;

public class BoundingBox2 {

    /**
     * In engine coordinates
     */
    public Vector3 min;
    /**
     * In engine coordinates
     */
    public Vector3 max;
    /**
     * In engine coordinates
     */
    public Vector3 sizes = new Vector3();

    /**
     * In engine coordinates
     */
    private Vector3 halfSizes;

    private Vector3 corner000 = new Vector3();
    private Vector3 corner001 = new Vector3();
    private Vector3 corner010 = new Vector3();
    private Vector3 corner011 = new Vector3();
    private Vector3 corner100 = new Vector3();
    private Vector3 corner101 = new Vector3();
    private Vector3 corner110 = new Vector3();
    private Vector3 corner111 = new Vector3();

    public BoundingBox wrappedBox;

    /**
     * In engine coordinates
     */
    public BoundingBox2(Vector3 minimum, Vector3 maximum) {
        calculateOwnDimensions(minimum, maximum);
        createBox();
    }

    private void calculateOwnDimensions(Vector3 minimum, Vector3 maximum) {
        min = minimum;
        max = maximum;
        sizes.set(max).sub(min);
    }

    public BoundingBox2(final BoundingBox boundingBox) {
        wrappedBox = boundingBox;
        calculateOwnDimensions(wrappedBox.min, wrappedBox.max);
    }

    public BoundingBox2(final DecalActor decalActor) {

        Vector3 center = decalActor.globalPosition;

        float halfWidthX = decalActor.decal.getWidth() / 2;
        float halfWidthY = decalActor.decal.getHeight() / 2;
        float halfHeight = 0.00001f;

        min = new Vector3(center.x - halfWidthX,
                center.y - halfHeight,
                center.z + halfWidthY);

        max = new Vector3(center.x + halfWidthX,
                center.y + halfHeight,
                center.z - halfWidthY);

        calculateOwnDimensions(min, max);

        createBox();
    }

    /**
     * Game coords<br>
     * starts from 0 vector
     */
    public BoundingBox2(float widthX, float widthY, float heightZ) {
        this(new Vector3(), widthX, widthY, heightZ);
    }

    /**
     * minimum - in engine coordinates<br>
     * pluses - in game coordinates
     */
    public BoundingBox2(Vector3 minimum, float plusX, float plusY, float plusZ) {
        this(minimum, new Vector3(minimum).add(MathUtils.toEngineCoords(plusX, plusY, plusZ)));
    }

    /**
     * minimum - in engine coordinates<br>
     * pluses - in game coordinates
     */
    public BoundingBox2(float minX, float minY, float minZ, float plusX, float plusY, float plusZ) {
        this(new Vector3(minX, minY, minZ), plusX, plusY, plusZ);
    }

    private void createBox() {
        wrappedBox = new BoundingBox(min, max);
    }

    /**
     * in game coordinates
     */
    public void translate(float x, float y, float z) {
        min.add(MathUtils.toEngineCoords(x, y, z));
        max.add(MathUtils.toEngineCoords(x, y, z));
        wrappedBox.set(min, max);
    }

    /**
     * Could be used for move after set<p>
     * * in game coordinates<br>
     * center of the box
     */
    public void setPosition(float x, float y, float z) {
        getHalfSizes();
        min.set(x - halfSizes.x, y - halfSizes.y, z + halfSizes.z);
        max.set(min).add(sizes.x, sizes.y, -sizes.z);
        wrappedBox.set(min, max);
    }

    public Vector3 getHalfSizes() {
        if (halfSizes == null) {
            halfSizes = new Vector3(sizes.x / 2, sizes.y / 2, sizes.z / 2);
        }
        return halfSizes;
    }

    public Vector3 getCorner000() {
        return wrappedBox.getCorner000(corner000);
    }

    public Vector3 getCorner001() {
        return wrappedBox.getCorner001(corner001);
    }

    public Vector3 getCorner010() {
        return wrappedBox.getCorner010(corner010);
    }

    public Vector3 getCorner011() {
        return wrappedBox.getCorner011(corner011);
    }

    public Vector3 getCorner100() {
        return wrappedBox.getCorner100(corner100);
    }

    public Vector3 getCorner101() {
        return wrappedBox.getCorner101(corner101);
    }

    public Vector3 getCorner110() {
        return wrappedBox.getCorner110(corner110);
    }

    public Vector3 getCorner111() {
        return wrappedBox.getCorner111(corner111);
    }
}
