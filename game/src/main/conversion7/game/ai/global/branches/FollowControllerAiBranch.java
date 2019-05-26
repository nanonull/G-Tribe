package conversion7.game.ai.global.branches;

import conversion7.game.ai.global.AiTaskType;
import conversion7.game.ai.global.tasks.MoveTask;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.effects.items.UnderControlEffect;

public class FollowControllerAiBranch {

    public static void eval(AbstractSquad animal) {
        UnderControlEffect controlEffect = animal.getEffectManager().getEffect(UnderControlEffect.class);
        if (animal.sees(controlEffect.controller.squad)) {
            animal.addAiTask(new MoveTask(animal, controlEffect.controller.squad.getLastCell(),
                    AiTaskType.MOVE_TO_CONTROLLER));
            controlEffect.resetTickCounter();
        } else {
            controlEffect.validate();
        }
    }
}
