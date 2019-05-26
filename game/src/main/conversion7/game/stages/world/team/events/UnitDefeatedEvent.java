package conversion7.game.stages.world.team.events;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import org.apache.commons.lang3.StringUtils;

public class UnitDefeatedEvent extends AbstractEventSquadNotification {

    public UnitDefeatedEvent(AbstractSquad squad) {
        super(squad, new ButtonWithActor(new Image(Assets.eyeRed), true));
    }

    @Override
    public String getHint() {
        StringBuilder stringBuilder = new StringBuilder()
                .append(super.getHint()).append("\n\n");

        stringBuilder.append(squad.getFullName()).append("...");
        if (squad.willDieOfAge) {
            stringBuilder.append("\nDied of age...");
        } else {
            stringBuilder.append("\nDied at ")
                    .append(squad.getAgeName()).append(" age");
            if (squad.killedBy != null) {
                stringBuilder.append("\n...and killed by ").append(squad.killedBy.getFullName());
                if (squad.killedBy.team != null) {
                    stringBuilder.append("\n...from ").append(squad.killedBy.team.getName());
                }
            }
        }

        String exceptionalStatusHint = squad.getExceptionalStatusHint();
        if (!StringUtils.isEmpty(exceptionalStatusHint)) {
            stringBuilder.append("\nThis ").append(squad.getGenderUi())
                    .append(" was a great person: ").append(exceptionalStatusHint).append("...");
        }

        return stringBuilder.toString();
    }
}
