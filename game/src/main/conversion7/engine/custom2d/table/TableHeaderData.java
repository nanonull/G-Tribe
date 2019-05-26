package conversion7.engine.custom2d.table;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.engine.utils.FastAsserts;
import conversion7.game.Assets;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.hint.PopupHintPanel;

public class TableHeaderData {

    private final int tableWidth;
    private final Array<HeaderCellData> headers = new Array<>();
    private final Table table = new DefaultTable();

    public TableHeaderData(int tableWidth) {
        FastAsserts.assertMoreThan(tableWidth, 0);
        this.tableWidth = tableWidth;
        table.defaults().pad(ClientUi.SPACING).center();
    }

    public Table getTable() {
        return table;
    }

    public int getTableWidth() {
        return tableWidth;
    }

    public Array<HeaderCellData> getHeaders() {
        return headers;
    }

    public void addHeaderCell(HeaderCellData headerCellData) {
        headers.add(headerCellData);
        headerCellData.calculateWidth(tableWidth);

        Cell cell;
        if (headerCellData.getName() != null) {
            cell = table.add(new TextButton(headerCellData.getName(), Assets.uiSkin));

        } else if (headerCellData.getIconForHeaderButton() != null) {
            cell = table.add(new ButtonWithActor(headerCellData.getIconForHeaderButton(), true));

        } else {
            cell = table.add();
        }
        cell.width(headerCellData.getWidth()).height(26);

        if (headerCellData.getHintText() != null) {
            Actor actor = cell.getActor();
            if (actor != null) {
                PopupHintPanel.assignHintTo(actor, headerCellData.getHintText());
            }
        }
    }
}
