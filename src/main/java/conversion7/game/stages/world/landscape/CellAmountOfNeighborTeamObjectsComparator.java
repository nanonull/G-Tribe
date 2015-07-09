package conversion7.game.stages.world.landscape;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.game.stages.world.WorldThreadLocalSort;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.team.Team;

import java.util.Comparator;

/** 1st has the smallest amount of neighbor objects for target team */
public class CellAmountOfNeighborTeamObjectsComparator implements Comparator<Cell> {

    private static final CellAmountOfNeighborTeamObjectsComparator COMPARATOR = new CellAmountOfNeighborTeamObjectsComparator();

    public static CellAmountOfNeighborTeamObjectsComparator instance() {
        return COMPARATOR;
    }

    private Team target;

    @Override
    public int compare(Cell o1, Cell o2) {
        Array<AreaObject> neighborObjectsOfTeam1 = o1.getNeighborObjectsOfTeam(AreaObject.class, target);
        Array<AreaObject> neighborObjectsOfTeam2 = o2.getNeighborObjectsOfTeam(AreaObject.class, target);
        int compare = Integer.compare(neighborObjectsOfTeam1.size, neighborObjectsOfTeam2.size);
        PoolManager.ARRAYS_POOL.free(neighborObjectsOfTeam1);
        PoolManager.ARRAYS_POOL.free(neighborObjectsOfTeam2);
        return compare;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public Array<Cell> sort(Array<Cell> cellsToBeSorted, Team byTeam) {
        target = byTeam;
        WorldThreadLocalSort.instance().sort(cellsToBeSorted, this);
        target = null;
        return cellsToBeSorted;
    }

}