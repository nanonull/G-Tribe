package conversion7.game.stages.world.objects;

import conversion7.game.stages.world.objects.unit.AbstractSquad;

import java.util.Comparator;

/** 1st has the biggest TotalPowerWithNeighbors */
public class AreaObjectTotalPowerWithNeighborsComparator implements Comparator<AbstractSquad> {

    private static final AreaObjectTotalPowerWithNeighborsComparator COMPARATOR = new AreaObjectTotalPowerWithNeighborsComparator();

    public static AreaObjectTotalPowerWithNeighborsComparator instance() {
        return COMPARATOR;
    }

    @Override
    public int compare(AbstractSquad o1, AbstractSquad o2) {
        return Float.compare(o2.getPowerWithNeighborSupport(), o1.getPowerWithNeighborSupport());
    }
}
