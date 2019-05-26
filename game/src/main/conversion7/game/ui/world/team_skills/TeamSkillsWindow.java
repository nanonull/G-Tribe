package conversion7.game.ui.world.team_skills;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.custom2d.tree.AbstractTreeNode;
import conversion7.engine.custom2d.tree.TreeWidget;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.skills.SkillType;
import conversion7.game.ui.ClientUi;

public class TeamSkillsWindow extends AnimatedWindow {

    public static final int TREE_WIDGET_PADDING = (int) (TeamSkillTreeNode.BASE_SIZE * 0.5f);

    TreeWidget treeWidget;


    public TeamSkillsWindow(Stage stage, String title, Skin skin, int direction) {
        super(stage, title, skin, direction);
        addCloseButton();

        Array<AbstractTreeNode> treeNodes = new Array<>();
        Array<AbstractTreeNode> rootNodes = new Array<>();
        for (SkillType skillType : SkillType.values()) {
            treeNodes.add(skillType.teamSkillTreeNode);
            if (skillType.parentSkillTypes.isEmpty()) {
                rootNodes.add(skillType.teamSkillTreeNode);
            }
        }

        treeWidget = new TreeWidget(treeNodes, rootNodes);
        add(treeWidget).size(700).grow()
                .pad(TREE_WIDGET_PADDING);
    }

    public void showFor(Team team) {
        if (Gdxg.clientUi.getTribeEvolutionWindow().isDisplayed()) {
            Gdxg.clientUi.getTribeEvolutionWindow().hide();
        }
        refreshContent(team);
        pack();

        setPosition(Gdxg.clientUi.getTeamBar().getX() - ClientUi.SPACING - getPrefWidth(),
                GdxgConstants.SCREEN_HEIGHT_IN_PX - ClientUi.SPACING - getPrefHeight());

        updateAnimationBounds();
        show();
    }

    public void refreshContent(final Team team) {
        TeamSkillTreeNode.nodeStateRefreshCounter++;
        for (AbstractTreeNode abstractTreeNode : treeWidget.getRootNodes()) {
            ((TeamSkillTreeNode) abstractTreeNode).updateNodesState(team.getTeamSkillsManager());
        }

    }
}
