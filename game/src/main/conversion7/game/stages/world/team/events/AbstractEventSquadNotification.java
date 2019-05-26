package conversion7.game.stages.world.team.events;

import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public abstract class AbstractEventSquadNotification extends AbstractEventPlaceNotification {

    protected AbstractSquad squad;

    public AbstractEventSquadNotification(AbstractSquad squad, ButtonWithActor icon) {
        super(squad.getTeam(), squad.getLastCell(), icon);
        this.squad = squad;
    }

    public AbstractSquad getSquad() {
        return squad;
    }

}
