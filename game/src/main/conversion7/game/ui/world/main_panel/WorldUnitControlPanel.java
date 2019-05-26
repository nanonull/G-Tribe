package conversion7.game.ui.world.main_panel;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.VBox;
import conversion7.engine.custom2d.table.DefaultTable;
import conversion7.engine.custom2d.table.Panel;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.world.main_panel.unit.UnitActionsPanel;
import conversion7.game.ui.world.main_panel.unit.UnitAdditionalInfoPanel;
import conversion7.game.ui.world.main_panel.unit.UnitParametersBasePanelType1;
import conversion7.game.ui.world.main_panel.unit.UnitsComparisonPanel;

public class WorldUnitControlPanel extends Panel {

    public UnitsComparisonPanel unitsComparisonPanel;
    protected final UnitActionsPanel unitActionsPanel;
    private UnitAdditionalInfoPanel unitAdditionalInfoPanel;

    private AbstractSquad squad;

    public WorldUnitControlPanel() {
        VBox firstColumn = new VBox();
        add(firstColumn).width(UnitParametersBasePanelType1.MAX_WIDTH);

        firstColumn.add(new Image(Assets.pixel)).pad(ClientUi.SPACING).growX().height(1);

        unitActionsPanel = new UnitActionsPanel();
        firstColumn.add(unitActionsPanel).grow();

        firstColumn.add(new Image(Assets.pixel)).pad(ClientUi.SPACING).growX().height(1);

        unitsComparisonPanel = new UnitsComparisonPanel();
        firstColumn.add(unitsComparisonPanel).grow();

        hide();
    }

    public UnitAdditionalInfoPanel getUnitAdditionalInfoPanel() {
        return unitAdditionalInfoPanel;
    }

    public UnitsComparisonPanel getUnitsComparisonPanel() {
        return unitsComparisonPanel;
    }

    private void createSecondColumn() {
        DefaultTable secondCell = new DefaultTable();
        add(secondCell);

        secondCell.add(new Label("Info", Assets.labelStyle14white2));
        secondCell.row();
        unitAdditionalInfoPanel = new UnitAdditionalInfoPanel();
        secondCell.add(unitAdditionalInfoPanel);
        secondCell.row();
    }

    public void load(AbstractSquad squad) {
        this.squad = squad;
        if (squad == null) {
            setVisible(false);
        } else {
            setVisible(true);
            unitsComparisonPanel.selectedUnitTable.load(squad);
            unitActionsPanel.load(squad);
        }
    }
}
