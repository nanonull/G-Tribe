package conversion7.engine;

import conversion7.engine.utils.Utils;
import net.namekdev.entity_tracker.network.base.Client;
import net.namekdev.entity_tracker.network.base.PersistentClient;
import net.namekdev.entity_tracker.network.base.Server;
import net.namekdev.entity_tracker.network.communicator.ExternalInterfaceCommunicator;
import net.namekdev.entity_tracker.ui.EntityTrackerMainWindow;
import org.slf4j.Logger;

public class MainArtemisEntityTracker {
    private static final Logger LOG = Utils.getLoggerForClass();

    public static void main(String[] args) {
        String serverName = args.length > 0 ? args[0] : "localhost";
        int serverPort = Server.DEFAULT_PORT;

        LOG.info("Start at {}:{}", serverName, serverPort);
        init(serverName, serverPort);
    }

    public static void init(final String serverName, final int serverPort) {
        final EntityTrackerMainWindow window = new EntityTrackerMainWindow(true);
        final Client client = new PersistentClient(new ExternalInterfaceCommunicator(window));
        client.connect(serverName, serverPort);
    }
}
