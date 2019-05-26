package conversion7.engine.artemis.engine.time

import com.artemis.Component
import groovy.transform.ToString

import java.util.concurrent.Callable

@ToString(includeFields = true, includeNames = true, includePackage = false, excludes = ['metaClass'])
public class BasePollingComponent extends Component {

    /**Executed on each step*/
    public Callable<Boolean> callable;
    public Runnable postAction;
    public float intervalSeconds;
    protected float collectedIntervalSeconds;
    public float collectedTotalSeconds
    public String name
    public Float stopAfterSeconds
    public UUID entityUuid

    public void appendPostAction(Runnable newPostAction) {
        if (postAction == null) {
            postAction = newPostAction;
        } else {
            def prevAction = postAction
            postAction = new Runnable() {
                @Override
                void run() {
                    prevAction.run()
                    newPostAction.run()
                }
            }
        }
    }
}
