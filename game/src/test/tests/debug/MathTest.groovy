package tests.debug

import conversion7.engine.utils.Normalizer

class MathTest extends GroovyTestCase {

    int BASE_AGI = 10
    int MAX_AGI = 50
    /*
    base chance = 85%
    from BASE_AGI to MAX_AGI
    85% to 100%

    10a = 85
    20a = 95
    50a = 100
    */

    void 'test1'() {
        assert Normalizer.normalize(10, 10, 50, 85, 100) == 85
        assert Normalizer.normalize(50, 10, 50, 85, 100) == 100

        double d
        [0, 5, 9, 10, 11, 12, 15, 18, 20, 30, 40, 50, 51, 60, 100].each {
            println it
            d = Normalizer.normalize(it, 10, 50, 0, Math.PI / 2f)
            println d
            println Math.sin(d)
            println ''
        }

    }
}
