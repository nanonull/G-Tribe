package conversion7.engine.artemis.ui;

import com.artemis.BaseSystem;
import conversion7.engine.AudioPlayer;
import conversion7.engine.Gdxg;
import conversion7.game.stages.world.team.Team;

public class GameEventUiNotificationSystem extends BaseSystem {
    private static final float INTERVAL = 0.3f;
    private static final float NOTIFICATION_TIME_SEC = 2.5f;
    private static final int INTERVALS = (int) Math.ceil(NOTIFICATION_TIME_SEC / INTERVAL);
    float deltaAcc;
    private int notificationStep;
    private int woohSoundAtWorldStep = -1;

    @Override
    protected void processSystem() {
        deltaAcc += getWorld().delta;

        if (deltaAcc >= INTERVAL) {
            deltaAcc -= INTERVAL;

            if (notificationStep == 0) {
                showNotification();
            } else {
                notificationStep++;
                if (notificationStep > INTERVALS) {
                    stopNotification();
                }
            }

        }
    }

    private void showNotification() {
        Team playerTeam = null;
        try {
            playerTeam = Gdxg.core.world.lastActivePlayerTeam;
        } catch (NullPointerException e) {
        }
        if (playerTeam != null && playerTeam.getEventUiNotifications().size > 0) {
            notificationStep++;
            String msg = playerTeam.getEventUiNotifications().removeIndex(0);
            Gdxg.clientUi.getEventUiNotificationPanel().showFor(msg);
            AudioPlayer.play("fx\\new_class.mp3").setVolume(0.2f);
        }
    }

    private void stopNotification() {
        notificationStep = 0;
        Gdxg.clientUi.getEventUiNotificationPanel().hide();
    }
}
