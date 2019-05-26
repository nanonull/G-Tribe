package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.custom2d.table.Panel;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;
import conversion7.game.ui.hint.PopupHintPanel;

import static conversion7.engine.utils.Utils.iter;
import static conversion7.engine.utils.Utils.safeIter;

public class UnitEffectsPanel extends Panel {

    public UnitEffectsPanel() {
    }

    public void load(AbstractSquad squad) {
        clear();

        if (squad == null) {
            return;
        }

        Array<AbstractUnitEffect> effects = squad.getEffectManager().effects;
        for (AbstractUnitEffect effect : safeIter(effects) ? effects : iter(effects)) {
            Actor icon = effect.getIconPanel();
            add(icon).padRight(1);
            PopupHintPanel.assignHintTo(icon, effect.getHint());
        }
    }

}
