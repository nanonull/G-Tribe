package conversion7.game.stages.world.elements;

import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.gods.AbstractGod;
import conversion7.game.stages.world.gods.Geb;
import conversion7.game.stages.world.gods.Neptune;
import conversion7.game.stages.world.gods.Ra;
import conversion7.game.stages.world.gods.Zeus;

import java.util.HashMap;
import java.util.Map;

public enum ElementType {
    WATER,
    FIRE,
    AIR,
    EARTH;

    public static final Map<Class<? extends AbstractGod>, ElementType> GODS_BY_ELEMENTS = new HashMap<>();
    public static final Map<ElementType, ElementType> ELEMENT_ATTACKS = new HashMap<>();

    static {
        GODS_BY_ELEMENTS.put(Neptune.class, WATER);
        GODS_BY_ELEMENTS.put(Ra.class, FIRE);
        GODS_BY_ELEMENTS.put(Zeus.class, AIR);
        GODS_BY_ELEMENTS.put(Geb.class, EARTH);

        ELEMENT_ATTACKS.put(WATER, FIRE);
        ELEMENT_ATTACKS.put(FIRE, AIR);
        ELEMENT_ATTACKS.put(AIR, EARTH);
        ELEMENT_ATTACKS.put(EARTH, WATER);
    }

    public static ElementType getRandom() {
        ElementType[] elementTypes = values();
        return elementTypes[MathUtils.random(0, elementTypes.length - 1)];
    }

    public boolean doesAttack(ElementType elementType) {
        return ELEMENT_ATTACKS.get(this) == elementType;
    }
}
