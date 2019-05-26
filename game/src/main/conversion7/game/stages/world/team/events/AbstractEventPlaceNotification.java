package conversion7.game.stages.world.team.events;

import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.team.Team;

public abstract class AbstractEventPlaceNotification extends AbstractEventNotification {

    protected Cell cell;

    public AbstractEventPlaceNotification(Team team, Cell cell, ButtonWithActor icon) {
        super(team, icon);
        this.cell = cell;
    }

    public Cell getCell() {
        return cell;
    }

    @Override
    public void action() {
        LOG.info("action {}", cell);
        Gdxg.graphic.getCameraController().moveCameraToLookAtWorldCell(cell);
        Gdxg.core.areaViewer.selectCell(cell);
    }
}
