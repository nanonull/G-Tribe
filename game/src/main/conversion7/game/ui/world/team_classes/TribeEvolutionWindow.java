package conversion7.game.ui.world.team_classes;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.custom2d.tree.AbstractTreeNode;
import conversion7.engine.custom2d.tree.TreeWidget;
import conversion7.game.GdxgConstants;
import conversion7.game.unit_classes.UnitClassConstants;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.ClientUi;

import java.util.Map;

public class TribeEvolutionWindow extends AnimatedWindow {

    public static final int TREE_WIDGET_PADDING = (int) (UnitClassTreeNode.BASE_SIZE * 0.5f);
    private TreeWidget treeWidget;

    public TribeEvolutionWindow(Stage stage, String title, Skin skin, int direction) {
        super(stage, title, skin, direction);
        addCloseButton();

        Array<AbstractTreeNode> treeNodes = new Array<>();
        for (Map.Entry<Class<? extends Unit>, UnitClassTreeNode> nodeEntry : UnitClassConstants.DESIGN_TABLE_THE_OLDEST_CLASSES.entrySet()) {
            treeNodes.add(nodeEntry.getValue());
        }

        Array<AbstractTreeNode> rootNodes = new Array<>();
        rootNodes.add(UnitClassConstants.DESIGN_TABLE_THE_OLDEST_CLASSES.get(UnitClassConstants.BASE_HUMAN_CLASS));

        treeWidget = new TreeWidget(treeNodes, rootNodes);
        add(treeWidget).size(500).expand().fill()
                .pad(TREE_WIDGET_PADDING);
    }

    public void showFor(Team team) {
        if (Gdxg.clientUi.getTeamSkillsWindow().isDisplayed()) {
            Gdxg.clientUi.getTeamSkillsWindow().hide();
        }

        refreshContent(team);
        pack();

        setPosition(Gdxg.clientUi.getTeamBar().getX() - ClientUi.SPACING - getPrefWidth(),
                GdxgConstants.SCREEN_HEIGHT_IN_PX - ClientUi.SPACING - getPrefHeight());

        updateAnimationBounds();
        show();
    }

    private void refreshContent(Team team) {
        UnitClassTreeNode.nodeStateRefreshCounter++;
        for (AbstractTreeNode abstractTreeNode : treeWidget.getRootNodes()) {
            ((UnitClassTreeNode) abstractTreeNode).updateNodesState(team.getTeamClassesManager());
        }
    }

}
