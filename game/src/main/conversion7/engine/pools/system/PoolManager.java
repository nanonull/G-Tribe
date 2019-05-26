package conversion7.engine.pools.system;

import com.badlogic.gdx.utils.Pool;
import conversion7.engine.customscene.DecalActor;
import conversion7.engine.pools.ArraysPool;
import conversion7.engine.pools.ObjectMapPool;
import conversion7.engine.pools.ObjectSetPool;
import conversion7.engine.pools.OrderedSetPool;
import conversion7.engine.pools.Vectors2Pool;
import conversion7.engine.pools.Vectors3Pool;
import conversion7.engine.pools.cells_decals.GrassDecalPool;
import conversion7.engine.pools.cells_decals.NotVisibleDecalPool;
import conversion7.engine.pools.cells_decals.SandDecalPool;
import conversion7.engine.pools.cells_decals.StoneDecalPool;
import conversion7.engine.pools.cells_decals.UnexploredDecalPool;
import conversion7.engine.pools.models.AllyTotemCellSelectionPool;
import conversion7.engine.pools.models.ArmyModelPool;
import conversion7.engine.pools.models.CampCellSelectionPool;
import conversion7.engine.pools.models.CampNetCellSelectionPool;
import conversion7.engine.pools.models.ForestGroupPool;
import conversion7.engine.pools.models.OthersTotemCellSelectionPool;
import conversion7.engine.pools.models.StoneModelPool;
import conversion7.engine.pools.models.TownFragmentBuiltModelPool;
import conversion7.engine.pools.models.TownFragmentModelPool;
import conversion7.engine.pools.models.TreeModelPool;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.Landscape;
import conversion7.game.stages.world.landscape.Soil;

public class PoolManager {

    public static final Vectors2Pool VECTOR_2_POOL = new Vectors2Pool();
    public static final Vectors3Pool VECTOR_3_POOL = new Vectors3Pool();
    public static final ArraysPool ARRAYS_POOL = new ArraysPool();
    public static final ObjectSetPool OBJECT_SET_POOL = new ObjectSetPool();
    public static final OrderedSetPool ORDERED_SET_POOL = new OrderedSetPool();
    public static final ObjectMapPool OBJECT_MAP_POOL = new ObjectMapPool();

    public static final ArmyModelPool ARMY_MODEL_POOL = new ArmyModelPool();
    public static final TownFragmentModelPool CAMP_FRAGMENT_MODEL_POOL = new TownFragmentModelPool();
    public static final TownFragmentBuiltModelPool CAMP_FRAGMENT_BUILT_MODEL_POOL = new TownFragmentBuiltModelPool();
    public static final AllyTotemCellSelectionPool ALLY_TOTEM_CELL_SELECTION = new AllyTotemCellSelectionPool();
    public static final OthersTotemCellSelectionPool OTHERS_TOTEM_CELL_SELECTION = new OthersTotemCellSelectionPool();
    public static final CampCellSelectionPool CAMP_CELL_SELECTION = new CampCellSelectionPool();
    public static final CampNetCellSelectionPool CAMP_NET_CELL_SELECTION = new CampNetCellSelectionPool();
    public static final ForestGroupPool FOREST_GROUP_POOL = new ForestGroupPool();
    @Deprecated
    public static final TreeModelPool TREE_MODEL_POOL = new TreeModelPool();
    public static final StoneModelPool STONE_MODEL_POOL = new StoneModelPool();

    public static final Pool<DecalActor> GRASS_DECAL_POOL = new GrassDecalPool();
    public static final Pool<DecalActor> SAND_DECAL_POOL = new SandDecalPool();
    public static final Pool<DecalActor> STONE_DECAL_POOL = new StoneDecalPool();
    public static final UnexploredDecalPool UNEXPLORED_DECAL_POOL = new UnexploredDecalPool();
    public static final NotVisibleDecalPool NOT_VISIBLE_DECAL_POOL = new NotVisibleDecalPool();

    public static DecalActor getFloorDecal(Cell cell) {
        Landscape.Type landType = cell.getLandscape().type;
        if (landType.equals(Landscape.Type.COMMON)
                || landType.equals(Landscape.Type.MOUNTAIN)) {
            // 1st entry in sorted Map should have the biggest soil landType
            switch (cell.getLandscape().soil.parameterArraySorted.get(0).id) {
                case Soil.TypeId.DIRT:
                    return PoolManager.GRASS_DECAL_POOL.obtain();
                case Soil.TypeId.SAND:
                    return PoolManager.SAND_DECAL_POOL.obtain();
                case Soil.TypeId.STONE:
                    return PoolManager.STONE_DECAL_POOL.obtain();
                default:
                    Utils.error("biggestType is unknown");
            }
        }
        return null;
    }

    public static DecalActor getExplorationDecal(Cell cell) {
        Cell.Discovered discovered = cell.getDiscovered();
        if (discovered == null) {
            return PoolManager.UNEXPLORED_DECAL_POOL.obtain();
        } else if (discovered.equals(Cell.Discovered.NOT_VISIBLE)) {
            return PoolManager.NOT_VISIBLE_DECAL_POOL.obtain();
        }

        return null;
    }


}
