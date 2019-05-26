package conversion7.game.ui.world.highlight_cell;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.StringBuilder;
import conversion7.engine.custom2d.table.DefaultTable;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.Landscape;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.ClientUi;

import static java.lang.String.valueOf;

public class CellInfoPanel extends DefaultTable {

    private static final int PAD = ClientUi.SPACING;
    private final Image foodImage;
    private final Image temperatureImage;
    private final Image waterImage;

    Label forest = new Label("Forest", Assets.labelStyle14_lightGreen);
    Label bog = new Label("Bog", Assets.labelStyle14orange);
    Label hill = new Label("Hill", Assets.labelStyle14_lightGreen);
    Label mountain = new Label("Mountain", Assets.labelStyle14orange);
    Label water = new Label("Water", Assets.labelStyle14orange);
    Label gatheringValue = new Label("", Assets.labelStyle14yellow);
    Label moreDescriptions = new Label("", Assets.labelStyle14white2);

    public CellInfoPanel() {
        temperatureImage = new Image(Assets.temperature);
        foodImage = new Image(Assets.apple);
        waterImage = new Image(Assets.blob);
    }

    public void load(Cell cell, AbstractSquad selectedSquad) {
        clearChildren();
        defaults().left();

        Label label;

        gatheringValue.setText("Gathering value: " + cell.getGatheringValue());
        add(gatheringValue);
        row();

        Table cellNumbers = new DefaultTable();
        add(cellNumbers).fill().expand();
        cellNumbers.add(temperatureImage).width(temperatureImage.getWidth()).center().pad(1, 1, 1, PAD);
        Label label1 = new Label("Temperature: ", Assets.labelStyle14white2);
        cellNumbers.add(label1);
        label1.setColor(Color.WHITE);
        cellNumbers.add(new Label(cell.getTemperatureString(), cell.hasHealthyTemperature() ? Assets.labelStyle14green
                : Assets.labelStyle14red));

        cellNumbers.row();

        cellNumbers.add(foodImage).width(foodImage.getWidth()).center().pad(1, 1, 1, PAD);
        Label label2 = new Label("Food: ", Assets.labelStyle14white2);
        cellNumbers.add(label2);
        label2.setColor(Color.WHITE);
        label = new Label(valueOf(cell.getFoodUi()),
                cell.hasEnoughUnitFood() ? Assets.labelStyle14green : Assets.labelStyle14red);
        cellNumbers.add(label);

        cellNumbers.row();

        cellNumbers.add(waterImage).width(waterImage.getWidth()).center().pad(1, 1, 1, PAD);
        Label label3 = new Label("Water: ", Assets.labelStyle14white2);
        cellNumbers.add(label3);
        label3.setColor(Color.WHITE);
        label = new Label(valueOf(cell.getWaterUi()),
                cell.hasEnoughUnitWater() ? Assets.labelStyle14green : Assets.labelStyle14red);
        cellNumbers.add(label);
        cellNumbers.row();
        row();

        addMoreCellDescription(cell);
        row();

        pack();
    }

    private void addMoreCellDescription(Cell cell) {
        add().height(ClientUi.DOUBLE_SPACING);
        row();

        if (cell.getLandscape().type == Landscape.Type.WATER) {
            add(water);
            row();
            return;
        }
        if (cell.getLandscape().type == Landscape.Type.MOUNTAIN) {
            add(mountain);
            row();
            return;
        }


        if (cell.getLandscape().hasForest()) {
            add(forest);
            row();
        }
        if (cell.getLandscape().hasHill()) {
            add(hill);
            row();
        }
        if (cell.getLandscape().hasBog()) {
            add(bog);
            row();
        }

        StringBuilder moreDescriptionBuilder = new StringBuilder();
        for (AreaObject object : cell.getObjectsOnCell()) {
            moreDescriptionBuilder.append(object.getShortHint()).append("\n");
        }

        if (moreDescriptionBuilder.length() > 0) {
            add(moreDescriptions);
            moreDescriptions.setText(moreDescriptionBuilder.toString());
            row();
        }
    }


}
