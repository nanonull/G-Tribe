package conversion7.game.ui.world.split_units;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.custom2d.table.DefaultTable;
import conversion7.engine.custom2d.table.TableWithScrollPane;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.ClientUi;
import conversion7.game.utils.collections.IterationRegistrators;

// TODO highlight row by mouse
public class SplitMergeUnitsWindow extends AnimatedWindow {

    public static final float UNITS_TABLE_WIDTH = 200;
    private static final float UNITS_TABLE_HEIGHT = 500;
    private static final float TRANSFER_BUTTON_WIDTH = UNITS_TABLE_WIDTH / 3;
    private static final float TRANSFER_BUTTON_HEIGHT = TRANSFER_BUTTON_WIDTH * 1.5f;
    private final SplitMergeUnitsWindow myLink;

    /** Will contain all Tables and elements in one row */
    private DefaultTable mainTable = new DefaultTable();

    private TableWithScrollPane leftSideUnitsTable;
    private TableWithScrollPane leftSideUnitsForTransferTable;
    private TableWithScrollPane rightSideUnitsTable;
    private TableWithScrollPane rightSideUnitsForTransferTable;

    private final SplitMergeUnitsController splitMergeUnitsController;

    public SplitMergeUnitsWindow(Stage stage, String title, Skin skin, int direction) {
        super(stage, title, skin, direction);
        this.myLink = this;
        setSize(GdxgConstants.SCREEN_WIDTH_IN_PX, GdxgConstants.SCREEN_HEIGHT_IN_PX);

        addCloseButton();

        add(new Label("Click on units to move them between initial list and list for transfer", Assets.labelStyle12_i_lightGreen)).center().expandX();
        row();
        add().height(ClientUi.SPACING * 5);

        row();
        add(mainTable);

        // header for mainTable
        mainTable.add(new Label("Initiator", Assets.labelStyle18yellow)).bottom().center();
        mainTable.add(new Label("transfer list", Assets.labelStyle14_lightGreen)).bottom().center();
        mainTable.add(); // middle
        mainTable.add(new Label("transfer list", Assets.labelStyle14_lightGreen)).bottom().center();
        mainTable.add(new Label("Target", Assets.labelStyle18yellow)).bottom().center();

        mainTable.row();
        // 4 tables with Transfer button in the middle

        leftSideUnitsTable = new TableWithScrollPane();
        mainTable.add(leftSideUnitsTable.getScrollPane()).size(UNITS_TABLE_WIDTH, UNITS_TABLE_HEIGHT);
        leftSideUnitsTable.getScrollPane().setScrollingDisabled(true, false);

        leftSideUnitsForTransferTable = new TableWithScrollPane();
        mainTable.add(leftSideUnitsForTransferTable.getScrollPane()).size(UNITS_TABLE_WIDTH, UNITS_TABLE_HEIGHT);
        leftSideUnitsForTransferTable.getScrollPane().setFadeScrollBars(false);
        leftSideUnitsForTransferTable.getScrollPane().setScrollingDisabled(true, false);

        addTransferButton();

        rightSideUnitsForTransferTable = new TableWithScrollPane();
        mainTable.add(rightSideUnitsForTransferTable.getScrollPane()).size(UNITS_TABLE_WIDTH, UNITS_TABLE_HEIGHT);
        rightSideUnitsForTransferTable.getScrollPane().setFadeScrollBars(false);
        rightSideUnitsForTransferTable.getScrollPane().setScrollingDisabled(true, false);

        rightSideUnitsTable = new TableWithScrollPane();
        mainTable.add(rightSideUnitsTable.getScrollPane()).size(UNITS_TABLE_WIDTH, UNITS_TABLE_HEIGHT);
        rightSideUnitsTable.getScrollPane().setFadeScrollBars(false);
        rightSideUnitsTable.getScrollPane().setScrollingDisabled(true, false);

        splitMergeUnitsController = new SplitMergeUnitsController(this);
    }

    private void addTransferButton() {
        TextButton okButton = new TextButton(">>>\nAccept\nTransfer\n<<<", Assets.uiSkin);
        mainTable.add(okButton).center().width(TRANSFER_BUTTON_WIDTH).height(TRANSFER_BUTTON_HEIGHT).pad(ClientUi.SPACING);
        okButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                splitMergeUnitsController.executeTransfer();
            }
        });
    }

    public TableWithScrollPane getLeftSideUnitsTable() {
        return leftSideUnitsTable;
    }

    public TableWithScrollPane getLeftSideUnitsForTransferTable() {
        return leftSideUnitsForTransferTable;
    }

    public TableWithScrollPane getRightSideUnitsTable() {
        return rightSideUnitsTable;
    }

    public TableWithScrollPane getRightSideUnitsForTransferTable() {
        return rightSideUnitsForTransferTable;
    }

    @Override
    protected void addCloseButton() {
        TextButton closeButton = new TextButton("  X  ", Assets.uiSkin);
        add(closeButton).right().expandX();
        closeButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                hide();
                Gdxg.clientUi.enableWorldInteraction();
            }
        });

        row();
    }

    public void showFor(AreaObject object, Cell target) {
        Gdxg.clientUi.disableWorldInteraction();
        World.getAreaViewer().hideSelection();
        Gdxg.clientUi.getHighlightedCellBar().hide();
        refreshContent(object, target);
        pack();
        setPosition(ClientUi.SPACING, GdxgConstants.SCREEN_HEIGHT_IN_PX - ClientUi.SPACING - getHeight());
        updateAnimationBounds();
        show();
    }

    private void refreshContent(AreaObject object, Cell target) {
        leftSideUnitsTable.clearChildren();
        leftSideUnitsForTransferTable.clearChildren();
        rightSideUnitsTable.clearChildren();
        rightSideUnitsForTransferTable.clearChildren();
        splitMergeUnitsController.reset();

        splitMergeUnitsController.setInitiatorTeam(object.getTeam());
        splitMergeUnitsController.getLeftSide().wrapAreaObject(object);

        if (target.isSeized()) {
            AreaObject secondObject = target.getSeizedBy();
            splitMergeUnitsController.getRightSide().wrapAreaObject(secondObject);
            IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
            for (int i = 0; i < secondObject.getUnits().size; i++) {
                Unit unit = secondObject.getUnits().get(i);
                splitMergeUnitsController.getRightSide().addUnitRowToMainTable(new UnitRow(unit));
            }
            IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();
        } else {
            splitMergeUnitsController.getRightSide().wrapCell(target);
        }

        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
        for (int i = 0; i < object.getUnits().size; i++) {
            Unit unit = object.getUnits().get(i);
            splitMergeUnitsController.getLeftSide().addUnitRowToMainTable(new UnitRow(unit));
        }
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();
    }

}
