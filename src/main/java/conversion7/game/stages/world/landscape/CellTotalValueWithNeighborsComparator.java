package conversion7.game.stages.world.landscape;

import java.util.Comparator;

/** 1st has the biggest TotalValueWithNeighbors */
public class CellTotalValueWithNeighborsComparator implements Comparator<Cell> {

    private static final CellTotalValueWithNeighborsComparator COMPARATOR = new CellTotalValueWithNeighborsComparator();

    public static CellTotalValueWithNeighborsComparator instance() {
        return COMPARATOR;
    }

    @Override
    public int compare(Cell o1, Cell o2) {
        float v1 = o1.getTotalValueWithCellsAround();
        float v2 = o2.getTotalValueWithCellsAround();
        if (v1 > v2) {
            return -1;
        } else if (v1 == v2) {
            return o1.id > o2.id ? 1 : -1;
        } else {
            return 1;
        }
    }
}
