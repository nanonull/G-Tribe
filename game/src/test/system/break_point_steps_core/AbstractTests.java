package system.break_point_steps_core;

public abstract class AbstractTests {

    public <C extends AbstractSteps> C getSteps(Class<C> type) {
        return StepsFactory.getSteps(type);
    }

}
