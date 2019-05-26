package system.break_point_steps_core;

public interface TestTargetApplication {

    boolean isInRender();

    long getCoreFrameId();

    int getFramesInLock();

    void scheduleCoreStep(Runnable coreStepBody);

    boolean hasCoreStepScheduled();
}
