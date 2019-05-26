package conversion7.game.stages.world.team.events;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class NotEnoughResourcesHurtEvent extends AbstractEventSquadNotification {

    public NotEnoughResourcesHurtEvent(AbstractSquad object) {
        super(object, new ButtonWithActor(new Image(Assets.eyeGreenRed), true));
    }

}
