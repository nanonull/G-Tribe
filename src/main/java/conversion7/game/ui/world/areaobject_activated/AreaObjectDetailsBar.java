package conversion7.game.ui.world.areaobject_activated;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.custom2d.ImageWithLabel;
import conversion7.engine.custom2d.table.DefaultTable;
import conversion7.engine.custom2d.table.TableWithScrollPane;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.effects.AbstractObjectEffect;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.ClientUi;
import conversion7.game.utils.collections.IterationRegistrators;

import static java.lang.String.format;
import static java.lang.String.valueOf;

public class AreaObjectDetailsBar extends AnimatedWindow {


    private DetailsTable detailsTable = new DetailsTable();
    private UnitsTable unitsTable = new UnitsTable();
    private EffectsTable effectsTable = new EffectsTable();

    private static final int PAD = ClientUi.SPACING;

    public AreaObjectDetailsBar(Stage stage) {
        super(stage, "Units", Assets.uiSkin, Direction.up);

        add(effectsTable.getScrollPane()).pad(PAD).width(UnitsTable.WIDTH);
        effectsTable.getScrollPane().setScrollingDisabled(false, true);

        row();
        add(detailsTable).pad(PAD).width(UnitsTable.WIDTH);

        row();
        add(unitsTable.getScrollPane()).pad(0, PAD, PAD, PAD)
                .width(UnitsTable.WIDTH)
                .height(UnitsTable.HEIGHT);
        unitsTable.getScrollPane().setScrollingDisabled(true, false);
    }

    public void showFor(final AreaObject object) {
        getTitleLabel().setText(object.getName());
        effectsTable.refreshFor(object);
        detailsTable.refreshFor(object);
        unitsTable.refreshFor(object);
        pack();
        setPosition(PAD, PAD);
        updateAnimationBounds();
        show();
    }

    private static class EffectsTable extends TableWithScrollPane {
        static final int ICON_SIZE = 24;

        public void refreshFor(AreaObject object) {
            clearChildren();
            Array<AbstractObjectEffect> effects = object.getEffects();

            if (effects.size > 0) {
                for (AbstractObjectEffect effect : effects) {
                    add(effect.getImage()).size(ICON_SIZE).pad(PAD);
                }
                addSpaceForHorizontalScroll();
            }
        }
    }

    private static class UnitsTable extends TableWithScrollPane {

        static final int ICON_SIZE = 56;
        static final int HEIGHT = ICON_SIZE * 2 + PAD * 5;
        private static final int ICONS_AMOUNT_IN_ROW = 5;
        public static final float WIDTH = ICONS_AMOUNT_IN_ROW * (ICON_SIZE + PAD * 2) + ClientUi.SCROLL_LINE_SIZE + PAD * 2;

        private void refreshFor(AreaObject object) {
            clearChildren();

            ObjectMap<Class<? extends Unit>, Integer> mapClassAmount = new ObjectMap<>();
            IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
            for (int i = 0; i < object.getUnits().size; i++) {
                Unit unit = object.getUnits().get(i);
                Integer curAmount = mapClassAmount.get(unit.getClass());
                mapClassAmount.put(unit.getClass(), curAmount == null ? 1 : ++curAmount);
            }
            IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();

            int column = 0;
            for (ObjectMap.Entry<Class<? extends Unit>, Integer> classIntegerEntry : mapClassAmount.entries()) {
                Image image = new Image(Assets.CLASS_ICONS.get(classIntegerEntry.key));
                Label label = new Label(valueOf(classIntegerEntry.value), Assets.labelStyle14blackWithBackground);
                ImageWithLabel imageWithLabel = new ImageWithLabel(image, label, Align.right);
                add(imageWithLabel).size(ICON_SIZE).pad(PAD);

                column++;
                if (column == ICONS_AMOUNT_IN_ROW) {
                    add().width(ClientUi.SCROLL_LINE_SIZE + PAD); // scroll bar pad
                    row().height(ICON_SIZE).left().top();
                    column = 0;
                }
            }
        }
    }

    private static class DetailsTable extends DefaultTable {

        private Label foodStorageLabel = new Label("", Assets.labelStyle14_whiteAndLittleGreen);
        private Label taskLabel = new Label("", Assets.labelStyle14_whiteAndLittleGreen);
        private Label unitsHaveNoAPLabel = new Label("", Assets.labelStyle14_whiteAndLittleGreen);

        DetailsTable() {
            add(foodStorageLabel);
            row();
            add(taskLabel);
            row();
            add(unitsHaveNoAPLabel);
        }

        private void refreshFor(AreaObject object) {
            foodStorageLabel.setText(format("Food storage: %d/%d", object.getFoodStorage().getFood(), object.getFoodStorage().getFoodMax()));

            if (object.getActiveTask() == null) {
                taskLabel.setText("");
            } else {
                taskLabel.setText("Task: " + object.getActiveTask().getDescription());
            }

            int amountUnitsWithoutActionPoints = object.getAmountUnitsWithoutActionPoints();
            if (amountUnitsWithoutActionPoints == 0) {
                unitsHaveNoAPLabel.setText("");
            } else if (amountUnitsWithoutActionPoints == object.getUnits().size) {
                unitsHaveNoAPLabel.setText("There are no units with action points");
            } else {
                unitsHaveNoAPLabel.setText(amountUnitsWithoutActionPoints + " unit(s) have no action points");
            }
        }
    }

}
