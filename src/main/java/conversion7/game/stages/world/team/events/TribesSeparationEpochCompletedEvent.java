package conversion7.game.stages.world.team.events;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.game.Assets;
import conversion7.game.stages.battle.BattleSide;
import conversion7.game.stages.world.team.Team;

public class TribesSeparationEpochCompletedEvent extends AbstractEventNotification {

    private Team team;

    public TribesSeparationEpochCompletedEvent(Team team) {
        super(team, new ButtonWithActor(new Image(Assets.ACTOR_TEXTURES.get(BattleSide.LEFT))));
    }

}
