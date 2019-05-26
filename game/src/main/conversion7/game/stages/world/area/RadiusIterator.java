package conversion7.game.stages.world.area;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.geometry.Point2s;
import conversion7.game.stages.world.landscape.Cell;

@Deprecated
public class RadiusIterator {

    public static Cell start;
    private static Cell lastNode;
    public static int step = 0;
    public static int nextTurnAfter = 1;
    public static Array<Point2s> dirs = new Array<>();
    private Point2s dir;

    public static void start(Cell startOn) {
        nextTurnAfter = 1;
        dirs.clear();
        dirs.add(new Point2s(0, 1));
        dirs.add(new Point2s(-1, 0));
        dirs.add(new Point2s(0, -1));
        dirs.add(new Point2s(1, 0));
        start = startOn;
        lastNode = start;
    }

    public Cell next() {
        nextTurnAfter--;
        if (nextTurnAfter == 0) {
            calcNextTurnAfter();
        }
        step++;

        return lastNode.getCell(dir.x, dir.y);
    }

    private void calcNextTurnAfter() {
        // TODO
//        поворот на шаге / прибавка к поворот на шаге
//        1
//        2 1 прибавка
//        4 2
//        6 2
//
//        9 3
//        12 3
//        16 4
//        20 4
//
//        25 5
//        30 5
//        36 6
//        42 6

        nextTurnAfter = 1;
        Point2s first = dirs.removeIndex(0);
        dirs.add(first);
        dir = first;
    }


}
