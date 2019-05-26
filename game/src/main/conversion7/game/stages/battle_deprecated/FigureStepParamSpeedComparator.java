package conversion7.game.stages.battle_deprecated;

import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.battle_deprecated.calculation.FigureStepParams;

import java.util.Comparator;

/** 1st has the biggest speed or smallest id */
@Deprecated
public class FigureStepParamSpeedComparator implements Comparator<FigureStepParams> {

    private static final FigureStepParamSpeedComparator COMPARATOR = new FigureStepParamSpeedComparator();

    public static void sort(Array<FigureStepParams> toBeSorted) {
        BattleThreadLocalSort.instance().sort(toBeSorted, COMPARATOR);
    }

    @Override
    public int compare(FigureStepParams o1, FigureStepParams o2) {
        if (o1.speed < o2.speed) {
            return 1;
        } else if (o1.speed == o2.speed) {
            if (o1.battleFigure.getId() > o2.battleFigure.getId()) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}