package conversion7.engine;

import net.namekdev.entity_tracker.network.EntityTrackerServer;
import net.namekdev.entity_tracker.network.base.Server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Just to expose runningThread.setDaemon(true);
 */
public class EntityTrackerServerExt extends EntityTrackerServer {

    public EntityTrackerServerExt(int listeningPort) {
        super(listeningPort);
    }

    public Thread getRunningThread() {
        return runningThread;
    }

    @Override
    public Server start() {
        if (socket != null && !socket.isClosed()) {
            throw new IllegalStateException("Cannot serve twice in the same time.");
        }

        try {
            socket = new ServerSocket(listeningPort);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't start server on port " + listeningPort, e);
        }
        runningThread = new Thread(this);
        runningThread.setDaemon(true);
        runningThread.start();

        return this;
    }
}
