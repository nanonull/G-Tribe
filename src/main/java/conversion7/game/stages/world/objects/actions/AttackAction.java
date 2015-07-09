package conversion7.game.stages.world.objects.actions;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.ai.tasks.single.AttackTask;
import conversion7.game.stages.world.objects.AreaObject;

public class AttackAction extends AbstractAreaObjectAction {

    public AttackAction(AreaObject object) {
        super(object);
    }

    @Override
    public void execute() {
        AreaObject object = getObject();
        AttackTask attackTask = new AttackTask(object);
        object.setCurrentObjectTask(attackTask);
        World.getAreaViewer().startInputResolving(attackTask);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.swordIcon;
    }
}
