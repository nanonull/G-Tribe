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
import conversion7.game.stages.world.team.skills.AbstractSkill;
import conversion7.game.stages.world.team.skills.FireSkill;
import conversion7.game.stages.world.team.skills.HandsAsAToolSkill;
import conversion7.game.stages.world.team.skills.LocomotionSkill;
import conversion7.game.stages.world.team.skills.TotemsSkill;
import conversion7.game.stages.world.team.skills.statics.SkillStaticParams;
import conversion7.game.ui.ClientUi;

import java.util.Map;

public class TeamSkillsWindow extends AnimatedWindow {

    public static final int TREE_WIDGET_PADDING = (int) (TeamSkillTreeNode.BUTTON_SIZE * 0.5f);

    TreeWidget treeWidget;


    public TeamSkillsWindow(Stage stage, String title, Skin skin, int direction) {
        super(stage, title, skin, direction);
        addCloseButton();

        Array<AbstractTreeNode> treeNodes = new Array<>();
        for (Map.Entry<Class<? extends AbstractSkill>, SkillStaticParams> classSkillStaticParamsEntry : SkillStaticParams.SKILL_STATIC_PARAMS.entrySet()) {
            treeNodes.add(classSkillStaticParamsEntry.getValue().getTeamSkillTreeNode());

        }


        Array<AbstractTreeNode> rootNodes = new Array<>();
        rootNodes.add(SkillStaticParams.SKILL_STATIC_PARAMS.get(HandsAsAToolSkill.class).getTeamSkillTreeNode());
        rootNodes.add(SkillStaticParams.SKILL_STATIC_PARAMS.get(LocomotionSkill.class).getTeamSkillTreeNode());
        rootNodes.add(SkillStaticParams.SKILL_STATIC_PARAMS.get(FireSkill.class).getTeamSkillTreeNode());
        rootNodes.add(SkillStaticParams.SKILL_STATIC_PARAMS.get(TotemsSkill.class).getTeamSkillTreeNode());

        treeWidget = new TreeWidget(treeNodes, rootNodes);
        add(treeWidget).size(500).expand().fill()
                .pad(TREE_WIDGET_PADDING);
    }

    public void showFor(Team team) {
        if (Gdxg.clientUi.getTeamClassesWindow().isDisplayed()) {
            Gdxg.clientUi.getTeamClassesWindow().hide();
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
