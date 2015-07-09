package conversion7.game.ui.world.army_overview;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.table.HeaderCellData;
import conversion7.engine.custom2d.table.TableHeaderData;
import conversion7.engine.custom2d.table.TableWithHeader;
import conversion7.engine.custom2d.table.TableWithScrollPane;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.inputlisteners.ContinuousInput;

public class ArmyOverviewWindow extends AnimatedWindow {

    private final static int WINDOW_WIDTH = GdxgConstants.SCREEN_WIDTH_IN_PX;
    private final static int WINDOW_HEIGHT = (int) (GdxgConstants.SCREEN_HEIGHT_IN_PX * 0.65f);
    private final static int PAD = ClientUi.SPACING;
    private final static int DOUBLE_PAD = PAD * 2;
    private static final int SCROLLBAR_WIDTH = ClientUi.SCROLL_LINE_SIZE;
    public static ProgressBar.ProgressBarStyle BAR_STYLE;

    static {
        TextureRegionDrawable knobDrawable = new TextureRegionColoredDrawable(Color.GREEN, Assets.pixelWhite);
        TextureRegionDrawable backDrawable = new TextureRegionColoredDrawable(Color.RED, Assets.pixelWhite);
        BAR_STYLE = new ProgressBar.ProgressBarStyle(backDrawable, knobDrawable);
        BAR_STYLE.knob.setMinHeight(6);
        BAR_STYLE.knobBefore = BAR_STYLE.knob;
        BAR_STYLE.background.setMinHeight(5);
    }

    ScrollPane scrollBody;
    TableWithHeader tableHeaderAndBody2;
    private AreaObject loadedAreaObject;

    public ArmyOverviewWindow(Stage stage) {
        super(stage, "Units", Assets.uiSkin, Direction.down);
        setName("ArmyOverviewWindow");
        setBounds(0, GdxgConstants.SCREEN_HEIGHT_IN_PX - WINDOW_HEIGHT, WINDOW_WIDTH, WINDOW_HEIGHT);
        updateAnimationBounds();
        addCloseButton();

        top();
        TableWithScrollPane mainInventoryTableInner = new TableWithScrollPane();
        mainInventoryTableInner.getScrollPane().setScrollingDisabled(true, false);

        TableHeaderData mainTableHeaderData =
                new TableHeaderData(WINDOW_WIDTH - ClientUi.DOUBLE_SPACING - ClientUi.SCROLL_LINE_SIZE);
        mainTableHeaderData.addHeaderCell(new HeaderCellData(20, "Name/class", null, null, 1));
        mainTableHeaderData.addHeaderCell(new HeaderCellData(4, null, null, null));
        mainTableHeaderData.addHeaderCell(new HeaderCellData(9, "Health", null, null, 1));
        mainTableHeaderData.addHeaderCell(new HeaderCellData(8, "SPEC", null, "Specialization"));
        mainTableHeaderData.addHeaderCell(new HeaderCellData(3, null, new Image(Assets.temperature), "Temperature"));
        mainTableHeaderData.addHeaderCell(new HeaderCellData(5, null, new Image(Assets.apple), "Food"));
        mainTableHeaderData.addHeaderCell(new HeaderCellData(5, null, new Image(Assets.blob), "Water"));
        mainTableHeaderData.addHeaderCell(new HeaderCellData(4, "EFF", null, "Effects"));
        mainTableHeaderData.addHeaderCell(new HeaderCellData(4, "LVL", null, "Level"));
        mainTableHeaderData.addHeaderCell(new HeaderCellData(5, "GEN", null, "Gender"));
        mainTableHeaderData.addHeaderCell(new HeaderCellData(4, "STR", null, "Strength"));
        mainTableHeaderData.addHeaderCell(new HeaderCellData(4, "AGI", null, "Agility"));
        mainTableHeaderData.addHeaderCell(new HeaderCellData(4, "VIT", null, "Vitality"));
        mainTableHeaderData.addHeaderCell(new HeaderCellData(4, null, null, null));
        mainTableHeaderData.addHeaderCell(new HeaderCellData(5, "ID", null, null));
        tableHeaderAndBody2 = new TableWithHeader(mainTableHeaderData, mainInventoryTableInner, mainInventoryTableInner.getScrollPane());

        add(tableHeaderAndBody2.getMainTable()).center();

        addListener(new ClickListener() {

            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                scrollBody.setScrollPercentY(scrollBody.getScrollPercentY() + 1);
                return true;
            }
        });

    }

    public AreaObject getLoadedAreaObject() {
        return loadedAreaObject;
    }


    void updateBodyContent(final AreaObject object) {
        tableHeaderAndBody2.clearChildren();
        Image image;
        for (int i = 0; i < object.getUnits().size; i++) {
            final Unit unit = object.getUnits().get(i);
            UnitOverviewItemRow inventoryItemRow = new UnitOverviewItemRow(unit, tableHeaderAndBody2.getTableHeaderData());
            tableHeaderAndBody2.add(inventoryItemRow);

            if (i + 1 < object.getUnits().size) {
                tableHeaderAndBody2.row().center().height(1).padLeft(DOUBLE_PAD).padRight(SCROLLBAR_WIDTH + DOUBLE_PAD);
                image = new Image(Assets.pixelWhite);
                tableHeaderAndBody2.add(image).fillX().colspan(tableHeaderAndBody2.getTableHeaderData().getHeaders().size);
                image.setColor(Assets.LIGHT_GRAY);
            }

            tableHeaderAndBody2.row();
        }
    }

    public void showFor(AreaObject areaObject) {
        loadedAreaObject = areaObject;
        ContinuousInput.setEnabled(false);
        updateBodyContent(areaObject);
        show();
        Gdxg.clientUi.hideBarsForSelectedObject();
        linkedStage.addTouchFocus(getListeners().first(), this, this, 0, 0);
    }

    @Override
    public void hide() {
        super.hide();
        ContinuousInput.setEnabled(true);
        if (World.getAreaViewer().selectedObject != null) {
            Gdxg.clientUi.showBarsForSelectedObject();
        }
    }

}
