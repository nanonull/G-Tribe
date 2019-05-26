package conversion7.engine.utils;


import spock.lang.Specification;

class MathUtilsTest extends Specification {
    void getPercentValue() {
        given:
        assert MathUtils.getPercentValue(50, 3) == 1
        assert MathUtils.getPercentValue(100, 3) == 3
        assert MathUtils.getPercentValue(0, 3) == 0
        assert MathUtils.getPercentValue(67, 3) == 2
    }

}