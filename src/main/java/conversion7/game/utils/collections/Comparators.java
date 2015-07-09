package conversion7.game.utils.collections;

import conversion7.game.stages.battle.calculation.FigureStepParams;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;
import conversion7.game.stages.world.unit.Unit;

import java.util.Comparator;

// TODO refactor comparators: move here
public class Comparators {

    public static final Comparator<FigureStepParams> FIGURE_VALUE_COMPARATOR = new Comparator<FigureStepParams>() {
        @Override
        public int compare(FigureStepParams o1, FigureStepParams o2) {
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
        }
    };

    public static final Comparator<Unit> UNIT_FOOD_WATER_PRIORITY_COMPARATOR = new Comparator<Unit>() {
        @Override
        public int compare(Unit o1, Unit o2) {

            float power1 = o1.getFoodPriority();
            float power2 = o2.getFoodPriority();

            if (power1 < power2) {
                return 1;
            } else if (power1 == power2) {
                if (o1.id > o2.id) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        }
    };

    /** 1st has the biggest Power */
    public static final Comparator<Unit> UNIT_POWER_COMPARATOR = new Comparator<Unit>() {
        @Override
        public int compare(Unit o1, Unit o2) {
            return Float.compare(o2.getCalculatedPower(), o1.getCalculatedPower());
        }
    };

    /** 1st has the biggest Power */
    public static final Comparator<Unit> UNIT_EQUIPPING_PRIORITY_COMPARATOR = new Comparator<Unit>() {
        @Override
        public int compare(Unit o1, Unit o2) {
            return Float.compare(o2.getEquipPriority(), o1.getEquipPriority());
        }
    };

    /** 1st has the biggest Value */
    public static final Comparator<AbstractInventoryItem> INVENTORY_ITEM_VALUE_COMPARATOR = new Comparator<AbstractInventoryItem>() {
        @Override
        public int compare(AbstractInventoryItem o1, AbstractInventoryItem o2) {
            return Float.compare(o2.getParams().getValue(), o1.getParams().getValue());
        }
    };

    public static final Comparator<AbstractAreaObjectAction> AREA_OBJECT_ACTIONS_COMPARATOR = new Comparator<AbstractAreaObjectAction>() {
        @Override
        public int compare(AbstractAreaObjectAction o1, AbstractAreaObjectAction o2) {
            return Float.compare(o1.getActionPositionPriority(), o2.getActionPositionPriority());
        }
    };
}
