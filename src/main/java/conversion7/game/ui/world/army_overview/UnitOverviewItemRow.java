package conversion7.game.ui.world.army_overview;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.TruncatedLabel;
import conversion7.engine.custom2d.table.DefaultTable;
import conversion7.engine.custom2d.table.HeaderCellData;
import conversion7.engine.custom2d.table.TableHeaderData;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.HintForm;
import conversion7.game.ui.utils.UiUtils;
import org.slf4j.Logger;

import static java.lang.String.valueOf;

public class UnitOverviewItemRow extends DefaultTable {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static ProgressBar.ProgressBarStyle BAR_STYLE = ArmyOverviewWindow.BAR_STYLE;

    public UnitOverviewItemRow(final Unit unit, TableHeaderData tableHeaderData) {
        applyDefaultPaddings();
        defaults().center();
        Array<HeaderCellData> headers = tableHeaderData.getHeaders();

        HeaderCellData headerCellData;
        Image image;
        TruncatedLabel truncatedLabel;
        ProgressBar progressBar;
        Label label;

        int curColumn = 0;

        // name
        headerCellData = headers.get(curColumn);
        Table nameAndClassTable = new Table();
        add(nameAndClassTable).width(headerCellData.getWidth()).expand().fill().padLeft(ClientUi.SPACING).padRight(ClientUi.SPACING);
        nameAndClassTable.left().top();
        //
        truncatedLabel = new TruncatedLabel(headerCellData.getWidth(),
                unit.getName(), Assets.labelStyle14_lightGreen);
        nameAndClassTable.add(truncatedLabel).left();
        nameAndClassTable.row();
        truncatedLabel = new TruncatedLabel(headerCellData.getWidth(),
                unit.getClass().getSimpleName(), Assets.labelStyle12_i_whiteAndLittleGreen);
        nameAndClassTable.add(truncatedLabel).right().expandX().fillX();
        truncatedLabel.setAlignment(Align.right);
        curColumn++;

        // icon
        headerCellData = headers.get(curColumn);
        image = new Image(unit.getClassIcon());
        add(image).size(headerCellData.getWidth());
        HintForm.assignHintTo(image, unit.getEquipment().getHint());
        curColumn++;

        // health
        headerCellData = headers.get(curColumn);
        Table healthTable = new Table();
        add(healthTable).width(headerCellData.getWidth()).expand().fill();
        healthTable.defaults().center();
        //
        truncatedLabel = new TruncatedLabel(
                headerCellData.getWidth(),
                UiUtils.getShortStringFromInt(unit.getParams().getHealth()) + "/"
                        + UiUtils.getShortStringFromInt(unit.getMaxHealth()), Assets.labelStyle14yellow);
        healthTable.add(truncatedLabel);
        healthTable.row();
        progressBar = new ProgressBar(0, 100, 1, false, BAR_STYLE);
        healthTable.add(progressBar).width(
                headerCellData.getWidth()/*HEADERS[curColumn].widthPixels - ClientUi.DOUBLE_SPACING*/);
        int healthPercent = (int) (((float) unit.getParams().getHealth() / (float) unit.getMaxHealth()) * 100);
        progressBar.setValue(healthPercent);
        curColumn++;

        // specialization
        headerCellData = headers.get(curColumn);
        final SelectBox selectBox = new SelectBox(Assets.uiSkin);
        selectBox.setItems(Unit.UnitSpecialization.values());
        selectBox.setSelected(unit.getSpecialization());
        selectBox.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                LOG.info(selectBox.getSelected().toString());
                unit.setSpecialization((Unit.UnitSpecialization) selectBox.getSelected());
            }
        });
        selectBox.setScale(0.8f);
        add(selectBox).width(headerCellData.getWidth());
        curColumn++;

        // temperature
        headerCellData = headers.get(curColumn);
        label = new Label(unit.getTemperatureString(),
                unit.hasHealthyTemperature() ? Assets.labelStyle14green
                        : Assets.labelStyle14red);
        label.setAlignment(Align.center);
        add(label).width(headerCellData.getWidth());
        curColumn++;

        // food
        headerCellData = headers.get(curColumn);
        progressBar = new ProgressBar(0, 100, 1, false, BAR_STYLE);
        add(progressBar).width(headerCellData.getWidth())/*.width(HEADERS[curColumn].widthPixels - ClientUi.DOUBLE_SPACING)*/;
        int foodPercent = Math.round((float) unit.getFood() / (float) Unit.FOOD_LIMIT * 100);
        progressBar.setValue(foodPercent);
        curColumn++;

        // water
        headerCellData = headers.get(curColumn);
        progressBar = new ProgressBar(0, 100, 1, false, BAR_STYLE);
        add(progressBar).width(headerCellData.getWidth())/*.width(HEADERS[curColumn].widthPixels - ClientUi.DOUBLE_SPACING)*/;
        int waterPercent = Math.round((float) unit.getWater() / (float) Unit.WATER_LIMIT * 100);
        progressBar.setValue(waterPercent);
        curColumn++;

        // effects
        headerCellData = headers.get(curColumn);
        if (unit.getEffectManager().effects.size > 0) {
            image = new Image(unit.getEffectManager().getTotalEffectsIcon());
            add(image).width(headerCellData.getWidth());
            HintForm.assignHintTo(image, unit.getEffectManager().getEffectsTableForHint());
        } else {
            label = new Label("-", Assets.labelStyle14yellow);
            add(label).width(headerCellData.getWidth());
        }
        curColumn++;

        headerCellData = headers.get(curColumn);
        label = new Label(valueOf(unit.getLevel()), Assets.labelStyle14yellow);
        label.setAlignment(Align.center);
        add(label).width(headerCellData.getWidth());
        curColumn++;

        headerCellData = headers.get(curColumn);
        label = new Label(valueOf(unit.getGender()), Assets.labelStyle14yellow);
        label.setAlignment(Align.center);
        add(label).width(headerCellData.getWidth());
        curColumn++;

        // strength
        headerCellData = headers.get(curColumn);
        label = new Label(UiUtils.getShortStringFromInt(unit.getStrength()), Assets.labelStyle14yellow);
        label.setAlignment(Align.center);
        add(label).width(headerCellData.getWidth());
        curColumn++;

        headerCellData = headers.get(curColumn);
        label = new Label(UiUtils.getShortStringFromInt(unit.getAgility()), Assets.labelStyle14yellow);
        label.setAlignment(Align.center);
        add(label).width(headerCellData.getWidth());
        curColumn++;

        headerCellData = headers.get(curColumn);
        label = new Label(UiUtils.getShortStringFromInt(unit.getVitality()), Assets.labelStyle14yellow);
        label.setAlignment(Align.center);
        add(label).width(headerCellData.getWidth());
        curColumn++;

        // remove unit
        headerCellData = headers.get(curColumn);
        TextButton removeButton = new TextButton("X", Assets.uiSkin);
        removeButton.center();
        add(removeButton).width(headerCellData.getWidth()).center();
        curColumn++;

        removeButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO check if all other units total power > targett unit power to kill
                if (unit.getAreaObject().getUnits().size > 1) {
                    unit.killedByTeammates();
                    ArmyOverviewWindow armyOverviewWindow = Gdxg.clientUi.getArmyOverviewWindow();
                    AreaObject loadedAreaObject = armyOverviewWindow.getLoadedAreaObject();
                    if (loadedAreaObject.validateAndDefeat()) {
                        armyOverviewWindow.hide();
                        World.getAreaViewer().deselect();
                    } else {
                        armyOverviewWindow.updateBodyContent(loadedAreaObject);
                    }
                }
            }
        });

        // id
        headerCellData = headers.get(curColumn);
        label = new Label(valueOf(unit.id), Assets.labelStyle14yellow);
        label.setAlignment(Align.center);
        add(label).width(headerCellData.getWidth());

        add().width(ClientUi.SCROLL_LINE_SIZE);
    }
}
