package conversion7.game.stages.world.objects.actions;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.engine.Gdxg;
import conversion7.game.Assets;
import conversion7.game.interfaces.TargetableOnCell;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.actions.subactions.SelectNeighborCellForUnitsSplitMergeSubaction;

/** Flow: press action > select neighbor cell or areaObject except AnimalHerd */
public class SplitMergeUnitsAction extends AbstractAreaObjectAction implements TargetableOnCell {

    SelectNeighborCellForUnitsSplitMergeSubaction selectNeighborCellSubaction = new SelectNeighborCellForUnitsSplitMergeSubaction(this);

    public SplitMergeUnitsAction(AreaObject object) {
        super(object);
    }

    @Override
    public void execute() {
        Gdxg.clientUi.hideBarsForSelectedObject();
        selectNeighborCellSubaction.execute();
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.armyIcon;
    }

    @Override
    public void setTarget(Cell target) {
        Gdxg.clientUi.getSplitMergeUnitsWindow().showFor(getObject(), target);
    }

    @Override
    public void cancel() {
        World.getAreaViewer().unhideSelection();
    }
}
