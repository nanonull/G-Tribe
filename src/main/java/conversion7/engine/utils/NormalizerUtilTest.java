package conversion7.engine.utils;

import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class NormalizerUtilTest {

    @Test
    public void testDirectNormalize() {
        NormalizerUtil normUtil = new NormalizerUtil(5, 2, 1, 0);
        double normalized1 = normUtil.normalize(5);
        assertThat(normalized1).isEqualTo(1);
        double normalized2 = normUtil.normalize(2);
        assertThat(normalized2).isEqualTo(0);
    }

    @Test
    public void testReverseNormalizeUsingDiff() {
        // dst 2 > a ==  1
        // dst 5 > a ==  0

        double maxDst = 5;
        double minDst = 2;

        NormalizerUtil normUtil = new NormalizerUtil(maxDst - minDst, 0, 1, 0);

        double normalized;
        double dstValue;
        double dst;

        dst = minDst;
        dstValue = maxDst - dst;
        normalized = normUtil.normalize(dstValue);
        assertThat(normalized).isEqualTo(1);

        dst = maxDst;
        dstValue = maxDst - dst;
        normalized = normUtil.normalize(dstValue);
        assertThat(normalized).isEqualTo(0);

        dst = (maxDst + minDst) / 2f;
        dstValue = maxDst - dst;
        normalized = normUtil.normalize(dstValue);
        assertThat(normalized).isEqualTo(0.5);
    }

    /** Not supported */
    @Test
    public void testReverseNormalize() {
        NormalizerUtil normUtil = new NormalizerUtil(4, 2, 0, 1);

//        double normalized1 = normUtil.normalize(4);
//        assertThat(normalized1).isEqualTo(0);
//
//        double normalized2 = normUtil.normalize(2);
//        assertThat(normalized2).isEqualTo(1);
//
//        double normalized3 = normUtil.normalize(3);
//        assertThat(normalized2).isEqualTo(0.5f);
    }

}
