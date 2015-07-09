package conversion7.game.stages.world.landscape;

import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.Climate;

import java.util.HashMap;
import java.util.Map;

public class BiomBuilder {

    public static final int DEFAULT_DIRT = PropertiesLoader.getIntProperty("BIOM_CHANCE.DIRT");
    public static final int DEFAULT_SAND = PropertiesLoader.getIntProperty("BIOM_CHANCE.SAND");
    public static final int DEFAULT_STONE = PropertiesLoader.getIntProperty("BIOM_CHANCE.STONE");
    public static final int DEFAULT_WATER = PropertiesLoader.getIntProperty("BIOM_CHANCE.WATER");
    public static final int DEFAULT_MOUNTAIN = PropertiesLoader.getIntProperty("BIOM_CHANCE.MOUNTAIN");

    private static final Map<Biom.Type, Integer> CURRENT_CHANCES = new HashMap<>();

    public void resetToDefaults() {
        CURRENT_CHANCES.put(Biom.Type.DIRT, DEFAULT_DIRT);
        CURRENT_CHANCES.put(Biom.Type.SAND, DEFAULT_SAND);
        CURRENT_CHANCES.put(Biom.Type.STONE, DEFAULT_STONE);
        CURRENT_CHANCES.put(Biom.Type.WATER, DEFAULT_WATER);
        CURRENT_CHANCES.put(Biom.Type.MOUNTAIN, DEFAULT_MOUNTAIN);
    }


    public void addDesertChance(int originTemperature) {
        if (originTemperature >= Climate.DESERT_MORE_CHANCE_AFTER_TEMPERATURE) {
            Integer currSand = CURRENT_CHANCES.get(Biom.Type.SAND);
            CURRENT_CHANCES.put(Biom.Type.SAND, Math.round(currSand * 1.5f));
        }
    }

    private int getChancesSum() {
        return MathUtils.getSumOfMapValues(CURRENT_CHANCES);
    }

    public Biom.Type geWinnerBiomType() {
        int chancesSum = getChancesSum();
        int biomChanceThrow = Utils.RANDOM.nextInt(chancesSum);

        int totalChancesDone = 0;
        for (Map.Entry<Biom.Type, Integer> entry : CURRENT_CHANCES.entrySet()) {
            Integer typeChance = entry.getValue();
            totalChancesDone += typeChance;

            if (totalChancesDone > biomChanceThrow) {
                return entry.getKey();
            }
        }

        throw new GdxRuntimeException("Biom type winner was not found!");
    }

}
