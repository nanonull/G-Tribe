package conversion7.tests_standalone;

import conversion7.engine.utils.Utils;
import conversion7.game.run.RunAndScheduleLibrary;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.HumanSquad;
import conversion7.game.ui.UiLogger;
import org.slf4j.Logger;

public class RuntimeTestInvoker {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static boolean testRun;

    public static void run() {

        if (!testRun) {
            testRun = true;
            UiLogger.addInfoLabel("--- RuntimeTestInvoker ---");

//            World.getPlayerTeam().getArmies().get(0).defeat();
//            testAnotherTeamAddedObjectInFog();
            World.getPlayerTeam().updateEvolutionSubPointsOn(1000);

            RunAndScheduleLibrary.scheduleSingleExecution(200, new Runnable() {
                @Override
                public void run() {
                    RuntimeTestInvoker.testRun = false;
                }
            });
        }
    }

    private static void testPlayerTeamAddedObjectInFog() {
        WorldServices.createHumanSquadWithSomeUnit(World.getPlayerTeam(), World.getArea(0, 1).getCell(5, 5));
    }

    private static void testAnotherTeamAddedObjectInFog() {
        Cell cellWithPlayerArmy = World.getAreaViewer().getFocusedArea().getCell(5, 5);
        Cell targetCell = World.getAreaViewer().getFocusedArea().getCell(cellWithPlayerArmy, 0, 4);
        HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.getAnimalTeam(), targetCell);
    }


}
