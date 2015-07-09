package conversion7.game.ui.world.areaobject_activated;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.ClientUi;

import java.util.Iterator;

import static java.lang.String.valueOf;

public class CellBar extends AnimatedWindow {

    private static final int PAD = ClientUi.SPACING;

    public CellBar(Stage stage) {
        super(stage, "Cell", Assets.uiSkin, Direction.up);
        setPosition(PAD, PAD);

        addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
    }

    private class LossesData {
        int willBeKilledByTemperature = 0;
        int willBeHurtByTemperature = 0;
        int willBeKilledByHunger = 0;
        int willBeHurtByHunger = 0;
        int willBeKilledByThirst = 0;
        int willBeHurtByThirst = 0;
    }

    public void showFor(final AreaObject object) {

        clearChildren();
        defaults().left();

        LossesData lossesData = calculateLosses(object);

        addTemperatureInfo(object, lossesData);
        addFoodInfo(object, lossesData);
        addWaterInfo(object, lossesData);

        pack();
        setPosition(Gdxg.clientUi.getAreaObjectDetailsBar().getFinalWidth() + PAD * 2, PAD);
        updateAnimationBounds();

        show();
    }

    private void addTemperatureInfo(AreaObject object, LossesData lossesData) {
        Image image;

        image = new Image(Assets.temperature);
        add(image).width(image.getWidth()).center().pad(1, 1, 1, PAD);
        add(new Label("Temperature: ", Assets.labelStyle14_whiteAndLittleGreen));
        add(new Label(object.getCell().getTemperatureString(),
                object.areUnitsWithHealthyTemperature() ? Assets.labelStyle14green
                        : Assets.labelStyle14red));


        if (lossesData.willBeKilledByTemperature > 0 || lossesData.willBeHurtByTemperature > 0) {
            row();
            add(); // skip icon row
            Table lossesTable = new Table();
            add(lossesTable).colspan(2);
            lossesTable.defaults().left();

            if (lossesData.willBeKilledByTemperature > 0) {
                lossesTable.row();
                lossesTable.add(new Label(valueOf(lossesData.willBeKilledByTemperature) + " will die",
                        Assets.labelStyle14orange));
            }

            if (lossesData.willBeHurtByTemperature > 0) {
                lossesTable.row();
                lossesTable.add(new Label(valueOf(lossesData.willBeHurtByTemperature) + " will be hurt",
                        Assets.labelStyle14orange));
            }
        }
    }

    private void addFoodInfo(AreaObject object, LossesData lossesData) {
        Image image;

        row();
        image = new Image(Assets.apple);
        add(image).width(image.getWidth()).center().pad(1, 1, 1, PAD);
        add(new Label("Food: ", Assets.labelStyle14_whiteAndLittleGreen));
        add(new Label(valueOf(object.getCell().getFood()),
                object.hasEnoughFood() ? Assets.labelStyle14green : Assets.labelStyle14red));


        if (lossesData.willBeKilledByHunger > 0 || lossesData.willBeHurtByHunger > 0) {
            row();
            add(); // skip icon row
            Table lossesTable = new Table();
            add(lossesTable).colspan(2);
            lossesTable.defaults().left();

            if (lossesData.willBeKilledByHunger > 0) {
                lossesTable.row();
                lossesTable.add(new Label(valueOf(lossesData.willBeKilledByHunger) + " will die",
                        Assets.labelStyle14orange));
            }

            if (lossesData.willBeHurtByHunger > 0) {
                lossesTable.row();
                lossesTable.add(new Label(valueOf(lossesData.willBeHurtByHunger) + " will be hurt",
                        Assets.labelStyle14orange));
            }
        }
    }

    private void addWaterInfo(AreaObject object, LossesData lossesData) {
        Image image;

        row();
        image = new Image(Assets.blob);
        add(image).width(image.getWidth()).center().pad(1, 1, 1, PAD);
        add(new Label("Water: ", Assets.labelStyle14_whiteAndLittleGreen));
        add(new Label(valueOf(object.getCell().getWater()),
                object.hasEnoughWater() ? Assets.labelStyle14green : Assets.labelStyle14red));

        if (lossesData.willBeKilledByThirst > 0 || lossesData.willBeHurtByThirst > 0) {
            row();
            add(); // skip icon row
            Table lossesTable = new Table();
            add(lossesTable).colspan(2);
            lossesTable.defaults().left();

            if (lossesData.willBeKilledByThirst > 0) {
                lossesTable.row();
                lossesTable.add(new Label(valueOf(lossesData.willBeKilledByThirst) + " will die",
                        Assets.labelStyle14orange));
            }

            if (lossesData.willBeHurtByThirst > 0) {
                lossesTable.row();
                lossesTable.add(new Label(valueOf(lossesData.willBeHurtByThirst) + " will be hurt",
                        Assets.labelStyle14orange));
            }
        }
    }

    private LossesData calculateLosses(AreaObject object) {
        LossesData lossesData = new LossesData();
        Array<Unit> unitsCopy = new Array<>(object.getUnits());
        Iterator<Unit> unitCandidatesOnLosesIterator = unitsCopy.iterator();
        while (unitCandidatesOnLosesIterator.hasNext()) {
            Unit unit = unitCandidatesOnLosesIterator.next();

            int unitTemperatureAtStepEnds = unit.getTemperature() + unit.getTemperatureStepBalance();
            if (unitTemperatureAtStepEnds + unit.getEquipment().getHeat() < Unit.HEALTHY_TEMPERATURE_MIN) {
                lossesData.willBeHurtByTemperature++;
                if (unit.getHealthDamagedByEffects() <= 0) {
                    lossesData.willBeKilledByTemperature++;
                    unitCandidatesOnLosesIterator.remove();
                    continue;
                }
            }

            if (unit.getFood() <= 1 && !unit.willEat()) {
                lossesData.willBeHurtByHunger++;
                if (unit.getHealthDamagedByEffects() <= 0) {
                    lossesData.willBeKilledByHunger++;
                    unitCandidatesOnLosesIterator.remove();
                    continue;
                }
            }

            if (unit.getWater() <= 1 && !unit.willDrink()) {
                lossesData.willBeHurtByThirst++;
                if (unit.getHealthDamagedByEffects() <= 0) {
                    lossesData.willBeKilledByThirst++;
                    unitCandidatesOnLosesIterator.remove();
                    continue;
                }
            }
        }
        return lossesData;
    }

}
