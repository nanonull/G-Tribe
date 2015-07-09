package conversion7.tests_standalone.misc.battle;

import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

@Deprecated
public class TestCaseCellForward {

    private static final Logger LOG = Utils.getLoggerForClass();

    /**
     * Description: prohibit to step on cell where figure now<p></p>
     * precondition: disable all figure actions except "moveForOneCell" <p></p>
     * ER: both figures have nothing to do
     */
    public static void test1() {

        LOG.info("mainTest");

//        Step step1 = Gdxg.core.battle.step;
//
//        step1.addFigure(new BattleFigure(Gdxg.core.battle, SIDE.LEFT, null), 0, 10);
//        step1.addFigure(new BattleFigure(Gdxg.core.battle, SIDE.RIGHT, null), 1, 10);

    }

    /**
     * Description: prohibit to step on cell which will be seized by another figure<p></p>
     * precondition: disable all figure actions except "moveForOneCell" <p></p>
     * ER: the slowest figure has no actions
     */
    public static void test2() {
        LOG.info("test2");

//        Step step1 = Gdxg.core.battle.step;
//
//        step1.addFigure(new BattleFigure(Gdxg.core.battle, SIDE.LEFT, null), 0, 10);
//        step1.addFigure(new BattleFigure(Gdxg.core.battle, SIDE.RIGHT, null), 2, 10);

        // step: calculate actions

    }

    /**
     * Description: possible to step on cell from which figure goes away <p></p>
     * precondition: disable all figure actions except "moveForOneCell"   <br></br>
     * precondition2: disable figure speed sorting except comparing by id   <br>
     * ER: There are ERs in comments for each figure
     */
    public static void test3() {
        LOG.info("test3");

//        Step step1 = Gdxg.core.battle.step;
//
//        step1.addFigure(new BattleFigure(Gdxg.core.battle, SIDE.LEFT, null), 0, 10); // ER: goes first, goes right
//        step1.addFigure(new BattleFigure(Gdxg.core.battle, SIDE.UP, null), 0, 11); // ER: goes down and seize cell which had been seized by another

        // step: calculate actions

    }

}
