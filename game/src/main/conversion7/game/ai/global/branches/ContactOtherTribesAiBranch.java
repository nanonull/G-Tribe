package conversion7.game.ai.global.branches;

import conversion7.game.ai.global.AiTaskType;
import conversion7.game.ai.global.tasks.MoveTask;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class ContactOtherTribesAiBranch {

    public static void eval(AbstractSquad mySquad) {
        if (!mySquad.team.canAskToJoinAtWorldStep()) {
            return;
        }

//        for (AbstractSquad squad : mySquad.getSquadsAround()) {
//            if (canJoin(mySquad, squad)) {
//                tryToJoin(mySquad, squad);
//                return;
//            }
//        }

        for (AreaObject object : mySquad.visibleObjects) {
            if (object.isSquad() && canJoin(mySquad, (AbstractSquad) object)) {
                mySquad.addAiTask(new MoveTask(mySquad, object.getLastCell(), AiTaskType.MOVE_TO_CONTACT_TRIBE));
            }
        }
    }

    public static void tryToJoin(AbstractSquad mySquad, AbstractSquad squad) {
        mySquad.team.tryToJoin(squad);
    }

    public static boolean canJoin(AbstractSquad mySquad, AbstractSquad squad) {
        return mySquad.canJoin(squad);
    }

}
