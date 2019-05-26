package conversion7.engine.artemis;

import com.artemis.BaseSystem;
import com.badlogic.gdx.math.Vector3;
import conversion7.engine.Gdxg;
import conversion7.engine.artemis.engine.time.PollingComponent;
import conversion7.engine.artemis.engine.time.PollingSystem;
import conversion7.engine.artemis.scene.TranslateSystem;
import conversion7.engine.customscene.SceneNode3d;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.view.AreaViewer;
import org.slf4j.Logger;

import java.util.UUID;

public class AnimationSystem extends BaseSystem {

    public static final float ANIM_DURATION = 1f;
    public static final float ANIM_DURATION2 = ANIM_DURATION * 2;
    public static final int ANIM_DURATION_MS = (int) (1000 * ANIM_DURATION);
    public static final float PAUSE_BETWEEN_ANIMATIONS = 1f;
    private static final float VECTOR_ANIM_Z = 1.5f;
    private static final Logger LOG = Utils.getLoggerForClass();
    //    private static int animDurationMs;
    static UUID locker;
    static AnimationSystem animationSystem;
    private static boolean locked;
    private float lockLeft;

    public static boolean isLocking() {
//        return locked;
        return animationSystem.lockLeft > 0;
    }

    public static void lockAnimation() {
        lockAnimation(ANIM_DURATION);
    }

    public static void lockAnimation(float animDuration) {
        animationSystem.lockLeft = Math.max(animDuration, animationSystem.lockLeft);
    }

    public static void vectorAnimation(String title, Cell fromCell, Cell targetCell) {
        float animDuration = ANIM_DURATION2;
        lockAnimation(animDuration + PAUSE_BETWEEN_ANIMATIONS);
        Gdxg.clientUi.getWorldHintPanel().showHint(title);
        AreaViewer areaViewer = Gdxg.core.areaViewer;
        SceneNode3d highlightNode = areaViewer.getActionHighlightNode();

        Point2s diffWithCell = fromCell.getDiffWithCell(targetCell);
        Vector3 translationVector = MathUtils.toEngineCoords(new Vector3(
//                1,0,0
                diffWithCell.x,
                diffWithCell.y,
                0
        ));

        int stepDurationMs = (int) (animDuration * 1000 / 5);
        PollingComponent animationInProgress = PollingSystem.schedule("animation in progress", stepDurationMs, animDuration, () -> {
            if (fromCell.getArea().areaView == null) {
                return false;
            }
            Point2s positionOnViewInCells = fromCell.getGamePosOnViewInCells();
            Vector3 positionOnViewVec = new Vector3(positionOnViewInCells.x + 0.35f, positionOnViewInCells.y + 0.35f,
                    fromCell.getLandscape().getTerrainVertexData().getHeight() + VECTOR_ANIM_Z);
            highlightNode.setPosition(MathUtils.toEngineCoords(positionOnViewVec));
            if (highlightNode.getParent() == null) {
                Gdxg.core.areaViewer.getStage().addNode(highlightNode);
            }

            TranslateSystem.translateBy(stepDurationMs / 1000f, highlightNode, translationVector);
            return false;
        });
        animationInProgress.appendPostAction(() -> {
            Gdxg.clientUi.getWorldHintPanel().hide();
            highlightNode.removeFromParent();
        });
    }

    @Override
    protected void processSystem() {
        lockLeft -= world.getDelta();
        if (lockLeft < 0) {
            lockLeft = 0;
        }
    }
}
