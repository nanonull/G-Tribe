package conversion7.game.ui.world.highlight_cell;

import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.table.Panel;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.ClientUi;

public class HighlightedCellPanel extends Panel {

    CellInfoPanel cellInfoPanel;
    UnitInfoTable unitInfoTable;

    public HighlightedCellPanel() {

        pad(ClientUi.SPACING);
        defaults().pad(2);
        setBackground(new TextureRegionColoredDrawable(ClientUi.PANEL_COLOR, Assets.pixel));

        cellInfoPanel = new CellInfoPanel();
        add(cellInfoPanel);

        row();

        unitInfoTable = new UnitInfoTable();
        add(unitInfoTable);

        row().height(ClientUi.SPACING);
        add();
        hide();
    }

    public void showOn(Cell cell, AbstractSquad selectedObject) {
        cellInfoPanel.load(cell, selectedObject);
        unitInfoTable.load(selectedObject, cell.getSquad());

        pack();

        if (!isVisible()) {
            show();
        }
    }

}
