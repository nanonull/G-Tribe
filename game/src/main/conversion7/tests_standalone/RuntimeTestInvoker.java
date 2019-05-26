package conversion7.tests_standalone;

import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.run.RunAndScheduleLibrary;
import conversion7.game.ui.UiLogger;
import org.slf4j.Logger;

public class RuntimeTestInvoker {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static boolean testRun;

    public static void run() {

        if (!testRun) {
            testRun = true;
            UiLogger.addInfoLabel("--- RuntimeTestInvoker ---");

            LOG.info("{}", Gdxg.core.world.teams);
//            BaalsMainCampaign.theyLand();
//            AbstractSquad squad = Gdxg.core.world.playerTeam.getSquads().first();
//            FloatingStatusOnCellSystem.scheduleMessage(squad.unit, "test");
//
//            squad.getBaseParams().update(UnitParameterType.STRENGTH, +2);

            RunAndScheduleLibrary.scheduleSingleExecution(200, () -> RuntimeTestInvoker.testRun = false);
        }
    }


}
