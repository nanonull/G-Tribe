package conversion7.game.stages.world.ai_deprecated;

import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.WorldThreadLocalSort;
import conversion7.game.stages.world.landscape.Cell;

import java.util.Comparator;

public class AiNodeDistanceComparator implements Comparator<AiNode> {

    private Cell target;

    @Override
    public int compare(AiNode o1, AiNode o2) {
        float v1 = o1.origin.distanceTo(target);
        float v2 = o2.origin.distanceTo(target);
        if (v1 > v2) {
            return 1;
        } else if (v1 == v2) {
            return o1.id > o2.id ? 1 : -1;
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }


    public void sort(Array<AiNode> nodesToBeSorted, Cell cellTo) {
        target = cellTo;
        WorldThreadLocalSort.instance().sort(nodesToBeSorted, this);
        target = null;
    }
}