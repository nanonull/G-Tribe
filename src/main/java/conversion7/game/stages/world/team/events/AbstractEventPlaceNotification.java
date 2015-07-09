package conversion7.game.stages.world.team.events;

import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.team.Team;

public abstract class AbstractEventPlaceNotification extends AbstractEventNotification {

    private Cell cell;

    AbstractEventPlaceNotification(Team team, ButtonWithActor icon, Cell cell) {
        super(team, icon);
        this.cell = cell;
    }

    @Override
    public void action() {
        LOG.info("AbstractEventPlaceNotification.action " + cell);
        Gdxg.graphic.getCameraController().moveCameraToLookAtWorldCell(cell);
    }
}
