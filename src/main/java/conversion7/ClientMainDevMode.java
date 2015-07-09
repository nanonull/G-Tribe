package conversion7;

import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;
import conversion7.game.GdxgConstants;
import conversion7.game.services.WorldServices;

public class ClientMainDevMode {

    public static void main(String[] args) {
        GdxgConstants.DEVELOPER_MODE = true;
        WorldServices.disableFaunaGeneration();
        ClientCore.initWorldFromCore = true;
        ClientCore.initWorldFromWorldQuest = true;
        ClientApplication.startClientCore();
    }

}
