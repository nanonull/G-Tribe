package conversion7.game.stages.world.team.events;

import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.engine.utils.Utils;
import conversion7.game.interfaces.HintProvider;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;

/** Notify team about some event */
public abstract class AbstractEventNotification implements HintProvider {

    static final Logger LOG = Utils.getLoggerForClass();
    private final Team team;

    private ButtonWithActor icon;

    public AbstractEventNotification(Team team, ButtonWithActor icon) {
        this.icon = icon;
        this.team = team;
    }

    public String getHint() {
        return getClass().getSimpleName();
    }

    public ButtonWithActor getIcon() {
        return icon;
    }

    public void action() {

    }

    public Team getTeam() {
        return team;
    }
}
