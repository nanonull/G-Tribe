package tests.debug

import conversion7.engine.utils.Utils
import shared.BaseGdxgSpec

public class TestStuckHandlerTest extends BaseGdxgSpec {

    public void 'test 1 COMMON'() {
        given:
        Utils.sleepThread(5000)
    }

}
