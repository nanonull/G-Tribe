package conversion7.engine.artemis.audio;

import com.artemis.BaseSystem;
import conversion7.engine.AudioPlayer;

public class PlayerTribeAudioSystem extends BaseSystem {
    private static final float INTERVAL = 0.3f;
    public static boolean metTribe;
    public static boolean newWar;
    public static boolean unitLost;
    float deltaAcc;

    @Override
    protected void processSystem() {
        deltaAcc += getWorld().delta;

        if (deltaAcc >= INTERVAL) {
            deltaAcc -= INTERVAL;

            if (metTribe) {
                metTribe = false;
                AudioPlayer.playTribe();
            }

            if (newWar) {
                newWar = false;
                AudioPlayer.play("fx\\new_skill.mp3").setVolume(0.5f);
            }

            if (unitLost) {
                unitLost = false;
                AudioPlayer.play("fx\\2\\wow_pos1.mp3").setVolume(0.8f);
            }
        }
    }

}
