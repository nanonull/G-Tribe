package conversion7.engine.artemis.engine.time;

import com.artemis.Component
import groovy.transform.ToString;

@ToString(includeFields = true, includeNames = true, includePackage = false
        , includes = ["name", "delayTimeMillis", "killEntity"]
//        , excludes = ['metaClass']
)
public class SchedulingComponent extends Component {
    public String name;
    public Runnable runnable;
    protected float collectedTimeMillis;
    public float delayTimeMillis;
    public boolean killEntity = true;
}