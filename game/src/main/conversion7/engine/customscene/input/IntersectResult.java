package conversion7.engine.customscene.input;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.customscene.SceneNode3d;

import java.util.Comparator;
import java.util.Map;

import static java.util.AbstractMap.SimpleEntry;

public class IntersectResult {

    private static Comparator<Map.Entry<SceneNode3d, Float>> COMPARATOR_OBJECT_DISTANCES_TO = new Comparator<Map.Entry<SceneNode3d, Float>>() {
        @Override
        public int compare(Map.Entry<SceneNode3d, Float> o1, Map.Entry<SceneNode3d, Float> o2) {
            return o1.getValue() > o2.getValue() ? 1 : -1;
        }
    };

    /** pickedNodesWithDistance To Camera From PickPoint */
    public Array<Map.Entry<SceneNode3d, Float>> pickedNodesWithDistance = new Array<>();

    public void addNode(SceneNode3d pickedObject, Vector3 whereIntersected) {
        float distanceToCamera = pickedObject.stage.getCamera().position.dst(whereIntersected);
        pickedNodesWithDistance.add(new SimpleEntry(pickedObject, distanceToCamera));
    }

    /** 1st item will be the closest to the camera */
    public void sortNodes() {
        pickedNodesWithDistance.sort(COMPARATOR_OBJECT_DISTANCES_TO);
    }

    public void clear() {
        pickedNodesWithDistance.clear();
    }
}
