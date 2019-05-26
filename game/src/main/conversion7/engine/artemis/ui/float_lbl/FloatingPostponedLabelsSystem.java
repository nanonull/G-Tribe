package conversion7.engine.artemis.ui.float_lbl;

import com.artemis.BaseSystem;
import conversion7.engine.Gdxg;
import conversion7.game.stages.world.FloatingLabel;
import conversion7.game.stages.world.view.AreaViewer;

public class FloatingPostponedLabelsSystem extends BaseSystem {
    @Override
    protected void processSystem() {
        AreaViewer areaViewer = Gdxg.core.areaViewer;
        if (areaViewer != null && areaViewer.mouseOverCell != null) {
            for (FloatingLabel label : areaViewer.mouseOverCell.postponedFloatingLabels) {
                int currStep = Gdxg.core.world.step;
                if (currStep < label.createdAtWorldStep + 1) {
                    areaViewer.mouseOverCell.addFloatLabel(label.txt, label.color, false);
                }
            }
            areaViewer.mouseOverCell.postponedFloatingLabels.clear();
        }
    }
}
