package conversion7.game.stages.world.objects.actions;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.ai.tasks.single.ExploreTask;
import conversion7.game.stages.world.objects.AreaObject;

public class ExploreAction extends AbstractAreaObjectAction {

    public ExploreAction(AreaObject object) {
        super(object);
    }

    @Override
    public void execute() {
        AreaObject object = getObject();
        ExploreTask exploreTask = new ExploreTask(object);
        object.setCurrentObjectTask(exploreTask);
        World.getAreaViewer().startInputResolving(exploreTask);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.followIcon;
    }
}
