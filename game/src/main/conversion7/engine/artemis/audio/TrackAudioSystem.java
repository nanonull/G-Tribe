package conversion7.engine.artemis.audio;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.AudioPlayer;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;

public class TrackAudioSystem extends BaseSystem {
    public static final int TRANS_TIME_FROM_MAX_TO_MIN = 15;
    private static final float INTERVAL = 0.5f;
    public static final int TRANS_STEPS_FROM_MAX_TO_MIN = (int) (TRANS_TIME_FROM_MAX_TO_MIN / INTERVAL);
    private static final float MIN_VOL = 0.3f;
    private static final float MAX_VOL = 0.9f;
    public static final float VOL_STEP = (MAX_VOL - MIN_VOL) / TRANS_STEPS_FROM_MAX_TO_MIN;
    private static final float MID_VOL = (MAX_VOL - MIN_VOL) / 2;
    private static final String TRACKS_PATH = "track\\";
    private static final float BELOW_MIN_VOL_TRANSITION_MLT = 1.03f;
    float deltaAcc;
    private Array<String> trackList = new Array<>();
    private Music activeTrack;
    private float masterVol = 1;

    private void initTrackList() {
        for (FileHandle fileHandle : Gdx.files.internal(Assets.RES_FOLDER + "audio/track").list()) {
            trackList.add(fileHandle.name());
        }
    }

    public void setMaxVol() {
        if (activeTrack != null) {
            activeTrack.setVolume(MAX_VOL);
        }
    }

    @Override
    protected void processSystem() {
        if (GdxgConstants.DEVELOPER_MODE) {
            return;
        }
        deltaAcc += getWorld().delta;

        if (deltaAcc >= INTERVAL) {
            deltaAcc -= INTERVAL;

            if (activeTrack == null) {
                if (trackList.size == 0) {
                    initTrackList();
                }
                String trackName = trackList.removeIndex(0);
                activeTrack = AudioPlayer.play(TRACKS_PATH + trackName, music -> {
                    activeTrack = null;
                    return true;
                });
                activeTrack.setVolume(MID_VOL);
            } else {
                float newVol = activeTrack.getVolume() - VOL_STEP;
                if (newVol < MIN_VOL) {
                    newVol = newVol + VOL_STEP / BELOW_MIN_VOL_TRANSITION_MLT;
                }
                if (newVol < 0) {
                    newVol = 0;
                }
                newVol *= masterVol;
                activeTrack.setVolume(newVol);
            }
        }
    }
}
