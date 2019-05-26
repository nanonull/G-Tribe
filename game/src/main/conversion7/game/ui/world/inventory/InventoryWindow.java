package conversion7.game.ui.world.inventory;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.custom2d.HBox;
import conversion7.engine.custom2d.VBox;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.world.inventory.panels.EquipmentPanel;
import conversion7.game.ui.world.inventory.panels.InventoriesPanel;
import conversion7.game.ui.world.inventory.panels.UnitAllParametersPanel;
import conversion7.game.ui.world.inventory.panels.lvl1.IconPanel;

public class InventoryWindow extends AnimatedWindow {

    private final InventoriesPanel inventoriesPanel = new InventoriesPanel(this);
    EquipmentPanel equipmentPanel = new EquipmentPanel(this);
    UnitAllParametersPanel parametersPanel = new UnitAllParametersPanel();
    private final IconPanel iconPanel = new IconPanel();
    private AbstractSquad squad;

    public InventoryWindow(Stage stage, String title, Skin skin, int direction) {
        super(stage, title, skin, direction);
        addCloseButton();

        VBox rows = new VBox();
        add(rows);

        HBox row1 = new HBox();
        rows.add(row1).padBottom(40);

        row1.add(iconPanel).padRight(20);

        row1.add(parametersPanel).padRight(40);
        row1.add(equipmentPanel);

        HBox row2 = new HBox();
        rows.add(row2);
        row2.add(inventoriesPanel);
    }

    public InventoriesPanel getInventoriesPanel() {
        return inventoriesPanel;
    }

    public void show(AbstractSquad object) {
        refresh(object);
        setPosition(ClientUi.SPACING, GdxgConstants.SCREEN_HEIGHT_IN_PX - ClientUi.SPACING - getHeight());
        updateAnimationBounds();
        show();
    }

    public void refresh() {
        if (squad != null) {
            refresh(squad);
        }
    }

    public void refresh(final AbstractSquad squad) {
        this.squad = squad;
        iconPanel.load(squad);
        equipmentPanel.load(squad);
        parametersPanel.load(squad);
        inventoriesPanel.load(squad);
        pack();
    }
}
