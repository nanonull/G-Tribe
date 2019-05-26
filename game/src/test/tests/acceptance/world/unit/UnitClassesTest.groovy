package tests.acceptance.world.unit

import conversion7.game.unit_classes.UnitClassConstants
import spock.lang.Specification

class UnitClassesTest extends Specification {

    void 'test 1'() {
        given:
        UnitClassConstants.initClassStandards()
    }
}
