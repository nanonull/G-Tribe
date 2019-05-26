package conversion7.game.stages.world.team.events;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.game.Assets;
import conversion7.game.stages.battle_deprecated.BattleSide;
import conversion7.game.stages.world.team.Team;

public class ChaosPeriodStartedEvent extends AbstractEventNotification {

    private String hint;

    public ChaosPeriodStartedEvent(Team team) {
        super(team, new ButtonWithActor(new Image(Assets.ACTOR_TEXTURES.get(BattleSide.RIGHT_RED))));
        hint = "Chaos started!\n " +
                "\nSteps left: " + team.world.getChaosStepsLeft() +
                "\nDamage taken will be randomized from 1 to base value";
    }

    @Override
    public String getHint() {
        return hint;
    }
}
