package conversion7.game.stages.battle_deprecated;

import conversion7.game.stages.battle_deprecated2.BattleUnit;

import java.util.Comparator;

/** 1st has the biggest speed or smallest id */
public class BattleUnitsSpeedComparator implements Comparator<BattleUnit> {

    public static final BattleUnitsSpeedComparator INSTANCE = new BattleUnitsSpeedComparator();

    @Override
    public int compare(BattleUnit o1, BattleUnit o2) {
        int speed1 = o1.getSpeed();
        int speed2 = o2.getSpeed();
        if (speed1 < speed2) {
            return 1;
        } else if (speed1 == speed2) {
            if (o1.getUnit().id > o2.getUnit().id) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

}