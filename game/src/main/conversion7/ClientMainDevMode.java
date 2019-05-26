package conversion7;

import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;
import conversion7.engine.artemis.engine.GdxgDefaultArtemisBuilder;
import conversion7.engine.artemis.engine.time.SchedulingSystem;
import conversion7.game.GdxgConstants;

public class ClientMainDevMode {

    public static void main(String[] args) {
        GdxgConstants.DEVELOPER_MODE = true;
        GdxgConstants.AREA_OBJECT_AI = false;
        GdxgConstants.BATTLE_AI = true;
        ClientCore.initWorldFromWorldQuest = false;
        ClientCore clientCore = new ClientCore();
        ClientApplication.startLibgdxCore(clientCore);
        ClientApplication.startGameEngine(GdxgConstants.WORLD_SETTINGS_DEV_MODE,
                new GdxgDefaultArtemisBuilder(clientCore));

        SchedulingSystem.schedule("setup debug suite", 0, () -> {
            WorldDebugSuite.runSuite2_battleTest(clientCore);
        });
    }

}
