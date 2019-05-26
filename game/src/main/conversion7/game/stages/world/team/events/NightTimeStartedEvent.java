package conversion7.game.stages.world.team.events;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.game.Assets;
import conversion7.game.stages.battle_deprecated.BattleSide;
import conversion7.game.stages.world.elements.SoulType;
import conversion7.game.stages.world.team.Team;

public class NightTimeStartedEvent extends AbstractEventNotification {

    private String hint;

    public NightTimeStartedEvent(Team team) {
        super(team, new ButtonWithActor(new Image(Assets.ACTOR_TEXTURES.get(BattleSide.UP_BLUE))));
        hint = "Night started!" +
                "\nNight and Moon souls get +" + SoulType.NIGHT_HEAL_BONUS + " heal bonus";
    }

    @Override
    public String getHint() {
        return hint;
    }
}
