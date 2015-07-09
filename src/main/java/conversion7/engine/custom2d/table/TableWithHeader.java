package conversion7.engine.custom2d.table;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.testng.Assert;

public class TableWithHeader {

    private final Table mainTable = new DefaultTable();
    private final TableHeaderData tableHeaderData;
    private final Table tableContent;

    private final Cell<Table> headerTableCell;

    public TableWithHeader(TableHeaderData tableHeaderData, Table tableContent) {
        this(tableHeaderData, tableContent, null);
    }

    public TableWithHeader(TableHeaderData tableHeaderData, Table tableContent, ScrollPane contentScrollPane) {
        Assert.assertNotNull(tableHeaderData);
        Assert.assertNotNull(tableContent);
        this.tableHeaderData = tableHeaderData;
        this.tableContent = tableContent;
        headerTableCell = mainTable.add(tableHeaderData.getTable());

        mainTable.row();

        if (contentScrollPane == null) {
            mainTable.add(tableContent);
        } else {
            mainTable.add(contentScrollPane);
        }
    }

    public Table getMainTable() {
        return mainTable;
    }

    public int getTableWidth() {
        return tableHeaderData.getTableWidth();
    }

    public TableHeaderData getTableHeaderData() {
        return tableHeaderData;
    }

    public <T extends Actor> Cell<T> add(T actor) {
        return tableContent.add(actor);
    }

    public Cell add() {
        return tableContent.add();
    }

    public Cell row() {
        return tableContent.row();
    }

    public void clearChildren() {
        tableContent.clearChildren();
    }

    public void setHeaderVisible(boolean visible) {
        tableHeaderData.getTable().setVisible(visible);
    }
}
