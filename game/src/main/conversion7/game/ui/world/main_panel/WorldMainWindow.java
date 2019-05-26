package conversion7.game.ui.world.main_panel;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.custom2d.VBox;
import conversion7.engine.custom2d.table.DefaultTable;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.buildings.Camp;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.hint.PopupHintPanel;
import conversion7.game.ui.utils.UiUtils;

public class WorldMainWindow extends AnimatedWindow {

    public static final int HEIGHT = 300;
    public static final float POS_X = ClientUi.SPACING;
    public static final float POS_Y = ClientUi.SPACING;
    public WorldUnitControlPanel worldUnitControlPanel;
    public TeamControlsPanel teamControlsPanel;
    CellDetailsButtonsPanel buttonsRow;
    private AbstractSquad loadedSquad;
    private Camp loadedCamp;
    VBox middleColumn;
    Label yearStepLabel;
    public Cell loadedCell;
    public UiLoggerPanel uiLoggerPanel;

    public WorldMainWindow(Stage stage, Skin skin) {
        super(stage, WorldMainWindow.class.getSimpleName(), skin, Direction.up);
        DefaultTable.applyDefaults(this);

        yearStepLabel = add(new Label("", Assets.labelStyle14white2)).pad(ClientUi.SPACING).getActor();
        row();

        uiLoggerPanel = new UiLoggerPanel();
        add(uiLoggerPanel).width(300).growY();

        teamControlsPanel = new TeamControlsPanel();
        add(teamControlsPanel)
                .padLeft(ClientUi.SPACING).padRight(ClientUi.SPACING);

        Image separImage = new Image(Assets.pixel);
        add(separImage).pad(ClientUi.SPACING).padLeft(0)
                .expandY().fillY().width(1);

        middleColumn = new VBox();
        add(middleColumn).grow();
        buttonsRow = new CellDetailsButtonsPanel();
        middleColumn.add(buttonsRow).pad(ClientUi.HALF_SPACING);

        worldUnitControlPanel = new WorldUnitControlPanel();
        middleColumn.add(worldUnitControlPanel);


        hide();
    }

    private AbstractSquad getLoadedSquad() {
        return loadedSquad;
    }

    public Camp getLoadedCamp() {
        return loadedCamp;
    }

    @Override
    public void show() {
        if (!isShown() && !isShowing()) {
            setPosition(POS_X, POS_Y);
            setSize(GdxgConstants.SCREEN_WIDTH_IN_PX - ClientUi.DOUBLE_SPACING,
                    HEIGHT);
            updateAnimationBounds();
            super.show();
        }
    }

    public void refresh() {
        load(Gdxg.core.areaViewer.getSelectedCell());
    }

    private void load(Cell cell) {
        loadedCell = cell;
        if (cell == null) {
            middleColumn.hide();
            return;
        }

        buttonsRow.load(cell);
        loadedSquad = cell.squad;
        loadedCamp = cell.camp;
        worldUnitControlPanel.load(loadedSquad);
        middleColumn.show();
    }

    @Override
    public void clear() {
        load(null);
    }

    public void refreshWorldStepInfo(World world) {
        yearStepLabel.clearListeners();
        yearStepLabel.setText("Step " + world.step + ", year " + UiUtils.getNumberCode(world.year)
                /*+ ", epoch " + world.getEpochName()*/);
        PopupHintPanel.assignHintTo(yearStepLabel, "Year " + world.year);
    }
}
