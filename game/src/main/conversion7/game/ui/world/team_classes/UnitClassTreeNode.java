package conversion7.game.ui.world.team_classes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.ActorWithBackground;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.engine.custom2d.tree.AbstractTreeNode;
import conversion7.game.Assets;
import conversion7.game.unit_classes.ClassStandard;
import conversion7.game.unit_classes.UnitClassConstants;
import conversion7.game.unit_classes.humans.australopitecus.Australopithecus;
import conversion7.game.stages.world.team.TeamClassesManager;
import conversion7.game.stages.world.team.UnitClassTeamInfo;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.hint.PopupHintPanel;

public class UnitClassTreeNode extends AbstractTreeNode {


    public static long nodeStateRefreshCounter;
    private final ActorWithBackground imageButton;
    private final ActorWithBackground unitsAmountLabelHolder;
    private final Label unitsAmountLabel;
    private Class<? extends Unit> unitClass;
    private State state = State.NOT_EXPLORED;


    public UnitClassTreeNode(int rowFromTop, int columnFromLeft, Class<? extends Unit> unitClass) {
        super(rowFromTop, columnFromLeft);
        this.unitClass = unitClass;

        imageButton = new ActorWithBackground(new ButtonWithActor(
                new Image(Assets.CLASS_ICONS.get(unitClass))));
        imageButton.setPadding(ClientUi.SPACING);
        nodeTable.add(imageButton).size(BASE_SIZE);

        unitsAmountLabel = new Label("", Assets.labelStyle14yellow);
        unitsAmountLabelHolder = new ActorWithBackground(unitsAmountLabel).setPadding(ClientUi.SPACING);
        nodeTable.add(unitsAmountLabelHolder).pad(ClientUi.SPACING)
                .height(BASE_SIZE * 0.6f);

        unitsAmountLabelHolder.setBackgroundColor(Color.GRAY);
        unitsAmountLabelHolder.setUseFaceSizeForBackgroundLayout(true);

        ClassStandard classStandard = UnitClassConstants.CLASS_STANDARDS.get(unitClass);
        String hint = classStandard == null ? "" : classStandard.toString();
        PopupHintPanel.assignHintTo(imageButton, hint);
    }

    public ActorWithBackground getUnitsAmountLabelHolder() {
        return unitsAmountLabelHolder;
    }

    public Label getUnitsAmountLabel() {
        return unitsAmountLabel;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public ActorWithBackground getImageButton() {
        return imageButton;
    }

    public void updateNodesState(TeamClassesManager teamClassesManager) {
        if (nodeStateRefreshCounter == nodeStateUpdatedOnCounter) {
            return;
        }
        nodeStateUpdatedOnCounter = nodeStateRefreshCounter;

        UnitClassTeamInfo unitClassTeamInfo = teamClassesManager.getTeamInfo(unitClass);
        updateMyState(unitClassTeamInfo);
        getImageButton().setBackgroundColor(state.getValue());

        for (AbstractTreeNode childNode : childNodes) {
            ((UnitClassTreeNode) childNode).updateNodesState(teamClassesManager);
        }
    }

    private void updateMyState(UnitClassTeamInfo unitClassTeamInfo) {
        State newState = State.NOT_EXPLORED;
        if (unitClassTeamInfo != null) {
            newState = State.EXPLORED;
            unitsAmountLabel.setText(unitClassTeamInfo.getClassStandard().classShortName + ":"
                    + String.valueOf(unitClassTeamInfo.getAmount()));
            unitsAmountLabel.pack();
            unitsAmountLabelHolder.setVisible(true);
        } else {
            unitsAmountLabelHolder.setVisible(false);
            for (AbstractTreeNode parentNode : parentNodes) {
                if (((UnitClassTreeNode) parentNode).getState().equals(State.EXPLORED)) {
                    newState = State.NEXT_AFTER_EXPLORED;
                    break;
                }
            }
        }

        setState(newState);
    }

    public enum State {
        EXPLORED(Color.GREEN),
        NEXT_AFTER_EXPLORED(Color.BLUE),
        NOT_EXPLORED(Color.GRAY),;

        private final Color value;

        State(Color value) {
            this.value = value;
        }

        public Color getValue() {
            return value;
        }
    }
}
