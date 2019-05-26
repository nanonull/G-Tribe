package conversion7.game.ui.world.main_panel;

import conversion7.engine.custom2d.HBox;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObjectDetailsDescriptor;
import conversion7.game.stages.world.objects.buildings.Camp;
import conversion7.game.stages.world.objects.totem.AbstractTotem;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.world.CampDetailsPanel;
import conversion7.game.ui.world.CellDetailsPanel;
import conversion7.game.ui.world.TotemDetailsPanel;
import conversion7.game.ui.world.highlight_cell.HighlightedCellPanel;
import conversion7.game.ui.world.object_details.AreaObjectDetailsGenericPanel;

public class CellDetailsRootPanel extends HBox {
    private HighlightedCellPanel highlightedCellPanel = new HighlightedCellPanel();
    private TotemDetailsPanel totemDetailsPanel = new TotemDetailsPanel();
    private CellDetailsPanel cellDetailsPanel = new CellDetailsPanel();
    private CampDetailsPanel campDetailsPanel = new CampDetailsPanel();
    private AreaObjectDetailsGenericPanel genericPanel = new AreaObjectDetailsGenericPanel();
    HBox additionalPanelsRoot;

    public CellDetailsRootPanel() {
        defaults().space(2).bottom().left();
        setWidth(GdxgConstants.SCREEN_WIDTH_IN_PX - ClientUi.DOUBLE_SPACING);
        additionalPanelsRoot = new HBox();
        add(additionalPanelsRoot).left();
        add().growX();
        add(highlightedCellPanel).right();

    }

    public HighlightedCellPanel getHighlightedCellPanel() {
        return highlightedCellPanel;
    }

    @Override
    public void validate() {
        super.validate();
        setPosition(ClientUi.SPACING,
                ClientUi.DOUBLE_SPACING + WorldMainWindow.HEIGHT + getRowHeight(0));
    }

    public void resetAdditionalUi() {
        additionalPanelsRoot.clearChildren();
    }

    public void load(Cell cell) {
        resetAdditionalUi();
        additionalPanelsRoot.add(cellDetailsPanel);
        cellDetailsPanel.load(cell);
    }

    public void load(Camp camp, boolean checkIfLoaded) {
        if (checkIfLoaded && !campDetailsPanel.isLoaded(camp)) {
            return;
        }
        resetAdditionalUi();
        additionalPanelsRoot.add(campDetailsPanel);
        campDetailsPanel.load(camp);
    }

    public void load(AbstractTotem totem) {
        resetAdditionalUi();
        additionalPanelsRoot.add(totemDetailsPanel);
        totemDetailsPanel.load(totem);
    }

    public void load(AreaObjectDetailsDescriptor object) {
        resetAdditionalUi();
        additionalPanelsRoot.add(genericPanel);
        genericPanel.load(object);
    }

}
