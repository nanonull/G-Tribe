package conversion7.game.stages;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;

public abstract class GameStage {

    public int id;

    protected Array<InputProcessor> inputProcessors = PoolManager.ARRAYS_POOL.obtain();

    public Array<InputProcessor> getInputProcessors() {
        return inputProcessors;
    }

    public abstract void registerInputProcessors();

    public abstract void act(float delta);

    public abstract void draw();

    public abstract void onShow();

    public abstract void onHide();

    public abstract void dispose();

}
