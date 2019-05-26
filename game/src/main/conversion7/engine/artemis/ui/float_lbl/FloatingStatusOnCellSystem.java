package conversion7.engine.artemis.ui.float_lbl;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.engine.Gdxg;
import conversion7.engine.artemis.engine.time.SchedulingSystem;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.customscene.SceneNode3d;
import conversion7.engine.customscene.SceneNode3dWith2dActor;
import conversion7.engine.tween.Node3dAccessor;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.view.AreaViewer;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.utils.UiUtils;
import org.slf4j.Logger;
import org.testng.internal.collections.Pair;

public class FloatingStatusOnCellSystem extends BaseSystem {
    public static final float FLOATING_TIME_MLT = 0.12f;
    public static final Color BACK_COLOR = UiUtils.alpha(0.6f, ClientUi.PANEL_COLOR, false);
    private static final Logger LOG = Utils.getLoggerForClass();
    private static final Label.LabelStyle LABEL_STYLE;
    public static ObjectMap<Cell, Pair<Team, Pair<String, Color>>> messages = new ObjectMap<>();

    static {
        LABEL_STYLE = new Label.LabelStyle(Assets.labelStyle18yellow);
        LABEL_STYLE.background = new TextureRegionColoredDrawable(BACK_COLOR, Assets.pixel);
    }

    public static void scheduleMessage(Unit unit, String msg) {
        scheduleMessage(unit.squad.getLastCell(), unit.squad.team, msg, Assets.WHITE_AND_LITTLE_YELLOW);
    }

    public static void scheduleMessage(Unit unit, String msg, Color color) {
        scheduleMessage(unit.squad.getLastCell(), unit.squad.team, msg, color);
    }

    public static void scheduleMessage(Cell cell, Team team, String msg) {
        scheduleMessage(cell, team, msg, Assets.WHITE_AND_LITTLE_YELLOW);
    }

    public static void scheduleMessage(Cell cell, Team team, String msg, Color color) {
        if (msg.contains("WeakeningEff")) {
            LOG.error("");
        }
        if (cell == null) {
            LOG.error("scheduleMessage cell is null");
            return;
        }

        if (messages.containsKey(cell)) {
            // postpone UnitFloatingStatus - wait prev label on cell
            SchedulingSystem.schedule("postpone UnitFloatingStatus", 250, () -> {
                scheduleMessage(cell, team, msg, color);
            });
        } else {
            messages.put(cell, new Pair<>(team, new Pair<>(msg, color)));
        }
    }

    private static boolean shouldPostponeDueToPlayerView(Cell cell) {
        Team playerTeam = cell.getArea().world.lastActivePlayerTeam;
        return playerTeam != null && cell.hasSquad() && cell.squad.team == playerTeam;
    }

    @Override
    protected void processSystem() {
        for (ObjectMap.Entry<Cell, Pair<Team, Pair<String, Color>>> unitPairEntry : messages.entries()) {
            Cell cell = unitPairEntry.key;
            Team team = unitPairEntry.value.first();
            Pair<String, Color> secondPair = unitPairEntry.value.second();
            String msg = secondPair.first();
            Color color = secondPair.second();
            if (cell.isVisibleOnView()) {
                if (Gdxg.core.world.lastActivePlayerTeam == null || Gdxg.core.world.lastActivePlayerTeam == team) {
                    displayFloatingStatusTextLabel(msg, color, cell);
                }
            }
        }
        messages.clear();
    }

    private void displayFloatingStatusTextLabel(String msg, Color color, Cell cell) {

        Label label = new Label(msg, LABEL_STYLE);
        label.setColor(color);
        Gdxg.graphic.getGlobalStage().addActor(label);

        SceneGroup3d container = new SceneGroup3d();
        SceneNode3dWith2dActor hpLabelNode = new SceneNode3dWith2dActor(
                label);
        AreaViewer.placeBodyOnCell(cell, hpLabelNode);
        container.addNode(hpLabelNode);

        SceneNode3d superContainer = container;
        cell.getArea().getSceneGroup().addNode(superContainer);
        // TODO investigate bug in scene: to translate node it need to be wrapped into group
//        cell.squad.sceneBody.addNode(hpLabelNode);

        float startY = 1;
        float endY = startY * 3.5f;
        superContainer.setY(startY);
        superContainer.setX(-0.25f);
        superContainer.setZ(-0.25f);

        Tween.to(superContainer, Node3dAccessor.POSITION_XYZ, Math.max(2, msg.length() * FLOATING_TIME_MLT))
                .target(superContainer.localPosition.x,
                        endY,
                        superContainer.localPosition.z)
                .setCallback(new LabelMoveCallback(label, superContainer))
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(Gdxg.tweenManager);

    }

    public static class LabelMoveCallback implements TweenCallback {

        private final Label hpLabel;
        private final SceneNode3d sceneNode3d;

        public LabelMoveCallback(Label hpLabel, SceneNode3d sceneNode3d) {
            this.hpLabel = hpLabel;
            this.sceneNode3d = sceneNode3d;
        }

        @Override
        public void onEvent(int i, BaseTween<?> baseTween) {
            sceneNode3d.removeFromParent();
            hpLabel.remove();
        }
    }
}