package conversion7.game.stages.world.ai.tasks.single;

import conversion7.engine.utils.PropertiesLoader;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;

public class MoveToAttackTask extends MoveTask {

    public static final int DEFAULT_PRIORITY =
            PropertiesLoader.getIntProperty("AreaObjectTask.Priority.MoveToAttackTask");


    public MoveToAttackTask(AreaObject owner, Cell moveTo) {
        super(owner, moveTo, DEFAULT_PRIORITY);
    }

}
