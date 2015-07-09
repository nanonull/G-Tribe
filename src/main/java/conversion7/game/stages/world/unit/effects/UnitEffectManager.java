package conversion7.game.stages.world.unit.effects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import conversion7.game.Assets;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.ClientUi;
import org.testng.Assert;

import java.util.Iterator;

public class UnitEffectManager {

    public Array<AbstractUnitEffect> effects = new Array<>();
    private Unit unitOwner;

    public UnitEffectManager(Unit unitOwner) {
        this.unitOwner = unitOwner;
    }

    public void addEffect(AbstractUnitEffect unitEffect) {
        Assert.assertFalse(effects.contains(unitEffect, true));
        effects.add(unitEffect);
        unitEffect.setOwner(this.unitOwner);
    }

    public AbstractUnitEffect getEffect(Class<? extends AbstractUnitEffect> effectClass) {
        for (AbstractUnitEffect effect : effects) {
            if (effect.getClass().equals(effectClass)) {
                return effect;
            }
        }
        return null;
    }

    public <T extends AbstractUnitEffect> T getEffectCasted(final Class<T> effectClass) {
        AbstractUnitEffect effect = getEffect(effectClass);
        return effect == null ? null : (T) effect;
    }

    public boolean containsEffect(Class<? extends AbstractUnitEffect> effectClass) {
        return getEffect(effectClass) != null;
    }

    public boolean removeEffect(Class<? extends AbstractUnitEffect> effectClass) {
        Iterator<AbstractUnitEffect> effectIterator = effects.iterator();
        while (effectIterator.hasNext()) {
            AbstractUnitEffect unitEffect = effectIterator.next();
            if (unitEffect.getClass().equals(effectClass)) {
                effectIterator.remove();
                return true;
            }
        }
        return false;
    }

    public void removeEffect(AbstractUnitEffect unitEffect) {
        Assert.assertTrue(effects.removeValue(unitEffect, false),
                "unitEffect is absent: " + unitEffect);
    }

    public int getEffectsDamage() {
        int effectsDamage = 0;
        for (AbstractUnitEffect effect : effects) {
            if (effect.effectParameters.getHealth() < 0) {
                effectsDamage -= effect.effectParameters.getHealth();
            }
        }
        return Math.abs(effectsDamage);
    }

    public int getStrength() {
        int effectedValue = unitOwner.getParams().getStrength();
        for (AbstractUnitEffect effect : effects) {
            effectedValue += Math.abs(effect.effectParameters.getStrength());
        }
        if (effectedValue < 1) {
            return 1;
        }
        return effectedValue;
    }

    public int getAgility() {
        int effectedValue = unitOwner.getParams().getAgility();
        for (AbstractUnitEffect effect : effects) {
            effectedValue += Math.abs(effect.effectParameters.getAgility());
        }
        if (effectedValue < 1) {
            return 1;
        }
        return effectedValue;
    }

    public int getVitality() {
        int effectedValue = unitOwner.getParams().getVitality();
        for (AbstractUnitEffect effect : effects) {
            effectedValue += Math.abs(effect.effectParameters.getVitality());
        }
        if (effectedValue < 1) {
            return 1;
        }
        return effectedValue;
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

    public void effectsTick() {
        for (int i = 0; i < effects.size; i++) {
            effects.get(i).tick();
        }
    }

}
