package conversion7.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Predicate;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;

public class AudioPlayer {

    private static ObjectMap<String, Music> activeTracks = new ObjectMap<>();
    public static final String AUDIO_PATH = Assets.RES_FOLDER + "audio\\";

    public static Music play(String path, Predicate<Music> onCompletion) {
        if (GdxgConstants.DEVELOPER_MODE) {
            Music music = playPath("fx\\click1.mp3", null);
            music.stop();
            return music;
        }

        Music active = AudioPlayer.activeTracks.get(path);
        if (active != null) {
            active.stop();
            active.play();
            return active;
        }

        return playPath(path, onCompletion);
    }

    private static Music playPath(String path, Predicate<Music> onCompletion) {
        Music music = Gdx.audio.newMusic(Gdx.files.internal(AUDIO_PATH + path));
        music.play();
        AudioPlayer.activeTracks.put(path, music);
        music.setOnCompletionListener(m -> {
            m.dispose();
            AudioPlayer.activeTracks.remove(path);
            if (onCompletion != null) {
                onCompletion.evaluate(m);
            }
        });
        return music;
    }

    public static Music getOrPlay(String path) {
        Music active = activeTracks.get(path);
        if (active == null) {
            active = play(path);
        }
        return active;
    }

    public static Music play(String path) {
        return play(path, null);
    }

    public static void stopAll() {
        for (ObjectMap.Entry<String, Music> musicEntry : activeTracks) {
            musicEntry.value.stop();
            musicEntry.value.dispose();
        }
        activeTracks.clear();
    }

    public static void playFail() {
        AudioPlayer.play("fx\\click2.mp3");
    }

    public static void playRitual() {
        play("fx\\ritual.mp3").setVolume(0.3f);
    }

    public static void playTribe() {
        playSingleSnare();
    }

    public static void playSingleSnare() {
        play("fx\\2\\wow_2.mp3");
    }

    public static void playMultiSnares() {
        play("fx\\2\\wow_3.mp3");
    }

    public static void playPositiveVibe() {
        play("fx\\2\\wow_pos_1.mp3");
    }

    public static void playEnd() {
        AudioPlayer.play("fx\\wooh.mp3");
    }
}