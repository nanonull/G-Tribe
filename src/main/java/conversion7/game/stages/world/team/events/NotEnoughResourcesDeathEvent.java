package conversion7.game.stages.world.team.events;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.AreaObject;

public class NotEnoughResourcesDeathEvent extends AbstractEventPlaceNotification {

    private AreaObject object;

    public NotEnoughResourcesDeathEvent(AreaObject object) {
        super(object.getTeam(), new ButtonWithActor(new Image(Assets.eyeRed), true),
                object.getCell());
        this.object = object;
    }
}
