package system.break_point_steps_core;

import com.google.inject.Guice;

public class StepsFactory {

    public static <C extends AbstractSteps> C getSteps(Class<C> type) {
        C instance;
        try {
            instance = Guice.createInjector(type.newInstance()).getInstance(type);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

}
