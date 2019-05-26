package conversion7.game.stages.world.unit.effects.items;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class ConcealmentEffect extends AbstractUnitEffect {

    public ConcealmentEffect() {
        super(ConcealmentEffect.class.getSimpleName(), AbstractUnitEffect.Type.POSITIVE, null);
    }

    @Override
    public Image getIcon() {
        return new Image(Assets.getTextureReg("conceal"));
    }

    @Override
    public String getHint() {
        return super.getHint()
                + "\n \nUnit is invisible for others if distance is more than 1 cell";
    }

    @Override
    public void onAdded() {
        super.onAdded();
        refreshVisibility();
    }

    @Override
    public void remove() {
        super.remove();
        refreshVisibility();
    }

    private void refreshVisibility() {
        Cell myCell = getOwner().getLastCell();
        myCell.setRefreshedInView(false);
        for (AbstractSquad squad : new Array.ArrayIterable<>(myCell.visibleBySquads)) {
            squad.refreshStealth();
        }
        myCell.refreshViewer();
    }

}
