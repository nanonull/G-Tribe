package conversion7.game.stages.world.view;

import com.badlogic.gdx.math.Vector3;
import conversion7.engine.Gdxg;
import conversion7.engine.artemis.AnimationSystem;
import conversion7.engine.artemis.scene.TranslateSystem;
import conversion7.engine.customscene.SceneNode3d;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class AreaViewerAnimationsHelper {
    public static final Map<Object, Boolean> animations = new HashMap<>();
    public static final Map<Object, InWorldActionListener> listeners = new HashMap<>();
    public static boolean showAnimation = true;

    public static void setAnimationCompletedOn(AreaObject squad) {
//        animations.put(squad, false);
//        InWorldActionListener listener = listeners.remove(squad);
//        if (listener != null) {
//            listener.onEvent();
//        }
    }

    public static void setAnimationStartedOn(AreaObject squad, boolean active) {
//        animations.put(squad, active);
//        if (!Gdxg.core.world.isPlayerTeamActive()) {
//            Gdxg.graphic.getCameraController().moveCameraToLookAtWorldCell(squad.getLastCell());
//        }
    }

    public static boolean hasAnimationStarted(AbstractSquad abstractSquad) {
        Boolean act = animations.get(abstractSquad);
        return act != null && act;
    }

    public static void subscribeOnAnimationCompleted(Object key, InWorldActionListener listener) {
        listeners.put(key, listener);
    }

//    @Deprecated
//    public static void highlightAnimation(AreaObject owner, Cell fromCell, Cell targetCell, String title,
//                                          Runnable actionLogic) {
//        AnimationSystem.vectorAnimation(title, fromCell, targetCell);
////        if (shouldShowAnimation(fromCell, targetCell)) {
////            Gdxg.clientUi.getWorldHintPanel().showHint(title);
////            AreaViewer areaViewer = Gdxg.core.areaViewer;
////            SceneNode3d highlightNode = areaViewer.getActionHighlightNode();
////            setAnimationStartedOn(owner, true);
////            BasePollingComponent highlightCell = areaViewer.highlightCell(fromCell, highlightNode);
////            highlightCell.postAction = () -> {
////                startTranslation(owner, fromCell, targetCell, highlightNode);
////            };
////        }
//        actionLogic.run();
//    }

    private static void startTranslation(AreaObject owner, Cell fromCell, Cell targetCell, SceneNode3d highlightNode) {
        Point2s diffWithCell = fromCell.getDiffWithCell(targetCell);
        Vector3 translationVector = MathUtils.toEngineCoords(new Vector3(
                diffWithCell.x,
                diffWithCell.y,
                0.7f));

        TranslateSystem.translateBy(AnimationSystem.ANIM_DURATION, highlightNode, translationVector).appendPostAction(() -> {
            setAnimationCompletedOn(owner);
//            actionLogic.run();
            highlightNode.removeFromParent();
            Gdxg.clientUi.getWorldHintPanel().hide();
        });
    }

    public static boolean shouldShowAnimation(Cell oldCell, Cell targetCell) {
        if (!showAnimation) {
            return false;
        }
        boolean playerSeesAction = Gdxg.core.world.doesPlayerSeeCell(oldCell)
                || Gdxg.core.world.doesPlayerSeeCell(targetCell);
        return playerSeesAction;
    }
}