package conversion7.engine.customscene;


import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Disposable;

/** Libgdx 3d scene's modified class */
public class Actor3d extends ModelInstance implements Disposable {

    private static final Vector3 position = new Vector3();
    public final Vector3 center = new Vector3();
    public final Vector3 dimensions = new Vector3();
    private BoundingBox boundBox = new BoundingBox();
    public final float radius;
    private String name;
    private boolean visible = true;
    float x, y, z;
    float scaleX = 1, scaleY = 1, scaleZ = 1;
    float yaw = 0f, pitch = 0f, roll = 0f;
    Matrix4 rotationMatrix = new Matrix4();
    private AnimationController animation;
    public boolean animating = false;

    public Actor3d() {
        this(new Model());
        setScale(0, 0, 0);
    }

    public Actor3d(Model model) {
        this(model, 0f, 0f, 0f);
    }

    public Actor3d(Model model, float x, float y, float z) {
        super(model);
        setPosition(x, y, z);
        //boundBox = model.meshes.get(0).calculateBoundingBox();
        calculateBoundingBox(boundBox);
        boundBox.getCenter(center);
        boundBox.getDimensions(dimensions);
        radius = dimensions.len() / 2f;
        animation = new AnimationController(this);
    }

    public boolean isVisible() {
        return visible;

    }

    /** If false, the actor3d will not be drawn and will not receive touch events. Default is true. */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Matrix4 getTransform() {
        return transform;
    }

    public void setTransform(Matrix4 transform) {
        this.transform = transform;
    }

    public BoundingBox getBoundingBox() {
        return boundBox;
    }

    public void setBoundingBox(BoundingBox box) {
        boundBox = box;
    }

    public AnimationController getAnimation() {
        return animation;
    }

    public float getYaw() {
        return yaw;
    }

    /*
     *  Set the actor3d's yaw
     *  @param newYaw value must be within 360 degrees
     */
    public void setYaw(float newYaw) {
        yaw = newYaw;
        rotationMatrix = transform.setFromEulerAngles(yaw, pitch, roll).cpy();
        transform.setToTranslationAndScaling(x, y, z, scaleX, scaleY, scaleZ);
        transform.mul(rotationMatrix);
    }

    public float getPitch() {
        return pitch;
    }

    /*
     *  Set the actor3d's pitch
     *  @param newPitch value must be within 360 degrees
     */
    public void setPitch(float newPitch) {
        pitch = newPitch;
        rotationMatrix = transform.setFromEulerAngles(yaw, pitch, roll).cpy();
        transform.setToTranslationAndScaling(x, y, z, scaleX, scaleY, scaleZ);
        transform.mul(rotationMatrix);
    }

    public float getRoll() {
        return roll;
    }

    /*
     *  Set the actor3d's roll
     *  @param newRoll value must be within 360 degrees
     */
    public void setRoll(float newRoll) {
        roll = newRoll;
        rotationMatrix = transform.setFromEulerAngles(yaw, pitch, roll).cpy();
        transform.setToTranslationAndScaling(x, y, z, scaleX, scaleY, scaleZ);
        transform.mul(rotationMatrix);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
        transform.setToTranslation(x, y, z);
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
        transform.setToTranslation(x, y, z);
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
        transform.setToTranslation(x, y, z);
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
        transform.scale(scaleX, scaleY, scaleZ);
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
        transform.scale(scaleX, scaleY, scaleZ);
    }

    public float getScaleZ() {
        return scaleZ;
    }

    public void setScaleZ(float scaleZ) {
        this.scaleY = scaleZ;
        transform.scale(scaleX, scaleY, scaleZ);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

    }

    public Color getColor() {
        return ((ColorAttribute) getMaterial("Color").get(ColorAttribute.Diffuse)).color;
    }

    public void setColor(Color color) {
        ColorAttribute ca = new ColorAttribute(ColorAttribute.Diffuse, color);
        if (getMaterial("Color") != null)
            getMaterial("Color").set(ca);
        else
            materials.add(new Material("Color", ca));
        model.materials.add(new Material("Color", ca));
    }

    public void setScale(float scale) {
        this.scaleX = scale;
        this.scaleY = scale;
        this.scaleZ = scale;
        transform.setToScaling(scaleX, scaleY, scaleZ);
    }

    public static float normalizeDegrees(float degrees) {
        float newAngle = degrees;
        while (newAngle < -360) newAngle += 360;
        while (newAngle > 360) newAngle -= 360;
        return newAngle;
    }

    public void doAnimate(float delta) {
        if (animating) {
            animation.update(delta);
        }
    }

    public boolean isCullable(final Camera cam) {
        return cam.frustum.sphereInFrustum(getTransform().getTranslation(position).add(center), radius);
    }

    public void rotateYaw(float amountYaw) {
        yaw = normalizeDegrees(yaw + amountYaw);
        rotationMatrix = transform.setFromEulerAngles(yaw, pitch, roll).cpy();
        transform.setToTranslationAndScaling(x, y, z, scaleX, scaleY, scaleZ);
        transform.mul(rotationMatrix);
    }

    public void rotatePitch(float amountPitch) {
        pitch = normalizeDegrees(pitch + amountPitch);
        rotationMatrix = transform.setFromEulerAngles(yaw, pitch, roll).cpy();
        transform.setToTranslationAndScaling(x, y, z, scaleX, scaleY, scaleZ);
        transform.mul(rotationMatrix);
    }

    public void rotateRoll(float amountRoll) {
        roll = normalizeDegrees(roll + amountRoll);
        rotationMatrix = transform.setFromEulerAngles(yaw, pitch, roll).cpy();
        transform.setToTranslationAndScaling(x, y, z, scaleX, scaleY, scaleZ);
        transform.mul(rotationMatrix);
    }

    public void setScale(float scaleX, float scaleY, float scaleZ) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        transform.setToScaling(scaleX, scaleY, scaleZ);
    }

    /** Adds the specified scale to the current scale. */
    public void scale(float scale) {
        scaleX += scale;
        scaleY += scale;
        scaleZ += scale;
        transform.scl(scale); // re-implement this
    }

    public void scale(float scaleX, float scaleY, float scaleZ) {
        this.scaleX += scaleX;
        this.scaleY += scaleY;
        this.scaleZ += scaleZ;
        transform.scl(scaleX, scaleY, scaleZ); // re-implement this
    }

    /**
     * @return -1 on no intersection, or when there is an intersection: the squared distance between the center of this
     * object and the point on the ray closest to this object when there is intersection.
     */
    public float intersects(Ray ray) {
        transform.getTranslation(position).add(center);
        final float len = ray.direction.dot(position.x - ray.origin.x, position.y - ray.origin.y, position.z - ray.origin.z);
        if (len < 0f)
            return -1f;
        float dist2 = position.dst2(ray.origin.x + ray.direction.x * len, ray.origin.y + ray.direction.y * len, ray.origin.z + ray.direction.z * len);
        return (dist2 <= radius * radius) ? dist2 : -1f;
    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        transform.setToTranslationAndScaling(this.x, this.y, this.z, scaleX, scaleY, scaleZ);
        transform.mul(rotationMatrix);
    }

    public void translate(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        transform.setToTranslationAndScaling(this.x, this.y, this.z, scaleX, scaleY, scaleZ);
        transform.mul(rotationMatrix);
    }

    /*
     *  Set the actor3d's rotation values to new yaw, pitch and roll
     *  @param newYaw, newPitch, newRoll these values must be within 360 degrees
     */
    public void setRotation(float newYaw, float newPitch, float newRoll) {
        yaw = newYaw;
        pitch = newPitch;
        roll = newRoll;
        rotationMatrix = transform.setFromEulerAngles(yaw, pitch, roll).cpy();
        transform.setToTranslationAndScaling(x, y, z, scaleX, scaleY, scaleZ);
        transform.mul(rotationMatrix);
    }

    /*
     *  Rotates the actor3d by the amount of yaw, pitch and roll
     *  @param amountYaw,amountPitch,amountRoll These values must be within 360 degrees
     */
    public void rotate(float amountYaw, float amountPitch, float amountRoll) {
        yaw = normalizeDegrees(yaw + amountYaw);
        pitch = normalizeDegrees(pitch + amountPitch);
        roll = normalizeDegrees(roll + amountRoll);
        rotationMatrix = transform.setFromEulerAngles(yaw, pitch, roll).cpy();
        transform.setToTranslationAndScaling(x, y, z, scaleX, scaleY, scaleZ);
        transform.mul(rotationMatrix);
    }

    @Override
    public String toString() {
        String name = this.name;
        if (name == null) {
            name = getClass().getName();
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex != -1) name = name.substring(dotIndex + 1);
        }
        return name;
    }

    @Override
    public void dispose() {
        model.dispose();
    }
}