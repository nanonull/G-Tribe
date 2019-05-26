package conversion7.engine.custom2d;

import com.badlogic.gdx.graphics.Color;

public class ToggleButton {

    private boolean selected;
    protected ButtonWithActor buttonWithActor;

    public ToggleButton(){
        this(new ButtonWithActor());
    }

    public ToggleButton(ButtonWithActor buttonWithActor){
        this.buttonWithActor = buttonWithActor;
    }

    public ButtonWithActor getButtonWithActor() {
        return buttonWithActor;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setColor(Color color) {
        buttonWithActor.getBackground().setColor(color);
    }

    public void select() {
        selected = true;
        setColor(Color.YELLOW);
    }

    public void deselect() {
        selected = false;
        setColor(Color.LIGHT_GRAY);
    }

}
