package conversion7.engine.utils;

public class NormalizerUtil {

    private double dataHigh;
    private double dataLow;
    private double normalizedHigh;
    private double normalizedLow;

    /**
     * Construct the normalization utility.  Default to normalization range of -1 to +1.
     *
     * @param dataHigh The high value for the input data.
     * @param dataLow  The low value for the input data.
     */
    public NormalizerUtil(double dataHigh, double dataLow) {
        this(dataHigh, dataLow, 1, -1);
    }

    /**
     * Construct the normalization utility, allow the normalization range to be specified.
     */
    public NormalizerUtil(double dataHigh, double dataLow, double normalizedHigh, double normalizedLow) {
        this.dataHigh = dataHigh;
        this.dataLow = dataLow;
        this.normalizedHigh = normalizedHigh;
        this.normalizedLow = normalizedLow;
    }

    /**
     * Normalize x.
     *
     * @param x The value to be normalized.
     * @return The result of the normalization.
     */
    public double normalize(double x) {
        return ((x - dataLow)
                / (dataHigh - dataLow))
                * (normalizedHigh - normalizedLow) + normalizedLow;
    }

    /**
     * Denormalize x.
     *
     * @param x The value to denormalize.
     * @return The denormalized value.
     */
    public double denormalize(double x) {
        return ((dataLow - dataHigh) * x - normalizedHigh
                * dataLow + dataHigh * normalizedLow)
                / (normalizedLow - normalizedHigh);
    }

}
