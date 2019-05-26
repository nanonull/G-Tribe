package conversion7.engine.custom2d;

import com.badlogic.gdx.graphics.Color;

public abstract class ToggleActor {

    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public abstract void setColor(Color color);

    public void select() {
        selected = true;
        setColor(Color.YELLOW);
    }

    public void deselect() {
        selected = false;
        setColor(Color.LIGHT_GRAY);
    }

}
