package conversion7.game.ui.world;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.custom2d.ImageWithLabel;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.ClientUi;
import conversion7.game.utils.collections.IterationRegistrators;

import static java.lang.String.valueOf;

public class HighlightedCellBar extends AnimatedWindow {

    private static final int PAD = ClientUi.SPACING;
    private static final int ICON_SIZE = 64;
    private static final int ICONS_AMOUNT_IN_ROW = 4;
    private boolean updateWindowOnShowCompleted = false;

    public HighlightedCellBar(Stage stage) {
        super(stage, "", Assets.uiSkin, Direction.up);
    }

    public void showOn(Cell cell, AreaObject selectedObject) {
        clearChildren();

        describeCell(cell, selectedObject);
        AreaObject cellObject = cell.getSeizedBy();
        if (cellObject != null) {
            describeObject(cellObject);
        }

        if (isShowing()) {
            updateWindowOnShowCompleted = true;
        } else if (!isDisplayed() || isHiding()) {
            show();
        } else {
            refresh();
        }
    }

    private void refresh() {
        pack();
        setPosition(GdxgConstants.SCREEN_WIDTH_IN_PX - getWidth() - ClientUi.SPACING,
                ClientUi.SPACING);
    }

    @Override
    public void onShow() {
        refresh();
        updateAnimationBounds();
    }

    @Override
    public void onShowCompleted() {
        if (updateWindowOnShowCompleted) {
            updateWindowOnShowCompleted = false;
            refresh();
        }
    }

    private void describeCell(Cell cell, AreaObject selectedObject) {
        Label label;
        Image image;

        image = new Image(Assets.temperature);
        add(image).width(image.getWidth()).center().pad(1, 1, 1, PAD);
        label = new Label("Temperature: ", Assets.labelStyle14_whiteAndLittleGreen);
        add(label).left();
        if (selectedObject == null) {
            label = new Label(cell.getTemperatureString(), Assets.labelStyle14yellow);
        } else {
            label = new Label(cell.getTemperatureString(),
                    cell.hasGenerallyHealthyTemperature() ? Assets.labelStyle14green
                            : Assets.labelStyle14red);
        }
        add(label).left();

        row();
        image = new Image(Assets.apple);
        add(image).width(image.getWidth()).center().pad(1, 1, 1, PAD);
        label = new Label("Food: ", Assets.labelStyle14_whiteAndLittleGreen);
        add(label).left();
        if (selectedObject == null) {
            label = new Label(valueOf(cell.getFood()), Assets.labelStyle14yellow);
        } else {
            label = new Label(valueOf(cell.getFood()),
                    cell.hasEnoughFoodFor(selectedObject) ? Assets.labelStyle14green : Assets.labelStyle14red);
        }
        add(label).left();

        row();
        image = new Image(Assets.blob);
        add(image).width(image.getWidth()).center().pad(1, 1, 1, PAD);
        label = new Label("Water: ", Assets.labelStyle14_whiteAndLittleGreen);
        add(label).left();
        if (selectedObject == null) {
            label = new Label(valueOf(cell.getWater()), Assets.labelStyle14yellow);
        } else {
            label = new Label(valueOf(cell.getWater()),
                    cell.hasEnoughWaterFor(selectedObject) ? Assets.labelStyle14green : Assets.labelStyle14red);
        }
        add(label).left();
    }

    private void describeObject(AreaObject cellObject) {
        Label label;

        row().height(10);
        add();

        row().left();
        add();
        label = new Label("Team: ", Assets.labelStyle14yellow);
        add(label);
        label = new Label(cellObject.getTeam().getName(), Assets.labelStyle14yellow);
        add(label);

        row().left();
        add();
        label = new Label("Units: ", Assets.labelStyle14yellow);
        add(label);
        label = new Label(valueOf(cellObject.getUnits().size), Assets.labelStyle14yellow);
        add(label);


        row().left();
        Table tableUnits = new Table();
        add(tableUnits).colspan(3);

        ObjectMap<Class<? extends Unit>, Integer> mapClassAmount = new ObjectMap<>();
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
        for (int i = 0; i < cellObject.getUnits().size; i++) {
            Unit unit = cellObject.getUnits().get(i);
            Integer curAmount = mapClassAmount.get(unit.getClass());
            mapClassAmount.put(unit.getClass(), curAmount == null ? 1 : ++curAmount);
        }
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();

        int column = 0;
        for (ObjectMap.Entry<Class<? extends Unit>, Integer> classIntegerEntry : mapClassAmount.entries()) {
            Image image = new Image(Assets.CLASS_ICONS.get(classIntegerEntry.key));
            label = new Label(valueOf(classIntegerEntry.value), Assets.labelStyle14blackWithBackground);
            ImageWithLabel imageWithLabel = new ImageWithLabel(image, label, Align.right);
            tableUnits.add(imageWithLabel).size(ICON_SIZE).left().pad(PAD);

            column++;
            if (column == ICONS_AMOUNT_IN_ROW) {
                tableUnits.row().height(ICON_SIZE).left().top();
                column = 0;
            }
        }

    }

}
