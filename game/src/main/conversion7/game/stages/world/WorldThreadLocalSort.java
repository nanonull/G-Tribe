package conversion7.game.stages.world;

import com.badlogic.gdx.utils.Sort;

public class WorldThreadLocalSort {

    private static Sort instance = new Sort();

    static public Sort instance() {
        return instance;
    }
}
