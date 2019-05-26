package conversion7.engine.custom2d.table;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.utils.UiUtils;

public class DefaultTable extends Table {

    public DefaultTable() {
        applyDefaults(this);
    }

    public static void applyDefaults(Table table) {
        table.defaults().left().top().space(0);
        table.left().top();
    }

    @Deprecated
    public DefaultTable applyDefaultPaddings() {
        this.defaults().pad(ClientUi.SPACING);
        return this;
    }

    /** Dont use if label has line breaks! */
    public Cell<Label> addLabel(Label label) {
        label.setAlignment(Align.center);
        return add(label).height(UiUtils.getFontHeight(label.getStyle().font))
                .center().left();
    }

    /** Dont use if label has line breaks! */
    public Cell<Label> addLabel(String text, Label.LabelStyle style) {
        return addLabel(new Label(text, style));
    }

    /**
     * To get root panel use Cell#getTable().<br>
     */
    public Cell<Panel> addProgressBar(ProgressBar progressBar, int barWidth) {
        Panel panel = new Panel();
        Cell<ProgressBar> actorCell = panel.add(progressBar).width(barWidth);
        return add(panel).center();
    }
}
