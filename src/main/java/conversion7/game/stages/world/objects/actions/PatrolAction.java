package conversion7.game.stages.world.objects.actions;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.ai.tasks.single.PatrolTask;
import conversion7.game.stages.world.objects.AreaObject;

public class PatrolAction extends AbstractAreaObjectAction {

    public PatrolAction(AreaObject object) {
        super(object);
    }

    @Override
    public void execute() {
        AreaObject object = getObject();
        object.setCurrentObjectTask(new PatrolTask(object));
        World.getAreaViewer().refreshSelectionBars();
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.patrolIcon;
    }
}
