package conversion7.game.ui.world.inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.custom2d.table.DefaultTable;
import conversion7.engine.custom2d.table.HeaderCellData;
import conversion7.engine.custom2d.table.TableHeaderData;
import conversion7.game.Assets;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.ui.ClientUi;

public class InventoryItemRow extends DefaultTable {

    public InventoryItemRow(AbstractInventoryItem inventoryItem, TableHeaderData header) {
        defaults().pad(ClientUi.SPACING);
        Array<HeaderCellData> headers = header.getHeaders();

        int row = 0;
        add(new Label(inventoryItem.getName(), Assets.labelStyle14_lightGreen)).center()
                .width(headers.get(row).getWidth());

        row++;
        add(new Image(Assets.apple))
                .size(headers.get(row).getWidth());

        row++;
        Label label = new Label(String.valueOf(inventoryItem.getQuantity()), Assets.labelStyle14_lightGreen);
        label.setAlignment(Align.center);
        add(label).center()
                .width(headers.get(row).getWidth());

        add().width(ClientUi.SCROLL_LINE_SIZE);
    }
}
