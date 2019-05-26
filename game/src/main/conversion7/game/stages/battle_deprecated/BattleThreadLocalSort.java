package conversion7.game.stages.battle_deprecated;

import com.badlogic.gdx.utils.Sort;

public class BattleThreadLocalSort {

    private static Sort instance = new Sort();

    static public Sort instance() {
        return instance;
    }
}
