package conversion7.engine.artemis.scene;

import com.badlogic.gdx.math.Vector3;
import conversion7.engine.artemis.engine.time.BasePollingComponent;
import conversion7.engine.customscene.SceneNode3d;

public class TranslateComp extends BasePollingComponent {
    public SceneNode3d node;
    public Vector3 translation;
}
