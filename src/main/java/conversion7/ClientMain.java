package conversion7;

import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;

public class ClientMain {

    public static void main(String[] args) {
        ClientCore.initWorldFromCore = true;
        ClientCore.initWorldFromWorldQuest = true;
        ClientApplication.startClientCore();
    }

}
