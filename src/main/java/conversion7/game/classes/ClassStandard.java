package conversion7.game.classes;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitParameters;

public class ClassStandard {

    private Array<Class<? extends Unit>> childClasses = PoolManager.ARRAYS_POOL.obtain();
    private UnitParameters params;
    private TimeFrame birthTimeFrame;

    @SafeVarargs
    public ClassStandard(UnitParameters params, Class<? extends Unit>... childClasses) {
        this.params = params;
        if (childClasses != null) {
            this.childClasses.addAll(childClasses);
        }
        // TODO define birth timeLine for different Animal classes
        birthTimeFrame = new TimeFrame(-50000000, 2000);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(params.toString()).append("\n\n");
        if (childClasses.size > 0) {
            stringBuilder.append("Child classes:\n");
            for (Class<? extends Unit> childClass : childClasses) {
                stringBuilder.append(" - ").append(childClass.getSimpleName()).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    public Array<Class<? extends Unit>> getChildClasses() {
        return childClasses;
    }

    public UnitParameters getParams() {
        return params;
    }

    public TimeFrame getBirthTimeFrame() {
        return birthTimeFrame;
    }
}
