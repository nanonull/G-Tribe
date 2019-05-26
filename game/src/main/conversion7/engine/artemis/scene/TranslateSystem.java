package conversion7.engine.artemis.scene;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.math.Vector3;
import conversion7.engine.artemis.engine.time.BasePollingSystem;
import conversion7.engine.customscene.SceneNode3d;

public class TranslateSystem extends BasePollingSystem<TranslateComp> {

    private static ComponentMapper<TranslateComp> components;
    static World world;

    public TranslateSystem() {
        super(Aspect.all(TranslateComp.class));
    }

    @Override
    public ComponentMapper<TranslateComp> getMapper() {
        return components;
    }

    public static TranslateComp translateBy(float durationSec, SceneNode3d node, Vector3 translation) {
        TranslateSystem system = world.getSystem(TranslateSystem.class);
        TranslateComp comp = system.schedulePolling("translate", 0, durationSec, null);
        comp.translation = translation;
        comp.node = node;
        comp.callable = () -> {
            system.process(comp);
            return null;
        };

        Vector3 moveTo = new Vector3(comp.node.globalPosition);
        moveTo.add(translation);
        comp.postAction = () -> {
//            node.setPosition(moveTo);
        };
        return comp;
    }

    private void process(TranslateComp comp) {
        float delta = getWorld().delta;
        float deltaRatio = delta / comp.stopAfterSeconds;
        comp.node.translate(comp.translation.x * deltaRatio,
                comp.translation.y * deltaRatio,
                comp.translation.z * deltaRatio
        );
    }

}
