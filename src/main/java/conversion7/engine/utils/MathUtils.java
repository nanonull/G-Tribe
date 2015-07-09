package conversion7.engine.utils;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.pools.system.PoolManager;

import java.util.Map;

public class MathUtils {

    public static final float SQRT_TWO = (float) Math.sqrt(2);

    public static int getSumOfMapValues(Map<?, Integer> map) {
        int sum = 0;
        for (Map.Entry<?, Integer> e : map.entrySet()) {
            sum += e.getValue();
        }
        return sum;
    }

    public static Vector3 getPositionFromMatrix(Matrix4 matrix4) {
        float[] values = matrix4.getValues();
        return new Vector3(values[12], values[13], values[14]);
    }

    public static Vector3 toEngineCoords(Vector3 gameCoordinates) {
        return toEngineCoords(gameCoordinates.x, gameCoordinates.y, gameCoordinates.z);
    }

    public static Vector3 toEngineCoords(float gameX, float gameY, float gameZ) {
        return PoolManager.VECTOR_3_POOL.obtain().set(gameX, gameZ, -gameY);
    }

    /** Be careful with negative engineVector coords */
    public static Point2s toGameCoords2d(Vector3 engineVector) {
        return new Point2s(engineVector.x, -engineVector.z);
    }

    public static Vector3 toGameCoords(Vector3 engineVector) {
        Vector3 vector3 = PoolManager.VECTOR_3_POOL.obtain();
        vector3.set(engineVector.x, -engineVector.z, engineVector.y);
        return vector3;
    }

    public static float simpleMin(float v1, float v2) {
        if (v1 < v2) {
            return v1;
        } else {
            return v2;
        }
    }

    public static int simpleMin(int v1, int v2) {
        if (v1 < v2) {
            return v1;
        } else {
            return v2;
        }
    }

    public static float simpleMax(float v1, float v2) {
        if (v1 > v2) {
            return v1;
        } else {
            return v2;
        }
    }

    public static float getCircleRadiusAroundSquare(float sideLength) {
        return sideLength / SQRT_TWO;
    }

    public static String formatNumber(int value) {
        return value > 0 ? String.format("+%d", value)
                : value < 0 ? String.format("-%d", value)
                : String.valueOf(value);
    }
}
