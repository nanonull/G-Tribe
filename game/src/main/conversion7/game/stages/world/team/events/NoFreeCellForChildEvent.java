package conversion7.game.stages.world.team.events;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class NoFreeCellForChildEvent extends AbstractEventSquadNotification {
    public NoFreeCellForChildEvent(AbstractSquad squad) {
        super(squad, new ButtonWithActor(new Image(Assets.eyeRed), true));
    }
}
