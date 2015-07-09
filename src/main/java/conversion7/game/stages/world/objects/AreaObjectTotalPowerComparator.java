package conversion7.game.stages.world.objects;

import java.util.Comparator;

/** 1st has the biggest Power */
public class AreaObjectTotalPowerComparator implements Comparator<AreaObject> {

    private static final AreaObjectTotalPowerComparator COMPARATOR = new AreaObjectTotalPowerComparator();

    public static AreaObjectTotalPowerComparator instance() {
        return COMPARATOR;
    }

    @Override
    public int compare(AreaObject o1, AreaObject o2) {
        return Float.compare(o2.getPower(), o1.getPower());
    }


}
