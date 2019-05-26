package conversion7.game.utils.collections;

import conversion7.game.stages.battle_deprecated.calculation.FigureStepParams;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;

import java.util.Comparator;

// TODO refactor comparators: move here
public class Comparators {

    public static final Comparator<FigureStepParams> FIGURE_VALUE_COMPARATOR = (o1, o2) -> {
        float v1 = o1.calculateValueForEnemy();
        if (o1.distanceToMeFromLastAttacker > 0) {
            v1 /= o1.distanceToMeFromLastAttacker;
        }
        float v2 = o2.calculateValueForEnemy();
        if (o2.distanceToMeFromLastAttacker > 0) {
            v2 /= o2.distanceToMeFromLastAttacker;
        }

        if (v1 < v2) {
            return 1;
        } else if (v1 == v2) {
            if (o1.battleFigure.getId() > o2.battleFigure.getId()) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    };

    /** 1st has the biggest Power */
    public static final Comparator<Unit> UNIT_POWER_COMPARATOR = (o1, o2) ->
            Float.compare(o2.squad.getCurrentPower(), o1.squad.getCurrentPower());
    /** 1st has the biggest Power */
    public static final Comparator<AbstractSquad> SQUAD_POWER_COMPARATOR = (o1, o2) ->
            Float.compare(o2.getCurrentPower(), o1.getCurrentPower());
    /** 1st has the biggest */
    public static final Comparator<AbstractSquad> SQUAD_TARGET_VALUE_COMPARATOR = (o1, o2) ->
            Float.compare(o2.relativeTargetValue, o1.relativeTargetValue);

    @Deprecated
    /** 1st has the biggest Power */
    public static final Comparator<Unit> UNIT_EQUIPPING_PRIORITY_COMPARATOR = (o1, o2) ->
            Float.compare(o2.squad.getEquipPriority(), o1.squad.getEquipPriority());

    /** 1st has the biggest Value */
    public static final Comparator<AbstractInventoryItem> INVENTORY_ITEM_VALUE_COMPARATOR = (o1, o2) -> Float.compare(o2.getParams().getValue(), o1.getParams().getValue());

    public static final Comparator<AbstractAreaObjectAction> AREA_OBJECT_ACTIONS_COMPARATOR = (o1, o2) -> Float.compare(o1.getActionPositionPriority(), o2.getActionPositionPriority());


    public static final Comparator<? super Team> TEAMS_ORDER = (o1, o2) -> {
        int compareOrder = Integer.compare(o1.getTeamOrder(), o2.getTeamOrder());
        if (compareOrder == 0) {
            return Integer.compare(o1.getTeamId(), o2.getTeamId());
        } else {
            return compareOrder;
        }
    };
}
