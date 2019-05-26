package conversion7.game.ui.world.split_units;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.TruncatedLabel;
import conversion7.engine.custom2d.table.DefaultTable;
import conversion7.game.Assets;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.ClientUi;

public class UnitRow extends DefaultTable {

    public static final int ROW_WIDTH = (int) (SplitMergeUnitsWindow.UNITS_TABLE_WIDTH - ClientUi.SCROLL_LINE_SIZE - ClientUi.SPACING * 5);
    private static final int ICON_SIZE = 30;
    private static final int LABEL_SIZE = ROW_WIDTH - ICON_SIZE;
    private final UnitRow thiz;

    private Unit unit;
    private SplitMergeUnitsController.SplitMergeSide side;

    public UnitRow(final Unit unit) {
        this.unit = unit;

        TruncatedLabel truncatedLabel;

        DefaultTable labelsTable = new DefaultTable();
        add(labelsTable).pad(ClientUi.SPACING);
        truncatedLabel = new TruncatedLabel(LABEL_SIZE, unit.squad.getName(), Assets.labelStyle14_lightGreen);
        labelsTable.add(truncatedLabel).width(LABEL_SIZE);
        labelsTable.row();
        truncatedLabel = new TruncatedLabel(LABEL_SIZE, unit.getClass().getSimpleName(), Assets.labelStyle12_i_whiteAndLittleGreen);
        labelsTable.add(truncatedLabel).width(LABEL_SIZE).right().fillX();
        truncatedLabel.setAlignment(Align.right);

        Image icon = new Image(unit.squad.getClassIcon());
        add(icon).width(ICON_SIZE).pad(ClientUi.SPACING);

        row();
        add().height(ClientUi.SPACING);

        thiz = this;

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (unit.squad.canMove()) {
                    side.shiftUnitRow(thiz);
                } else {
                    Gdxg.clientUi.getInfoDialog().show("Could not transfer unit", "Unit could not move.");
                }
            }
        });
    }

    public Unit getUnit() {
        return unit;
    }

    public void setSide(SplitMergeUnitsController.SplitMergeSide side) {
        this.side = side;
    }
}
