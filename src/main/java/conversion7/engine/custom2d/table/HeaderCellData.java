package conversion7.engine.custom2d.table;

import com.badlogic.gdx.scenes.scene2d.Actor;
import conversion7.engine.utils.FastAsserts;

public class HeaderCellData {
    private static final int CHILD_TABLE_INCREASE_SIZE_OF_CELL = 2;
    private int widthPercent;
    private int widthCompensation;
    private int width;

    private String name;
    private Actor iconForHeaderButton;
    private String hintText;

    public HeaderCellData(int widthPercent, String name, Actor iconForHeaderButton, String hintText) {
        this(widthPercent, name, iconForHeaderButton, hintText, 0);
    }

    public HeaderCellData(int widthPercent, String name, Actor iconForHeaderButton, String hintText, int numberOfChildTablesInColumnCell) {
        this.widthPercent = widthPercent;
        this.widthCompensation = numberOfChildTablesInColumnCell * CHILD_TABLE_INCREASE_SIZE_OF_CELL;
        this.name = name;
        this.iconForHeaderButton = iconForHeaderButton;
        this.hintText = hintText;
    }

    public String getName() {
        return name;
    }

    public Actor getIconForHeaderButton() {
        return iconForHeaderButton;
    }

    public String getHintText() {
        return hintText;
    }

    public int getWidth() {
        FastAsserts.assertMoreThan(width, 0, "Make sure item was added to TableHeaderData \nAND tableWidth was big enough!");
        return width;
    }

    public void calculateWidth(int tableWidth) {
        width = (int) (tableWidth * (widthPercent / 100f));
    }
}
