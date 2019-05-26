package tests.debug

import conversion7.engine.Gdxg
import conversion7.engine.utils.Utils
import conversion7.game.GdxgConstants
import conversion7.game.services.WorldServices
import org.testng.annotations.Test
import shared.tests.BaseTests
import shared.tests.aaa.BaseAAATest

class DummyWorldTest extends BaseTests {

    @Test(invocationCount = 1, singleThreaded = true)
    public void 'test 1'() {
        new BaseAAATest() {
            @Override
            public void body() {
                WorldServices.scheduleNewWorld(Gdxg.core, GdxgConstants.WORLD_SETTINGS_TEST)
                Utils.infinitySleepThread()
            }
        }.run();
    }
}
