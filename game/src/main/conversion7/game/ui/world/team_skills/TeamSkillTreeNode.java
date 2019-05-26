package conversion7.game.ui.world.team_skills;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.ActorWithBackground;
import conversion7.engine.custom2d.tree.AbstractTreeNode;
import conversion7.game.Assets;
import conversion7.game.stages.world.team.TeamSkillsManager;
import conversion7.game.stages.world.team.skills.AbstractSkill;
import conversion7.game.stages.world.team.skills.SkillType;
import conversion7.game.ui.hint.PopupHintPanel;

public class TeamSkillTreeNode extends AbstractTreeNode {

    public static long nodeStateRefreshCounter;

    private final ActorWithBackground rootActor;
    private Label skillLevelLabel;
    private SkillType skillType;

    public TeamSkillTreeNode(int rowFromTop, int columnFromLeft, String label) {
        super(rowFromTop, columnFromLeft);
        setColumnWidth((int) (columnWidth * 1.05f));

        TextButton textButton = new TextButton(label, Assets.uiSkin);
        rootActor = new ActorWithBackground(textButton).setPadding(1);
        nodeTable.add(rootActor).size(BASE_SIZE);
        rootActor.setUseFaceSizeForBackgroundLayout(true);
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
        if (skillType.isMultiLevel()) {
            skillLevelLabel = new Label("0/0", Assets.labelStyle14orange);
            nodeTable.row();
            nodeTable.add(skillLevelLabel);
        }
    }

    public void updateNodesState(final TeamSkillsManager teamSkillsManager) {
        if (nodeStateRefreshCounter == nodeStateUpdatedOnCounter) {
            return;
        }
        nodeStateUpdatedOnCounter = nodeStateRefreshCounter;

        updateMe(teamSkillsManager);
        for (AbstractTreeNode childNode : childNodes) {
            ((TeamSkillTreeNode) childNode).updateNodesState(teamSkillsManager);
        }
    }

    private void updateMe(final TeamSkillsManager teamSkillsManager) {
        final AbstractSkill skill = teamSkillsManager.getSkill(skillType);
        if (skill.isFullyLearned()) {
            rootActor.setBackgroundColor(Color.GREEN);
        } else if (skill.isAvailableForLearn()) {
            rootActor.setBackgroundColor(Color.BLUE);
        } else {
            // not available for learn
            if (skill.isPartiallyLearned()) {
                rootActor.setBackgroundColor(Color.ORANGE);
            } else {
                rootActor.setBackgroundColor(Color.GRAY);
            }
        }

        // update multi level indicator
        if (skillLevelLabel != null) {
            skillLevelLabel.setText(String.format("%d/%d", skill.getCurrentLevel(), skill.getLevels()));
        }

        // update hint
        rootActor.getListeners().clear();
        PopupHintPanel.assignHintTo(rootActor, skill.getHint());
        rootActor.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (skill.isAvailableForLearn() && !Gdx.input.isTouched()) {
                    skill.learn();
                    Gdxg.clientUi.getTeamSkillsWindow().refreshContent(teamSkillsManager.getTeam());
                }
            }

        });
    }


}
