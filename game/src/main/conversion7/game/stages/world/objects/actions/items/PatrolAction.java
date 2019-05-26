package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.stages.world.ai_deprecated.tasks.single.PatrolTask;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

@Deprecated
public class PatrolAction extends AbstractSquadAction {

    public PatrolAction() {
        super(Group.DEFENCE);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.patrolIcon;
    }

    @Override
    public String buildDescription() {
        return getName() + "\n \n";
    }

    @Override
    public void begin() {
        AbstractSquad object = getSquad();
        object.setActiveTask(new PatrolTask(object));
    }

    @Override
    public void cancel() {
        AbstractSquad squad = getSquad();
        if (squad.getActiveTask() != null && squad.getActiveTask().getClass() == PatrolTask.class) {
            squad.setActiveTask(null);
        }
    }
}
