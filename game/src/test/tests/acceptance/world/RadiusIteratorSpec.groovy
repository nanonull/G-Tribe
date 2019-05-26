package tests.acceptance.world

import conversion7.engine.Gdxg
import conversion7.game.stages.world.area.RadiusIterator
import shared.BaseGdxgSpec

class RadiusIteratorSpec extends BaseGdxgSpec {
    def 'test 1'() {
        when:
        def world = Gdxg.core.world
        def startCell = world.getCell(0, 0)
        RadiusIterator.start(startCell)

        then:
        assert RadiusIterator.next() == startCell.getCell(0, 1)
    }

}
