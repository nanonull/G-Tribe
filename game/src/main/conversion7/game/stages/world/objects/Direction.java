package conversion7.game.stages.world.objects;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.geometry.Point2s;
import conversion7.game.stages.world.landscape.Cell;

import java.util.HashMap;
import java.util.Map;

public class Direction {

    private static final HashMap<Integer, Array<Point2s>> defendedDirections = new HashMap<>();
    private static final HashMap<Integer, Integer> yawByDir = new HashMap<>();

    static {
        Array<Point2s> diffVector;

        diffVector = new Array<>();
        defendedDirections.put(0, diffVector);
        diffVector.add(new Point2s(1, -1));
        diffVector.add(new Point2s(0, -1));
        diffVector.add(new Point2s(-1, -1));

        diffVector = new Array<>();
        defendedDirections.put(1, diffVector);
        diffVector.add(new Point2s(0, -1));
        diffVector.add(new Point2s(-1, -1));
        diffVector.add(new Point2s(-1, 0));

        diffVector = new Array<>();
        defendedDirections.put(2, diffVector);
        diffVector.add(new Point2s(-1, -1));
        diffVector.add(new Point2s(-1, 0));
        diffVector.add(new Point2s(-1, 1));

        diffVector = new Array<>();
        defendedDirections.put(3, diffVector);
        diffVector.add(new Point2s(-1, 0));
        diffVector.add(new Point2s(-1, 1));
        diffVector.add(new Point2s(0, 1));

        diffVector = new Array<>();
        defendedDirections.put(4, diffVector);
        diffVector.add(new Point2s(-1, 1));
        diffVector.add(new Point2s(0, 1));
        diffVector.add(new Point2s(1, 1));

        diffVector = new Array<>();
        defendedDirections.put(5, diffVector);
        diffVector.add(new Point2s(0, 1));
        diffVector.add(new Point2s(1, 1));
        diffVector.add(new Point2s(1, 0));

        diffVector = new Array<>();
        defendedDirections.put(6, diffVector);
        diffVector.add(new Point2s(1, 1));
        diffVector.add(new Point2s(1, 0));
        diffVector.add(new Point2s(1, -1));

        diffVector = new Array<>();
        defendedDirections.put(7, diffVector);
        diffVector.add(new Point2s(1, 0));
        diffVector.add(new Point2s(1, -1));
        diffVector.add(new Point2s(0, -1));

        yawByDir.put(0, 180);
        yawByDir.put(1, 135);
        yawByDir.put(2, 90);
        yawByDir.put(3, 45);
        yawByDir.put(4, 0);
        yawByDir.put(5, 315);
        yawByDir.put(6, 270);
        yawByDir.put(7, 225);
    }

    private AreaObject areaObject;
    private int value = 0;

    public Direction(AreaObject areaObject) {
        this.areaObject = areaObject;
    }

    public static int getDirBy(Cell from, Cell to) {
        Point2s diffWithCell = to.getDiffWithCell(from).trim(1);
        if (diffWithCell.x == 0 && diffWithCell.y == 0) {
            throw new GdxRuntimeException("Attack myself?");
        }
        for (Map.Entry<Integer, Array<Point2s>> entry : defendedDirections.entrySet()) {
            Integer key = entry.getKey();
            Array<Point2s> point2sArray = entry.getValue();
            Point2s midPointOfDefDirection = point2sArray.get(1);
            if (midPointOfDefDirection.equals(diffWithCell)) {
                return key;
            }
        }
        throw new GdxRuntimeException("bad diff: " + diffWithCell);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (value > 7) value = 7;
        if (value < 0) value = 0;
        this.value = value;
        areaObject.getSceneBody().setRotation(yawByDir.get(value), 0, 0);
    }

    public boolean isFlankedBy(Point2s diffWithCell) {
        Array<Point2s> defDirs = defendedDirections.get(value);
        for (Point2s defDir : defDirs) {
            if (defDir.equals(diffWithCell)) {
                return false;
            }
        }
        return true;
    }
}
