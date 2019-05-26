package conversion7.engine.utils;

public class Normalizer {

    /**
     * Normalize actualData.
     *
     * @param actualData The value to be normalized.
     * @return The result of the normalization.
     */
    public static double normalize(double actualData, double dataHigh, double dataLow, double normalizedHigh, double normalizedLow) {
        return ((actualData - dataLow)
                / (dataHigh - dataLow))
                * (normalizedHigh - normalizedLow) + normalizedLow;
    }

    /**
     * Denormalize actualData.
     *
     * @param actualData The value to denormalize.
     * @return The denormalized value.
     */
    public static double denormalize(double actualData, double dataHigh, double dataLow, double normalizedHigh, double normalizedLow) {
        return ((dataLow - dataHigh) * actualData - normalizedHigh
                * dataLow + dataHigh * normalizedLow)
                / (normalizedLow - normalizedHigh);
    }

}
