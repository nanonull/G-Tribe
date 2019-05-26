package conversion7.tests_standalone.misc.battle;

import conversion7.engine.customscene.Actor3d;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.geometry.Point2s;
import conversion7.game.Assets;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class TestCasesBattle {

    public static int CUR_STEP = -1;
    public static final List<Point2s> DIRECTIONS_TO_MOVE_BY_CIRCLE = new ArrayList<Point2s>();

    static {
        DIRECTIONS_TO_MOVE_BY_CIRCLE.add(new Point2s(1, 0));
        DIRECTIONS_TO_MOVE_BY_CIRCLE.add(new Point2s(1, 1));
        DIRECTIONS_TO_MOVE_BY_CIRCLE.add(new Point2s(0, 1));
        DIRECTIONS_TO_MOVE_BY_CIRCLE.add(new Point2s(-1, 1));
        DIRECTIONS_TO_MOVE_BY_CIRCLE.add(new Point2s(-1, 0));
        DIRECTIONS_TO_MOVE_BY_CIRCLE.add(new Point2s(-1, -1));
        DIRECTIONS_TO_MOVE_BY_CIRCLE.add(new Point2s(0, -1));
        DIRECTIONS_TO_MOVE_BY_CIRCLE.add(new Point2s(1, -1));
    }

    /**
     * How was used:<br>
     * In MovementController: Point newDirection = TestCasesBattle.getNextDirection();
     */
    public static Point getNextDirection() {
        CUR_STEP++;
        if (CUR_STEP == DIRECTIONS_TO_MOVE_BY_CIRCLE.size()) {
            CUR_STEP = 0;
        }
        return DIRECTIONS_TO_MOVE_BY_CIRCLE.get(CUR_STEP);
    }

    @Deprecated
    public static void modelPerformanceInBattle() {
        // test
        ModelActor modelActor;
        for (int x = 0; x < 100; x++) {
            Actor3d knight = new Actor3d(Assets.getModel("knight"), 0, 0, 0f);
//            knight.getAnimation().inAction = true;
            knight.getAnimation().animate("Walk", -1, 1f, null, 0.2f);
            modelActor = new ModelActor(knight);
//            modelGroup.addModel(modelActor);
            modelActor.setX(x);
        }
    }

}
