package conversion7.game.stages.world.unit.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.engine.Gdxg;
import conversion7.engine.artemis.ui.float_lbl.UnitFloatingStatusBatch;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.UnitParameterType;
import conversion7.game.ui.ClientUi;
import org.slf4j.Logger;
import org.testng.Assert;

import java.util.Iterator;

public class UnitEffectManager {

    private static final Logger LOG = Utils.getLoggerForClass();
    public Array<AbstractUnitEffect> effects = new Array<>();
    ;
    private Array.ArrayIterator<AbstractUnitEffect> effectsIterator = new Array.ArrayIterator<>(effects, false);
    private AreaObject unitOwner;

    public UnitEffectManager(AreaObject unitOwner) {
        this.unitOwner = unitOwner;
    }

    public TextureRegion getTotalEffectsIcon() {
        Assert.assertTrue(effects.size > 0);

        AbstractUnitEffect.Type firstType = null;
        for (AbstractUnitEffect effect : effects) {
            if (effect.type.equals(AbstractUnitEffect.Type.POSITIVE_NEGATIVE)) {
                return Assets.eyeGreenRed;
            }
            if (firstType == null) {
                firstType = effect.type;
            } else if (!firstType.equals(effect.type)) {
                return Assets.eyeGreenRed;
            }
        }

        if (firstType.equals(AbstractUnitEffect.Type.POSITIVE)) {
            return Assets.eyeGreen;
        } else {
            return Assets.eyeRed;
        }
    }

    @Deprecated
    public Table getEffectsTableForHint() {
        Table hintTable = new Table();
        hintTable.defaults().left().top().pad(ClientUi.SPACING);
        hintTable.add(new Label("Effects:", Assets.labelStyle12_i_black)).fill().expand();

        for (AbstractUnitEffect effect : effects) {
            hintTable.row();
            hintTable.add(new Label(effect.getHint(), Assets.labelStyle12_i_black))
                    .fill().expand();
        }

        hintTable.pack();
        return hintTable;
    }

    public Array.ArrayIterator<AbstractUnitEffect> getEffectsIterator() {
        effectsIterator.reset();
        return effectsIterator;
    }

    @Deprecated
    public void addEffect(AbstractUnitEffect unitEffect) {
        // TBD should be private; use getOrCreate
        Assert.assertFalse(effects.contains(unitEffect, true));
        effects.add(unitEffect);
        unitEffect.setOwner((AbstractSquad) this.unitOwner);
        try {
            unitEffect.onAdded();
            if (UnitFloatingStatusBatch.SHOW_EFFECT_LABELS) {
                unitOwner.cell.addFloatLabel("New effect: " + unitEffect.name, Color.ORANGE, true);
            }
        } catch (NullPointerException e) {
            LOG.warn("Add effect on dead unit...");
        }
    }

    public <T extends AbstractUnitEffect> T getEffect(final Class<T> effectClass) {
        AbstractUnitEffect effect = getEffectRaw(effectClass);
        return effect == null ? null : (T) effect;
    }

    public <T extends AbstractUnitEffect> T getOrCreate(final Class<T> effectClass) {
        AbstractUnitEffect effect = getEffectRaw(effectClass);
        if (effect == null) {
            try {
                effect = effectClass.newInstance();
                addEffect(effect);
            } catch (Exception e) {
                Gdxg.core.addError(e);
            }
        }
        return (T) effect;
    }

    public AbstractUnitEffect getEffectRaw(Class<? extends AbstractUnitEffect> effectClass) {
        for (AbstractUnitEffect effect : effects) {
            if (effect.getClass().equals(effectClass)) {
                return effect;
            }
        }
        return null;
    }

    public boolean containsEffect(Class<? extends AbstractUnitEffect> effectClass) {
        return getEffectRaw(effectClass) != null;
    }

    public boolean removeEffectIfExist(Class<? extends AbstractUnitEffect> effectClass) {
        Iterator<AbstractUnitEffect> effectIterator = effects.iterator();
        while (effectIterator.hasNext()) {
            AbstractUnitEffect unitEffect = effectIterator.next();
            if (unitEffect.getClass().equals(effectClass)) {
                unitEffect.onRemoved();
                effectIterator.remove();
                return true;
            }
        }
        return false;
    }

    public void removeEffect(AbstractUnitEffect unitEffect) {
        unitEffect.onRemoved();
        Assert.assertTrue(effects.removeValue(unitEffect, false),
                "unitEffect is absent: " + unitEffect);
    }

    public UnitParamTypeEffectTotal get(UnitParameterType typeToBeCollected) {
        UnitParamTypeEffectTotal effectTotal = new UnitParamTypeEffectTotal();
        for (AbstractUnitEffect effect : getEffectsIterator()) {
            if (effect.effectParameters == null || !effect.isEnabled()) {
                continue;
            }
            for (ObjectMap.Entry<UnitParameterType, Integer> param : effect.effectParameters.getParametersStorage()) {
                UnitParameterType type = param.key;
                Integer typeValue = param.value;
                if (type.equals(typeToBeCollected)) {
                    effectTotal.value += typeValue;
                } else if (type.isParamModifierOf(typeToBeCollected)) {
                    effectTotal.percentValue += typeValue;
                }
            }
        }

        return effectTotal;
    }

    public void effectsTick() {
        for (int i = effects.size - 1; i >= 0; i--) {
            AbstractUnitEffect unitEffect = effects.get(i);
            unitEffect.tick();
            if (unitEffect.completed) {
                effects.removeValue(unitEffect, true);
            }
        }
    }

    public void resetEffect(AbstractUnitEffect randomEffect) {
        AbstractUnitEffect effectRaw = getEffectRaw(randomEffect.getClass());
        if (effectRaw != null) {
            removeEffect(effectRaw);
        }
        addEffect(randomEffect);
    }
}
