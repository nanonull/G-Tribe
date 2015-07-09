package conversion7.game.stages.battle;

import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

/** 1st has the biggest speed or smallest id */
public class FigureSpeedComparator implements Comparator<BattleFigure> {

    private static final FigureSpeedComparator COMPARATOR = new FigureSpeedComparator();

    @Override
    public int compare(BattleFigure o1, BattleFigure o2) {
        if (o1.params.speed < o2.params.speed) {
            return 1;
        } else if (o1.params.speed == o2.params.speed) {
            if (o1.getId() > o2.getId()) {
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

    public static void sort(Array<BattleFigure> toBeSorted) {
        BattleThreadLocalSort.instance().sort(toBeSorted, COMPARATOR);
    }

}