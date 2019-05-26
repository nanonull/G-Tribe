package conversion7.engine.artemis.audio;

import com.artemis.BaseSystem;
import com.badlogic.gdx.audio.Music;
import conversion7.engine.AudioPlayer;
import conversion7.engine.Gdxg;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.Landscape;
import conversion7.game.stages.world.landscape.Soil;

public class CellAudioSystem extends BaseSystem {
    private static final float INTERVAL = 0.75f;
    private static final float MIN_MASTER = 0.12f;
    private static final float MAX_MASTER = 0.2f;
    private static final float TRANSITION_TIME = 5;
    private static final int TRANSITION_STEPS = (int) (TRANSITION_TIME / INTERVAL);
    private static final float TRANSITION_VOL_STEP = (MAX_MASTER - MIN_MASTER) / TRANSITION_STEPS;
    private static final float STONE_VOL_FIX = 0.75f;

    float deltaAcc;
    float dirt = 0.33f;
    float sand = 0.33f;
    float stone = 0.33f;
    float water;
    float forest;
    private float masterVolume = MAX_MASTER;
    private boolean resetMasterVol;

    @Override
    protected void processSystem() {
        deltaAcc += getWorld().delta;

        if (deltaAcc >= INTERVAL) {
            deltaAcc -= INTERVAL;

            if (resetMasterVol) {
                masterVolume -= TRANSITION_VOL_STEP;
                if (masterVolume <= MIN_MASTER) {
                    masterVolume = MIN_MASTER;
                    resetMasterVol = false;
                }
            } else {
                masterVolume += 0.01;
                if (masterVolume > MAX_MASTER) {
                    masterVolume = MAX_MASTER;
                }
            }

            Cell mouseOverCell;
            try {
                mouseOverCell = Gdxg.core.areaViewer.mouseOverCell;
                if (mouseOverCell == null) {
                    return;
                }
                interpolateBy(mouseOverCell);
            } catch (NullPointerException e) {
                return;
            }

            Music dirtAudio = AudioPlayer.getOrPlay("forest_4q.mp3");
            dirtAudio.setLooping(true);
            dirtAudio.setVolume(dirt * masterVolume);

            Music dirtAudio2 = AudioPlayer.getOrPlay("ambient1.mp3");
            dirtAudio2.setLooping(true);
            dirtAudio2.setVolume(dirt * masterVolume / 4);

            Music sandAudio = AudioPlayer.getOrPlay("ambient3.mp3");
            sandAudio.setLooping(true);
            sandAudio.setVolume(sand * masterVolume);

            Music stoneAudio = AudioPlayer.getOrPlay("ambient4.mp3");
            stoneAudio.setLooping(true);
            stoneAudio.setVolume(stone * masterVolume * STONE_VOL_FIX);

            Music waterAudio = AudioPlayer.getOrPlay("water-fields.mp3");
            waterAudio.setLooping(true);
            waterAudio.setVolume(water * masterVolume);

            Music forestAudio = AudioPlayer.getOrPlay("birds.mp3");
            forestAudio.setLooping(true);
            forestAudio.setVolume(forest * masterVolume);
        }
    }

    private void interpolateBy(Cell cell) {
        float newDirt = cell.getLandscape().soil.getSoilTypeValue(Soil.TypeId.DIRT) / 100f;
        float newSand = cell.getLandscape().soil.getSoilTypeValue(Soil.TypeId.SAND) / 100f;
        float newStone = cell.getLandscape().soil.getSoilTypeValue(Soil.TypeId.STONE) / 100f;
        float newWater = cell.getLandscape().type == Landscape.Type.WATER ? 1 : 0;
        float newForest = cell.getLandscape().hasForest() ? 1 : 0;
        dirt = (dirt + newDirt) / 2f;
        sand = (sand + newSand) / 2f;
        stone = (stone + newStone) / 2f;
        water = (water + newWater) / 2f;
        forest = (forest + newForest) / 2f;
    }

    public void resetMasterVol() {
        resetMasterVol = true;
    }
}
