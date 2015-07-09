package conversion7.game.ui.world.inventory;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.custom2d.table.DefaultTable;
import conversion7.engine.custom2d.table.HeaderCellData;
import conversion7.engine.custom2d.table.TableHeaderData;
import conversion7.engine.custom2d.table.TableWithHeader;
import conversion7.engine.custom2d.table.TableWithScrollPane;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.inventory.BasicInventory;
import conversion7.game.stages.world.inventory.CraftInventory;
import conversion7.game.stages.world.inventory.MainInventory;
import conversion7.game.stages.world.inventory.MilitaryInventory;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.controllers.InventoryController;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.HintForm;
import org.slf4j.Logger;

public class InventoryWindow extends AnimatedWindow {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static final String ACTIVATE_TRANSFER_INVENTORIES = "Activate transfer from main to this inventory";
    private static final int INVENTORIES_WIDTH = 3;

    private TableWithHeader objectMainInventoryTable;
    private TableWithHeader cellInventoryTable;
    private TableWithHeader militaryInventoryTable;
    private TableWithHeader craftInventoryTable;

    private TextButton activateLeftInventoryButton;
    private TextButton activateRightInventoryButton;

    private InventoryController inventoryController;


    public InventoryWindow(Stage stage, String title, Skin skin, int direction) {
        super(stage, title, skin, direction);
        addCloseButton();

        DefaultTable inventoriesTable = new DefaultTable();
        add(inventoriesTable);
        inventoriesTable.defaults().pad(ClientUi.SPACING);

        createObjectMainInventoryTable(inventoriesTable);
        createMiddleControls(inventoriesTable);
        createAdditionalInventories(inventoriesTable);

    }

    private void createObjectMainInventoryTable(Table inventoriesTable) {
        // header
        inventoriesTable.row();

        Label label;
        label = new Label(MainInventory.class.getSimpleName(), Assets.labelStyle14orange);
        label.setAlignment(Align.center);
        inventoriesTable.add(label).fill().colspan(INVENTORIES_WIDTH);

        inventoriesTable.row();
        // table
        TableWithScrollPane mainInventoryTableInner = new TableWithScrollPane();
        mainInventoryTableInner.getScrollPane().setScrollingDisabled(true, false);

        TableHeaderData mainTableHeaderData = new TableHeaderData(500);
        mainTableHeaderData.addHeaderCell(new HeaderCellData(20, "Name", null, null));
        mainTableHeaderData.addHeaderCell(new HeaderCellData(8, "Icon", null, null));
        mainTableHeaderData.addHeaderCell(new HeaderCellData(6, "Qty", null, null));
        objectMainInventoryTable = new TableWithHeader(mainTableHeaderData, mainInventoryTableInner, mainInventoryTableInner.getScrollPane());

        inventoriesTable.add(objectMainInventoryTable.getMainTable()).height(200).colspan(INVENTORIES_WIDTH).center();
        inventoriesTable.add();
        inventoriesTable.add();
    }

    private void createMiddleControls(DefaultTable inventoriesTable) {
        inventoriesTable.row().height(40);
        inventoriesTable.add();

        // headers
        inventoriesTable.row();

        Label label;
        label = new Label(MilitaryInventory.class.getSimpleName(), Assets.labelStyle14orange);
        label.setAlignment(Align.center);
        inventoriesTable.add(label).fill();

        label = new Label("CellInventory", Assets.labelStyle14orange);
        label.setAlignment(Align.center);
        inventoriesTable.add(label).fill();

        label = new Label(CraftInventory.class.getSimpleName(), Assets.labelStyle14orange);
        label.setAlignment(Align.center);
        inventoriesTable.add(label).fill();

        // switch inventories:
        inventoriesTable.row().height(20);

        activateLeftInventoryButton = new TextButton("Activate", Assets.uiSkin);
        activateLeftInventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inventoryController.setLeftInventoryActive();
                activateLeftInventoryButton.setDisabled(true);
                activateRightInventoryButton.setDisabled(false);
            }
        });
        inventoriesTable.add(activateLeftInventoryButton).center().bottom();
        HintForm.assignHintTo(activateLeftInventoryButton, ACTIVATE_TRANSFER_INVENTORIES);

        activateRightInventoryButton = new TextButton("Activate", Assets.uiSkin);
        activateRightInventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inventoryController.setRightInventoryActive();
                activateRightInventoryButton.setDisabled(true);
                activateLeftInventoryButton.setDisabled(false);
            }
        });
        inventoriesTable.add(activateRightInventoryButton).center().bottom();
        HintForm.assignHintTo(activateRightInventoryButton, ACTIVATE_TRANSFER_INVENTORIES);

    }

    private void createAdditionalInventories(DefaultTable inventoriesTable) {
        inventoriesTable.row();

        // militaryInventoryTable
        TableWithScrollPane militaryInventoryTableInner = new TableWithScrollPane();
        militaryInventoryTableInner.getScrollPane().setScrollingDisabled(true, false);

        TableHeaderData militaryTableHeaderData = new TableHeaderData(200);
        militaryTableHeaderData.addHeaderCell(new HeaderCellData(62, "Name", null, null));
        militaryTableHeaderData.addHeaderCell(new HeaderCellData(19, "Icon", null, null));
        militaryTableHeaderData.addHeaderCell(new HeaderCellData(19, "Qty", null, null));
        militaryInventoryTable = new TableWithHeader(militaryTableHeaderData, militaryInventoryTableInner, militaryInventoryTableInner.getScrollPane());

        inventoriesTable.add(militaryInventoryTable.getMainTable()).height(200);

        // cellInventoryTable
        TableWithScrollPane cellInventoryTableInner = new TableWithScrollPane();
        cellInventoryTableInner.getScrollPane().setScrollingDisabled(true, false);

        TableHeaderData cellTableHeaderData = new TableHeaderData(200);
        cellTableHeaderData.addHeaderCell(new HeaderCellData(62, "Name", null, null));
        cellTableHeaderData.addHeaderCell(new HeaderCellData(19, "Icon", null, null));
        cellTableHeaderData.addHeaderCell(new HeaderCellData(19, "Qty", null, null));
        cellInventoryTable = new TableWithHeader(cellTableHeaderData, cellInventoryTableInner, cellInventoryTableInner.getScrollPane());

        inventoriesTable.add(cellInventoryTable.getMainTable()).height(200);

        // craftInventoryTable
        TableWithScrollPane craftInventoryTableInner = new TableWithScrollPane();
        craftInventoryTableInner.getScrollPane().setScrollingDisabled(true, false);

        TableHeaderData craftTableHeaderData = new TableHeaderData(200);
        craftTableHeaderData.addHeaderCell(new HeaderCellData(62, "Name", null, null));
        craftTableHeaderData.addHeaderCell(new HeaderCellData(19, "Icon", null, null));
        craftTableHeaderData.addHeaderCell(new HeaderCellData(19, "Qty", null, null));
        craftInventoryTable = new TableWithHeader(craftTableHeaderData, craftInventoryTableInner, craftInventoryTableInner.getScrollPane());

        inventoriesTable.add(craftInventoryTable.getMainTable()).height(200);
    }

    public void show(AreaObject object) {
        refreshContent(object);
        pack();
        setPosition(ClientUi.SPACING, GdxgConstants.SCREEN_HEIGHT_IN_PX - ClientUi.SPACING - getHeight());
        updateAnimationBounds();
        show();
    }

    public void refreshContent(final AreaObject object) {
        inventoryController = object.getInventoryController();

        if (inventoryController.isLeftInventoryActive()) {
            activateLeftInventoryButton.setDisabled(true);
            activateRightInventoryButton.setDisabled(false);
        } else {
            activateLeftInventoryButton.setDisabled(false);
            activateRightInventoryButton.setDisabled(true);
        }

        loadMainInventoryTable(object);
        loadMilitaryInventoryTable(object);
        loadCellInventoryTable(object);
        loadCraftInventoryTable(object);
    }

    private void loadMainInventoryTable(final AreaObject object) {
        objectMainInventoryTable.clearChildren();
        BasicInventory mainInventory = object.getMainInventory();
        if (mainInventory.isEmpty()) {
            objectMainInventoryTable.add(new Label(" - inventory is empty - ", Assets.labelStyle14_lightGreen));

        } else {
            for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> itemEntry
                    : mainInventory.getItemsIterator()) {
                final AbstractInventoryItem inventoryItem = itemEntry.value;
                InventoryItemRow inventoryItemRow = new InventoryItemRow(inventoryItem, objectMainInventoryTable.getTableHeaderData());
                objectMainInventoryTable.add(inventoryItemRow);
                inventoryItemRow.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        inventoryController.moveItemFromMainToActiveInventory(inventoryItem);
                        refreshContent(object);
                    }
                });
                objectMainInventoryTable.row();
            }
        }
    }

    private void loadMilitaryInventoryTable(final AreaObject object) {
        militaryInventoryTable.clearChildren();
        BasicInventory militaryInventory = object.getMilitaryInventory();
        if (militaryInventory.isEmpty()) {
            militaryInventoryTable.add(new Label(" - inventory is empty - ", Assets.labelStyle14_lightGreen));

        } else {
            for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> itemEntry
                    : militaryInventory.getItemsIterator()) {
                final AbstractInventoryItem inventoryItem = itemEntry.value;
                InventoryItemRow inventoryItemRow = new InventoryItemRow(inventoryItem, militaryInventoryTable.getTableHeaderData());
                militaryInventoryTable.add(inventoryItemRow);
                inventoryItemRow.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        inventoryController.moveItemFromMilitaryToMainInventory(inventoryItem);
                        refreshContent(object);
                    }
                });
                militaryInventoryTable.row();
            }
        }
    }

    private void loadCellInventoryTable(final AreaObject object) {
        cellInventoryTable.clearChildren();
        BasicInventory cellInventory = object.getCell().getInventory();
        if (cellInventory.isEmpty()) {
            cellInventoryTable.add(new Label(" - inventory is empty - ", Assets.labelStyle14_lightGreen));

        } else {
            for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> itemEntry
                    : cellInventory.getItemsIterator()) {
                final AbstractInventoryItem inventoryItem = itemEntry.value;
                InventoryItemRow inventoryItemRow = new InventoryItemRow(inventoryItem, cellInventoryTable.getTableHeaderData());
                cellInventoryTable.add(inventoryItemRow);
                inventoryItemRow.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        inventoryController.moveItemFromCellToMainInventory(inventoryItem);
                        refreshContent(object);
                    }
                });
                cellInventoryTable.row();
            }
        }
    }

    private void loadCraftInventoryTable(final AreaObject object) {
        craftInventoryTable.clearChildren();
        BasicInventory craftInventory = object.getCraftInventory();
        if (craftInventory.isEmpty()) {
            craftInventoryTable.add(new Label(" - inventory is empty - ", Assets.labelStyle14_lightGreen));

        } else {
            for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> itemEntry
                    : craftInventory.getItemsIterator()) {
                final AbstractInventoryItem inventoryItem = itemEntry.value;
                InventoryItemRow inventoryItemRow = new InventoryItemRow(inventoryItem, craftInventoryTable.getTableHeaderData());
                craftInventoryTable.add(inventoryItemRow);
                inventoryItemRow.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        inventoryController.executeCraft(inventoryItem);
                        refreshContent(object);
                    }
                });
                craftInventoryTable.row();
            }
        }
    }
}
