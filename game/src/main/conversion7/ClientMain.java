package conversion7;

import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;
import conversion7.engine.artemis.engine.GdxgDefaultArtemisBuilder;
import conversion7.game.GdxgConstants;

public class ClientMain {

    public static void main(String[] args) {
        ClientCore.initWorldFromWorldQuest = true;
        ClientCore clientCore = new ClientCore();
        ClientApplication.startLibgdxCore(clientCore);
        ClientApplication.startGameEngine(GdxgConstants.WORLD_SETTINGS_GAME
                , new GdxgDefaultArtemisBuilder(clientCore));
    }

}
