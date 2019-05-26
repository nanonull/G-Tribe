package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.AudioPlayer;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.BorderPanel;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.engine.custom2d.HBox;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.table.Panel;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.unit.actions.ActionEvaluation;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.UiLogger;
import conversion7.game.ui.hint.PopupHintPanel;
import conversion7.game.ui.utils.UiUtils;

public class UnitActionsRowPanel extends Panel {
    private static final float BUTTON_SIDE = 32;
    private static final Color commonGroupColor = UiUtils.alpha(0.2f, Color.YELLOW);
    private static final Color attackGroupColor = UiUtils.alpha(0.2f, Color.RED);
    private static final Color defenceGroupColor = UiUtils.alpha(0.2f, Color.BLUE);
    private static final Color tribeGroupColor = UiUtils.alpha(0.2f, Color.GREEN);
    private static final Color ULT_BORDER_COLOR = Color.WHITE;
    private static final Color IMPORTANT_BORDER_COLOR = Color.GOLD;
    public boolean horizontalGroups = true;
    public boolean enableActions = true;

    public void load(Array<AbstractAreaObjectAction> actions) {
        HBox commonGroup = new HBox();
        commonGroup.pad(ClientUi.HALF_SPACING);
        commonGroup.defaults().spaceLeft(1).spaceRight(1).growY().center();
        commonGroup.setBackground(new TextureRegionColoredDrawable(commonGroupColor, Assets.pixel));
        HBox attackGroup = new HBox();
        attackGroup.pad(ClientUi.HALF_SPACING);
        attackGroup.defaults().spaceLeft(1).spaceRight(1).growY().center();
        attackGroup.setBackground(new TextureRegionColoredDrawable(attackGroupColor, Assets.pixel));
        HBox defenceGroup = new HBox();
        defenceGroup.pad(ClientUi.HALF_SPACING);
        defenceGroup.defaults().spaceLeft(1).spaceRight(1).growY().center();
        defenceGroup.setBackground(new TextureRegionColoredDrawable(defenceGroupColor, Assets.pixel));
        HBox tribeGroup = new HBox();
        tribeGroup.pad(ClientUi.HALF_SPACING);
        tribeGroup.defaults().spaceLeft(1).spaceRight(1).growY().center();
        tribeGroup.setBackground(new TextureRegionColoredDrawable(tribeGroupColor, Assets.pixel));

        actions.sort(ActionEvaluation.AreaObjectActionComparator.instance);
        for (AbstractAreaObjectAction absAction : actions) {
            AbstractSquadAction action = (AbstractSquadAction) absAction;
            HBox subgroup;
            switch (action.getGroup()) {
                case COMMON:
                    subgroup = commonGroup;
                    break;
                case ATTACK:
                    subgroup = attackGroup;
                    break;
                case DEFENCE:
                    subgroup = defenceGroup;
                    break;
                case TRIBE:
                    subgroup = tribeGroup;
                    break;
                default:
                    subgroup = commonGroup;
            }

            Image icon = action.getIcon();
            Actor rootActor;
            TextButton textButton;
            if (icon == null) {
                textButton = new TextButton(action.getShortName(), Assets.uiSkin);
                rootActor = textButton;
            } else {
                ButtonWithActor buttonWithActor = new ButtonWithActor();
                buttonWithActor.setFace(icon);
                buttonWithActor.setKeepActorSize(true);
                rootActor = buttonWithActor;
                textButton = buttonWithActor.getBackground();
            }
            textButton.setColor(Color.GOLD);
            Cell<Actor> actorCell;
            if (action.actionEvaluation.isSuperAbility()) {
                BorderPanel actionRoot = new BorderPanel(1.5f, ULT_BORDER_COLOR);
                actorCell = actionRoot.setActor(rootActor);
                subgroup.add(actionRoot);
            } else if (action.actionEvaluation.isImportant()) {
                BorderPanel actionRoot = new BorderPanel(1, IMPORTANT_BORDER_COLOR);
                actorCell = actionRoot.setActor(rootActor);
                subgroup.add(actionRoot);
            } else {
                actorCell = subgroup.add(rootActor);
            }

            if (icon == null) {
                actorCell.height(BUTTON_SIDE);
            } else {
                actorCell.size(BUTTON_SIDE);
            }
            PopupHintPanel.assignHintTo(rootActor, action.getDescription());

            rootActor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (event.getButton() == Input.Buttons.LEFT) {
                        if (enableActions) {
                            if (Gdxg.core.areaViewer.inputResolver != null) {
                                AudioPlayer.play("fx\\click2.mp3");
                                UiLogger.addInfoLabel("Active action has to be cancelled before");
                                return;
                            }
                            action.run();
                            AudioPlayer.play("fx\\in.mp3");
                        }
                    }
                }
            });
        }


        if (commonGroup.hasChildren()) {
            add(commonGroup).expandY().fillY().center();
            if (!horizontalGroups) {
                row();
            }
        }
        if (attackGroup.hasChildren()) {
            add(attackGroup).expandY().fillY().center();
            if (!horizontalGroups) {
                row();
            }
        }
        if (defenceGroup.hasChildren()) {
            add(defenceGroup).expandY().fillY().center();
            if (!horizontalGroups) {
                row();
            }
        }
        if (tribeGroup.hasChildren()) {
            add(tribeGroup).expandY().fillY().center();
            if (!horizontalGroups) {
                row();
            }
        }
    }
}
