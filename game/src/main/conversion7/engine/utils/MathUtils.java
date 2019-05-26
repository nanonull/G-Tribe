package conversion7.engine.utils;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.pools.system.PoolManager;
import org.testng.Assert;

import java.util.Map;
import java.util.Random;

public class MathUtils {

    public static final float SQRT_TWO = (float) Math.sqrt(2);
    public static final double HALF_PI = Math.PI / 2d;
    public static final Random RANDOM = new Random();

    public static int multiplyOnPercent(int param, int percentValue) {
        return (int) (param * (percentValue / 100f));
    }

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

    /** @param gameZ height */
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

    /**
     * @param percentValue Random from 0 to 99. <br>
     *                     >= 100 - always true
     *                     < 1 - always false
     */
    public static boolean testPercentChance(int percentValue) {
        return testChance(percentValue, 100);
    }

    /**
     * value from chanceMax <br>
     * testChance(1,1) ==> chance 1 of 1 == 100%<br>
     * testChance(1,2) ==> chance 1 of 2 == 50%<br>
     * <br>
     * return 99 > 0 && RANDOM.nextInt(100) == (0..99) <= 99;<br>
     * return 1 > 0 && RANDOM.nextInt(2) == (0..1) < 1;<br>
     * return 100 > 0 && RANDOM.nextInt(100) == (0..99) < 100;<br>
     */
    public static boolean testChance(int value, int chanceMax) {
        return value > 0 && RANDOM.nextInt(chanceMax) < value;

    }

    /**
     * random(0,0) >> return 0<br>
     * random(0,1) >> return 0..1<br>
     * random(1,3) >> return 1..3<br>
     */
    static public int random(int start, int end) {
        Assert.assertTrue(end >= start);
        return start + RANDOM.nextInt(end - start + 1);
    }

    public static int getPercentValue(int percent, int value) {
        return value * percent / 100;
    }

    /** For another types look into com.badlogic.gdx.math.MathUtils */
    public static int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static boolean random() {
        return RANDOM.nextBoolean();
    }
}
