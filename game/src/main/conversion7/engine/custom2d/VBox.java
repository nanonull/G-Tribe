package conversion7.engine.custom2d;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import conversion7.engine.custom2d.table.Panel;
import conversion7.game.ui.ClientUi;

public class VBox extends Panel {

    @Override
    public <T extends Actor> Cell<T> add(T actor) {
        Cell<T> add = super.add(actor);
        row();
        return add;
    }

    public void addSpaceLine() {
        add().height(ClientUi.SPACING);
    }

}
