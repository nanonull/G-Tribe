package conversion7.game.stages.world.elements;

import com.badlogic.gdx.graphics.Color;
import conversion7.engine.utils.MathUtils;
import conversion7.game.Assets;

import java.util.HashMap;
import java.util.Map;

public enum SoulType {
    DAY, NIGHT, SUN, MOON;

    public static final Map<SoulType, SoulType> POS_EFFECTS = new HashMap<>();
    public static final Map<SoulType, SoulType> NEG_EFFECTS = new HashMap<>();
    public static final int NIGHT_HEAL_BONUS = 1;
    public static final int DAY_DMG_BONUS = 1;

    static {
        POS_EFFECTS.put(DAY, SUN);
        POS_EFFECTS.put(SUN, DAY);
        POS_EFFECTS.put(NIGHT, MOON);
        POS_EFFECTS.put(MOON, NIGHT);

        NEG_EFFECTS.put(DAY, MOON);
        NEG_EFFECTS.put(MOON, DAY);
        NEG_EFFECTS.put(SUN, NIGHT);
        NEG_EFFECTS.put(NIGHT, SUN);
    }

    public static SoulType getRandom() {
        SoulType[] soulTypes = values();
        return soulTypes[MathUtils.random(0, soulTypes.length - 1)];
    }

    public Effect getEffectOn(SoulType other) {
        if (POS_EFFECTS.get(this) == other) {
            return Effect.POSITIVE;
        } else if (NEG_EFFECTS.get(this) == other) {
            return Effect.NEGATIVE;
        }
        return Effect.NEUTRAL;
    }

    public boolean hasHealBonus() {
        return this == MOON || this == NIGHT;
    }

    public boolean hasDmgBonus() {
        return this == DAY || this == SUN;
    }

    public enum Effect {
        POSITIVE(Color.GREEN, 1.5f), NEUTRAL(Color.GRAY, 1f), NEGATIVE(Assets.RED, 0.5f);

        private Color color;
        private float mlt;

        Effect(Color color, float mlt) {
            this.color = color;
            this.mlt = mlt;
        }

        public Color getColor() {
            return color;
        }

        public float getMlt() {
            return mlt;
        }
    }
}
