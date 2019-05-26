package conversion7.game.ai.global.branches;

import com.badlogic.gdx.utils.Array;
import conversion7.game.ai.global.AiTaskType;
import conversion7.game.ai.global.tasks.DummyUnitTask;
import conversion7.game.ai.global.tasks.MoveTask;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class FertilizeAiBranch {
    public static void eval(AbstractSquad squad) {
        if (squad.isFemale()) {
            return;
        }

        Array<AreaObject> visibleObjects = squad.visibleObjects;
        for (AreaObject visibleObject : visibleObjects) {
            if (!visibleObject.isSquad()) continue;
            AbstractSquad anotherSquad = (AbstractSquad) visibleObject;
            if (anotherSquad.isMale()) continue;
            if (anotherSquad.team != squad.team) continue;

            if (anotherSquad.isNeighborOf(squad)) {
                squad.addAiTask(new DummyUnitTask(squad));
            } else {
                squad.addAiTask(new MoveTask(squad, anotherSquad.getLastCell(), AiTaskType.MOVE_FOR_FERTILIZE));
            }
        }
    }
}
