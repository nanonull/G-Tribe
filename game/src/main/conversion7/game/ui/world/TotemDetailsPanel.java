package conversion7.game.ui.world;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.VBox;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.totem.AbstractTotem;
import conversion7.game.ui.ClientUi;

public class TotemDetailsPanel extends VBox {
    VBox rootInfo;
    private AbstractTotem activeTotem;

    public TotemDetailsPanel() {
        pad(ClientUi.SPACING);
        setBackground(new TextureRegionColoredDrawable(ClientUi.PANEL_COLOR, Assets.pixel));

        addSmallCloseButton();
        rootInfo = new VBox();
        add(rootInfo).grow();
        rootInfo.defaults().space(2);
    }

    public void load(AbstractTotem object) {
        refreshContent(object);
    }

    public void refreshContent(final AbstractTotem object) {
        activeTotem = object;
        rootInfo.clearChildren();
        rootInfo.addLabel(object.getShortHint(), Assets.labelStyle14blackWithBackground);
        rootInfo.add(new Label(object.getHint(), Assets.labelStyle14white2));
        pack();
    }

}