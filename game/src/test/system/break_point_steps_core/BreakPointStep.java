package system.break_point_steps_core;

public class BreakPointStep {

    public static int stepCounter;
    private BreakPointStep parent;
    private long completedOnCoreFrame = -1;
    private long startedOnCoreFrame = -1;
    private String name;

    public BreakPointStep() {
        name = Thread.currentThread().getStackTrace()[4].getMethodName();
        if (name.equals("proceed")) {
            name = Thread.currentThread().getStackTrace()[6].getMethodName();
        }
        stepCounter++;
    }

    public long getCompletedOnCoreFrame() {
        return completedOnCoreFrame;
    }

    public void setCompletedOnCoreFrame(long completedOnCoreFrame) {
        this.completedOnCoreFrame = completedOnCoreFrame;
    }

    public String getName() {
        return name;
    }

    public long getStartedOnCoreFrame() {
        return startedOnCoreFrame;
    }

    public void setStartedOnCoreFrame(long startedOnCoreFrame) {
        this.startedOnCoreFrame = startedOnCoreFrame;
    }

    public BreakPointStep getParent() {
        return parent;
    }

    public void setParent(BreakPointStep parent) {
        this.parent = parent;
    }

}
