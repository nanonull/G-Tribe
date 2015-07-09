package conversion7.game.stages.world.landscape;

import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.WorldThreadLocalSort;

import java.util.Comparator;

/** 1st has the shortest distance */
public class CellDistanceToComparator implements Comparator<Cell> {

    private static final CellDistanceToComparator COMPARATOR = new CellDistanceToComparator();

    public static CellDistanceToComparator instance() {
        return COMPARATOR;
    }

    private Cell target;

    @Override
    public int compare(Cell o1, Cell o2) {
        return Float.compare(o1.distanceTo(target), o2.distanceTo(target));
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public Array<Cell> sort(Array<Cell> cellsToBeSorted, Cell cellTo) {
        target = cellTo;
        WorldThreadLocalSort.instance().sort(cellsToBeSorted, this);
        target = null;
        return cellsToBeSorted;
    }

}