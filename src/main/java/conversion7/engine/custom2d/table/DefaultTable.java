package conversion7.engine.custom2d.table;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import conversion7.game.ui.ClientUi;

public class DefaultTable extends Table {

    public DefaultTable() {
        applyDefaults(this);
    }

    public DefaultTable applyDefaultPaddings() {
        defaults().pad(ClientUi.SPACING);
        return this;
    }

    public static void applyDefaults(Table table) {
        table.defaults().left().top();
        table.left().top();
    }
}
