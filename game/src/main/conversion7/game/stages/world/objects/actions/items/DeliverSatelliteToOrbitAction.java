package conversion7.game.stages.world.objects.actions.items;

import conversion7.engine.Gdxg;
import conversion7.game.dialogs.DeliverSatelliteToOrbitDialog;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.objects.buildings.SpaceShip;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class DeliverSatelliteToOrbitAction extends AbstractSquadAction {
    public DeliverSatelliteToOrbitAction() {
        super(Group.TRIBE);
    }

    @Override
    public String getShortName() {
        return "DelSat";
    }

    @Override
    public void begin() {
        AbstractSquad squad = getSquad();
        if (squad.team.ironFactory == null) {
            Gdxg.clientUi.getInfoDialog().show("satellite required", "Build satellite before start mission");
            cancel();
            return;
        }


        if (!squad.team.hasSatellite()) {
            Gdxg.clientUi.getInfoDialog().show("satellite required", "Build satellite before start mission");
            cancel();
            return;
        }

        SpaceShip spaceShip = null;
        for (Cell cell : squad.getLastCell().getCellsAround()) {
            spaceShip = cell.getObject(SpaceShip.class);
            if (spaceShip != null) {
                break;
            }
        }
        if (spaceShip == null) {
            Gdxg.clientUi.getInfoDialog().show("space ship required", "Space ship should be on adjacent cell");
            cancel();
            return;
        }


        if (!DeliverSatelliteToOrbitDialog.hasEnoughUran(squad.team)) {
            Gdxg.clientUi.getInfoDialog().show("uranus required", "You need " + DeliverSatelliteToOrbitDialog.URAN_QTY_REQ + " isotopes to start ship");
            cancel();
            return;
        }

        new DeliverSatelliteToOrbitDialog(squad, spaceShip).start();
    }

    @Override
    protected String buildDescription() {
        return "Start mission to deliver satellite on orbit\n " +
                "\nMission requirements:" +
                "\n * build iron factory (to repair spaceship)" +
                "\n * build uranus factory and gather isotopes (fuel for spaceship): " + DeliverSatelliteToOrbitDialog.URAN_QTY_REQ +
                "\n * build communication satellite " +
                "\n " +
                "\nOptional:" +
                "\n * get crew on mission";
    }
}
