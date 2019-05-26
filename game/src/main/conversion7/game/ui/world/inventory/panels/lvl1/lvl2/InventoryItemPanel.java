package conversion7.game.ui.world.inventory.panels.lvl1.lvl2;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import conversion7.engine.custom2d.table.Panel;
import conversion7.game.Assets;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.hint.PopupHintPanel;

public class InventoryItemPanel extends Panel {

    public InventoryItemPanel(AbstractInventoryItem inventoryItem) {
        defaults().pad(ClientUi.SPACING);

        Label nameLbl = new Label(inventoryItem.getName(), Assets.labelStyle14_lightGreen);
        add(nameLbl).center().width(120);
        nameLbl.setWrap(true);


        TextureRegion iconTexture = inventoryItem.getIconTexture();
        if (iconTexture == null) {
            add().size(48);
        } else {
            add(new Image(iconTexture)).size(48);
        }

        Label label = new Label(String.valueOf(inventoryItem.getQuantity()), Assets.labelStyle14orange2);
        label.setAlignment(Align.center);
        add(label).center()
                .width(32);

        add().width(ClientUi.SCROLL_LINE_SIZE);
        PopupHintPanel.assignHintTo(this, inventoryItem.getDescription());
    }
}
