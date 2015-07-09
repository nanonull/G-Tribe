package conversion7.game.stages.world.ai.tasks.single;

import conversion7.engine.utils.PropertiesLoader;
import conversion7.game.stages.world.objects.AreaObject;

public class WaitForAttackTask extends AbstractAreaObjectTaskSingle {

    public static final int DEFAULT_PRIORITY =
            PropertiesLoader.getIntProperty("AreaObjectTask.Priority.WaitForAttackTask");


    public WaitForAttackTask(AreaObject owner) {
        super(owner, DEFAULT_PRIORITY);
    }

    @Override
    public boolean execute() {
        // just wait
        return false;
    }
}
