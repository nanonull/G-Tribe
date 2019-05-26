package conversion7.game.ui.world.inventory.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import conversion7.engine.custom2d.HBox;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.world.inventory.InventoryWindow;
import conversion7.game.ui.world.inventory.panels.lvl1.CellInventoryPanel;
import conversion7.game.ui.world.inventory.panels.lvl1.CraftInventoryPanel;
import conversion7.game.ui.world.inventory.panels.lvl1.TeamInventoryPanel;

public class InventoriesPanel extends HBox {

    private TeamInventoryPanel teamInventoryPanel = new TeamInventoryPanel(this);
    private CellInventoryPanel cellInventoryPanel = new CellInventoryPanel(this);
    private CraftInventoryPanel craftInventoryPanel = new CraftInventoryPanel(this);
    public InventoryWindow inventoryWindow;

    public InventoriesPanel(InventoryWindow inventoryWindow) {
        this.inventoryWindow = inventoryWindow;
        add(craftInventoryPanel);
        add(teamInventoryPanel);
        add(cellInventoryPanel);
    }

    public TeamInventoryPanel getTeamInventoryPanel() {
        return teamInventoryPanel;
    }

    @Override
    public Cell row() {
        throw new RuntimeException("No rows in HBox!");
    }

    public void load(final AbstractSquad object) {
        teamInventoryPanel.load(object);
        cellInventoryPanel.load(object);
        craftInventoryPanel.load(object);
    }
}
