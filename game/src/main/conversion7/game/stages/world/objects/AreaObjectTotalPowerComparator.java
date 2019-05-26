package conversion7.game.stages.world.objects;

import conversion7.game.stages.world.objects.unit.AbstractSquad;

import java.util.Comparator;

/** 1st has the biggest Power */
public class AreaObjectTotalPowerComparator implements Comparator<AbstractSquad> {

    private static final AreaObjectTotalPowerComparator COMPARATOR = new AreaObjectTotalPowerComparator();

    public static AreaObjectTotalPowerComparator instance() {
        return COMPARATOR;
    }

    @Override
    public int compare(AbstractSquad o1, AbstractSquad o2) {
        return Float.compare(o2.getPowerValue(), o1.getPowerValue());
    }


}
