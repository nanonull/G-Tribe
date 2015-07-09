package conversion7.game.ui.world.team_skills;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.ActorWithBackground;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.engine.custom2d.tree.AbstractTreeNode;
import conversion7.game.Assets;
import conversion7.game.stages.world.team.TeamSkillsManager;
import conversion7.game.stages.world.team.skills.AbstractSkill;
import conversion7.game.stages.world.team.skills.statics.SkillStaticParams;
import conversion7.game.ui.HintForm;

public class TeamSkillTreeNode extends AbstractTreeNode {

    public static long nodeStateRefreshCounter;

    private final ActorWithBackground imageButton;
    private Label skillLevelLabel;
    private SkillStaticParams skillStaticParams;

    public TeamSkillTreeNode(int row, int column) {
        super(row, column);

        imageButton = new ActorWithBackground(new ButtonWithActor(new Image(Assets.homeIcon)));
        nodeTable.add(imageButton).size(BUTTON_SIZE);
    }

    public void setSkillParams(SkillStaticParams skillStaticParams) {
        this.skillStaticParams = skillStaticParams;
        if (skillStaticParams.isMultiLevel()) {
            skillLevelLabel = new Label("0/0", Assets.labelStyle14orange);
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
        final AbstractSkill skill = teamSkillsManager.getSkill(skillStaticParams.getSkillClass());
        if (skill.isLearned()) {
            imageButton.setBackgroundColor(Color.GREEN);
        } else if (skill.isAvailableForLearn()) {
            imageButton.setBackgroundColor(Color.BLUE);
        } else {
            // not available for learn
            if (skill.isPartiallyLearned()) {
                imageButton.setBackgroundColor(Color.ORANGE);
            } else {
                imageButton.setBackgroundColor(Color.GRAY);
            }
        }

        // update multi level indicator
        if (skillLevelLabel != null) {
            skillLevelLabel.setText(String.format("%d/%d", skill.getCurrentLevel(), skill.getLevels()));
        }

        // update hint
        imageButton.getListeners().clear();
        HintForm.assignHintTo(imageButton, skill.getHint());
        imageButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return button == Input.Buttons.LEFT;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (skill.isAvailableForLearn() && !Gdx.input.isTouched()) {
                    skill.learn();
                    Gdxg.clientUi.getTeamSkillsWindow().refreshContent(teamSkillsManager.getTeam());
                }
            }
        });
    }


}
