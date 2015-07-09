package conversion7.scene3dOld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public class Camera3d extends PerspectiveCamera {
    private static Camera3d instance;

    Camera3d() {
        super(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        position.set(10f, 10f, 10f);
        lookAt(0, 0, 0);
        near = 0.1f;
        far = 300f;
        update();
        instance = this;
    }

    private static float offsetX = 10f, offsetY = 10f, offsetZ = 10f;
    private static float folllowSpeed = 0.5f;
    private static Actor3d followedActor3d;
    private static boolean lookAt;

    /*
     * The camera follows the actor3d as it moves along the scene
     * @param actor3d The actor3d the camera has to follow , if it is null the camera stops following
     * @param lookAt whether the camera should always be pointing to the actor3d
     */
    public static void followActor3d(Actor3d actor3d, boolean la) {
        followedActor3d = actor3d;
        lookAt = la;
    }

    /*
     * This sets the distance between the camera and the actor
     * @param offX the x distance from actor
     * @param offY the y distance from actor
     * @param offZ the z distance from actor
     */
    public static void followOffset(float offX, float offY, float offZ) {
        offsetX = offX;
        offsetY = offY;
        offsetZ = offZ;
    }

    private static float moveDuration;
    private static float moveTime;
    private static boolean moveCompleted;
    private static float moveLastPercent;
    private static float panSpeedX, panSpeedY, panSpeedZ;
    private static float movePercentDelta;

    private static float rotateTime;
    private static float rotateDuration;
    private static float rotateYaw, rotatePitch, rotateRoll;
    private static boolean rotateCompleted;
    private static float rotateLastPercent;
    private static float rotatePercentDelta;

    public static void moveTo(float x, float y, float z, float duration) {
        moveBy(x - instance.position.x, y - instance.position.y, y - instance.position.y, duration);
    }

    public static void moveBy(float amountX, float amountY, float amountZ, float duration) {
        moveDuration = duration;
        panSpeedX = amountX;
        panSpeedY = amountY;
        panSpeedZ = amountZ;
        moveLastPercent = 0;
        moveTime = 0;
        moveCompleted = false;
    }

    public static void rotateBy(float yaw, float pitch, float roll, float duration) {
        rotateLastPercent = 0;
        rotateTime = 0;
        rotateYaw = yaw;
        rotatePitch = pitch;
        rotateRoll = roll;
        rotateDuration = duration;
        rotateCompleted = false;
    }

    @Override
    public void update() {
        super.update();
        float delta = Gdx.graphics.getDeltaTime();
        if (!moveCompleted) {
            moveTime += delta;
            moveCompleted = moveTime >= moveDuration;
            float percent;
            if (moveCompleted)
                percent = 1;
            else {
                percent = moveTime / moveDuration;
            }
            movePercentDelta = percent - moveLastPercent;
            translate(panSpeedX * movePercentDelta, panSpeedY * movePercentDelta, panSpeedZ * movePercentDelta);
            moveLastPercent = percent;
        }
        if (!rotateCompleted) {
            rotateTime += delta;
            rotateCompleted = rotateTime >= rotateDuration;
            float percent;
            if (rotateCompleted)
                percent = 1;
            else
                percent = rotateTime / rotateDuration;
            rotatePercentDelta = percent - rotateLastPercent;
            rotate(Vector3.Y, rotateYaw * rotatePercentDelta);
            rotate(Vector3.X, rotatePitch * rotatePercentDelta);
            rotate(Vector3.Z, rotateRoll * rotatePercentDelta);
            rotateLastPercent = percent;
        }
        if (followedActor3d != null) {
            moveTo(followedActor3d.x + offsetX, followedActor3d.y + offsetY, followedActor3d.z + offsetZ, folllowSpeed);
            if (lookAt)
                lookAt(followedActor3d.x, followedActor3d.y, followedActor3d.z);
            /*
			followedActor3d.getTransform().getTranslation(camera.direction);
			current.set(position).sub(camera.direction);
			desired.set(desiredLocation).rot(followedActor3d.getTransform()).add(desiredOffset);
			final float desiredDistance = desired.len();
			if (rotationSpeed < 0)
				current.set(desired).nor().mul(desiredDistance);
			else if (rotationSpeed == 0 || Vector3.tmp.set(current).dst2(desired) < rotationOffsetSq) 
				current.nor().mul(desiredDistance);
			else {
				current.nor();
				desired.nor();
				rotationAxis.set(current).crs(desired);
				float angle = (float)Math.acos(current.dot(desired)) * MathUtils.radiansToDegrees;
				final float maxAngle = rotationSpeed * delta;
				if (Math.abs(angle) > maxAngle) {
					angle = (angle < 0) ? -maxAngle : maxAngle;
				}
				current.rot(rotationMatrix.idt().rotate(rotationAxis, angle));
				current.mul(desiredDistance);
			}

			current.add(camera.direction);
			absoluteSpeed = Math.min(absoluteSpeed + acceleration, current.dst(position) / delta);
			position.add(speed.set(current).sub(position).nor().mul(absoluteSpeed * delta));
			if (bounds.isValid()) {
				if (position.x < bounds.min.x) position.x = bounds.min.x;
				if (position.x > bounds.max.x) position.x = bounds.max.x;
				if (position.y < bounds.min.y) position.y = bounds.min.y;
				if (position.y > bounds.max.y) position.y = bounds.max.y;
				if (position.z < bounds.min.z) position.z = bounds.min.z;
				if (position.z > bounds.max.z) position.z = bounds.max.z;
			}
			if (offsetBounds.isValid()) {
				Vector3.tmp.set(position).sub(camera.direction);
				if (Vector3.tmp.x < offsetBounds.min.x) position.x = offsetBounds.min.x + camera.direction.x;
				if (Vector3.tmp.x > offsetBounds.max.x) position.x = offsetBounds.max.x + camera.direction.x;
				if (Vector3.tmp.y < offsetBounds.min.y) position.y = offsetBounds.min.y + camera.direction.y;
				if (Vector3.tmp.y > offsetBounds.max.y) position.y = offsetBounds.max.y + camera.direction.y;
				if (Vector3.tmp.z < offsetBounds.min.z) position.z = offsetBounds.min.z + camera.direction.z;
				if (Vector3.tmp.z > offsetBounds.max.z) position.z = offsetBounds.max.z + camera.direction.z;
			}
			camera.direction.add(target.set(targetLocation)
			.rot(followedActor3d.getTransform()).add(targetOffset)).sub(position).nor();*/
        }
    }


    public static float getX() {
        return instance.position.x;
    }

    public static float getY() {
        return instance.position.y;
    }

    public static float getZ() {
        return instance.position.z;
    }

    public static float getWidth() {
        return instance.viewportWidth;
    }

    public static float getHeight() {
        return instance.viewportHeight;
    }

    public static void setFar(float far) {
        instance.far = far;
    }

    public static float getFar() {
        return instance.far;
    }

    public static void setNear(float near) {
        instance.near = near;
    }

    public static float getNear() {
        return instance.near;
    }

    public static void setFieldOfView(float fov) {
        instance.fieldOfView = fov;
    }

    public static float getFieldOfView() {
        return instance.fieldOfView;
    }

}
