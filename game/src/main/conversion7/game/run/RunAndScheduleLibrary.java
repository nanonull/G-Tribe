package conversion7.game.run;

import conversion7.engine.artemis.engine.time.SchedulingSystem;

public class RunAndScheduleLibrary {

    // use SchedulingSystem.schedule directly
    @Deprecated
    public static void scheduleSingleExecution(int delayMillis, final Runnable runnable) {
        SchedulingSystem.schedule("scheduleSingleExecution", delayMillis, runnable);
    }
}
