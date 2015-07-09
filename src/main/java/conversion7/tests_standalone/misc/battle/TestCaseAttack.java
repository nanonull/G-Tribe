package conversion7.tests_standalone.misc.battle;

import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

@Deprecated
public class TestCaseAttack {

    private static final Logger LOG = Utils.getLoggerForClass();

    /**
     * Description: dying figure could not hit another figure (because it is slower) <p></p>
     * precondition: be sure f1 is a slower figure (left only speed-comparison by version) <br>
     * ER: inside
     */
    public static void test1() {
        LOG.info("mainTest");

//        Step step1 = Gdxg.core.battle.step;
//
//        BattleFigure f1 = new BattleFigure(Gdxg.core.battle, SIDE.LEFT, null);
//        step1.addFigure(f1, 0, 10);
////        f1.params.damage = 6;
//
//        BattleFigure f2 = new BattleFigure(Gdxg.core.battle, SIDE.RIGHT, null);
//        step1.addFigure(f2, 1, 10);
////        f2.params.damage = 6;


        // ER: on 2nd step:
        // f1 will have 4 life (f2 hits once because it dies)
        // f2 will be killed (f1 hits twice)

    }


    /**
     * Description: before die figure could hit another figure (because it is faster) <p></p>
     * precondition: be sure f1 is a faster figure (left only speed-comparison by id) <br>
     * ER: inside
     */
    @Deprecated
    public static void test2() {
        LOG.info("test2");

//        Step step1 = Gdxg.core.battle.step;
//
//        BattleFigure f1 = new BattleFigure(Gdxg.core.battle, SIDE.LEFT, null);
//        step1.addFigure(f1, 0, 10);
////        f1.params.damage = 6;
//
//        BattleFigure f2 = new BattleFigure(Gdxg.core.battle, SIDE.RIGHT, null);
//        step1.addFigure(f2, 1, 10);
////        f2.params.damage = 10;

        // ER: on 2nd step:
        // f2 will have 4 life
        // f1 will be killed

    }

    /**
     * Description: it should not be possible to step on cell where figure dies <p></p>
     * f1 (team1) | f2 (team1) | f3 (team2)<br>
     * f1 is blocked by f2 (turn off path searching)<br>
     * f2 and f3 action first before f1<br>
     * f3 kills f2<br>
     * ER: f1 starts actions only after f2 dies
     */
    public static void test3() {
        LOG.info("test3");

//        Step step1 = Gdxg.core.battle.step;
//
//        BattleFigure f1 = new BattleFigure(Gdxg.core.battle, SIDE.LEFT, null);
//        step1.addFigure(f1, 0, 10);
//
//        BattleFigure f2 = new BattleFigure(Gdxg.core.battle, SIDE.LEFT, null);
//        step1.addFigure(f2, 1, 10);
////        f2.params.damage = 1;
//        f2.params.speed = f1.params.speed + 1;
//
//        BattleFigure f3 = new BattleFigure(Gdxg.core.battle, SIDE.RIGHT, null);
//        step1.addFigure(f3, 2, 10);
////        f3.params.damage = 10;
//        f3.params.speed = f1.params.speed + 1;


    }


    /**
     * f1 and f2 hit f3<br>
     * ER: f3 dies after f1 hit, but it also plans to attack enemy (dummy animation + null damage)
     */
    public static void test4() {
        LOG.info("test4");

//        Step step1 = Gdxg.core.battle.step;
//
//        BattleFigure f1 = new BattleFigure(Gdxg.core.battle, SIDE.LEFT, null);
//        step1.addFigure(f1, 0, 10);
//
//        BattleFigure f2 = new BattleFigure(Gdxg.core.battle, SIDE.LEFT, null);
//        step1.addFigure(f2, 0, 11);
//
//        BattleFigure f3 = new BattleFigure(Gdxg.core.battle, SIDE.RIGHT, null);
//        step1.addFigure(f3, 1, 11);


    }

}
