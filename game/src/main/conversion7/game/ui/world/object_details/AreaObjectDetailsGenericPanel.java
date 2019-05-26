package conversion7.game.ui.world.object_details;

import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.VBox;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.AreaObjectDetailsDescriptor;
import conversion7.game.ui.ClientUi;

public class AreaObjectDetailsGenericPanel extends VBox {
    VBox infoBox;

    public AreaObjectDetailsGenericPanel() {
        pad(ClientUi.SPACING);
        setBackground(new TextureRegionColoredDrawable(ClientUi.PANEL_COLOR, Assets.pixel));

        addSmallCloseButton();
        infoBox = new VBox();
        infoBox.defaults().space(2);
        add(infoBox).grow();
    }

    public void load(AreaObjectDetailsDescriptor object) {
        infoBox.clearChildren();
        infoBox.add(object.getDetailsDescriptionActor()).grow();
        pack();
    }

}