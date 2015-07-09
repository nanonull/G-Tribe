package conversion7.game.run;

import com.badlogic.ashley.core.Entity;
import conversion7.engine.Gdxg;
import conversion7.engine.ashley.PollingComponent;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.objects.AreaObject;

import java.util.concurrent.Callable;

public class RunAndScheduleLibrary {

    public static void scheduleSingleExecution(int delayMillis, final Runnable runnable) {
        final Entity entity = new Entity();
        Gdxg.ENTITY_SYSTEMS_ENGINE.addEntity(entity);
        entity.add(new PollingComponent(delayMillis, new Callable<PollingComponent.Status>() {
            @Override
            public PollingComponent.Status call() throws Exception {
                runnable.run();
                return PollingComponent.Status.COMPLETED;
            }
        }));
    }

    public static void scheduleCameraFocusOn(int delayMillis, final AreaObject targetObject) {
        scheduleSingleExecution(delayMillis, new Runnable() {
            @Override
            public void run() {
                Gdxg.graphic.getCameraController().moveCameraToLookAtWorldCell(targetObject.getCell());
            }
        });
    }

    public static void scheduleCameraFocusOnPlayerSquad(int delayMillis) {
        scheduleCameraFocusOn(delayMillis, World.getPlayerTeam().getArmies().get(0));
    }

    public static void scheduleWelcomeHint() {
        scheduleSingleExecution(1000, new Runnable() {
            @Override
            public void run() {
                Gdxg.clientUi.showWelcomeHint();
            }
        });
    }
}
