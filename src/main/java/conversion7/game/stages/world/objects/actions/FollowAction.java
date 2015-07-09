package conversion7.game.stages.world.objects.actions;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.ai.tasks.single.FollowTask;
import conversion7.game.stages.world.objects.AreaObject;

public class FollowAction extends AbstractAreaObjectAction {

    public FollowAction(AreaObject object) {
        super(object);
    }

    @Override
    public void execute() {
        AreaObject object = getObject();
        FollowTask followTask = new FollowTask(object);
        object.setCurrentObjectTask(followTask);
        World.getAreaViewer().startInputResolving(followTask);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.exploreIcon;
    }
}
