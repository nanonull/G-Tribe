package conversion7.game.stages.world.objects;

import java.util.Comparator;

/** 1st has the biggest TotalPowerWithNeighbors */
public class AreaObjectTotalPowerWithNeighborsComparator implements Comparator<AreaObject> {

    private static final AreaObjectTotalPowerWithNeighborsComparator COMPARATOR = new AreaObjectTotalPowerWithNeighborsComparator();

    public static AreaObjectTotalPowerWithNeighborsComparator instance() {
        return COMPARATOR;
    }

    @Override
    public int compare(AreaObject o1, AreaObject o2) {
        return Float.compare(o2.getPowerWithNeighborSupport(), o1.getPowerWithNeighborSupport());
    }
}
