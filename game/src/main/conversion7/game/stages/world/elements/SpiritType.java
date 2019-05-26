package conversion7.game.stages.world.elements;

import conversion7.engine.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;

public enum SpiritType {
    NATURE, VITALITY, ANIMALITY;
    public static final Map<SpiritType, SpiritType> SUPPORT = new HashMap<>();

    static {
        SUPPORT.put(NATURE, VITALITY);
        SUPPORT.put(VITALITY, ANIMALITY);
        SUPPORT.put(ANIMALITY, NATURE);
    }

    public static SpiritType getRandom() {
        SpiritType[] spiritTypes = values();
        return spiritTypes[MathUtils.random(0, spiritTypes.length - 1)];
    }

    public boolean doesSupport(SpiritType spiritType) {
        return SUPPORT.get(this) == spiritType;
    }
}
