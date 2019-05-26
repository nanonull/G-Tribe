package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.stages.world.ai_deprecated.tasks.single.FollowTaskOldOld;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

@Deprecated
public class FollowAction extends AbstractWorldTargetableAction {

    public FollowAction() {
        super(Group.COMMON);
    }

    public int getDistance() {
        return 1;
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.exploreIcon;
    }

    @Override
    public String getActionWorldHint() {
        return "follow";
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return null;
    }

    @Override
    public String buildDescription() {
        return getName() + "\n";
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        AbstractSquad squad = getSquad();
        FollowTaskOldOld followTaskOld = new FollowTaskOldOld(squad);
        squad.setActiveTask(followTaskOld);
        followTaskOld.setTarget(input.getSquad());
    }
}
