package conversion7.engine.artemis.engine.time;

import com.artemis.Component;

public abstract class SelfIntervalExecutorComponent extends Component {
    public Runnable runnable;
    public int intervalMillis = 1;
    public boolean stop = false;
    float collectedTimeMillis;

    public void setup(int intervalMillis, Runnable runnable) {
        this.intervalMillis = intervalMillis;
        this.runnable = runnable;
    }
}
