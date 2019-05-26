package conversion7.game.stages.world;

import com.badlogic.gdx.graphics.Color;

public class FloatingLabel {
    public final String txt;
    public final Color color;
    public int createdAtWorldStep;

    public FloatingLabel(String txt, Color color, int worldStep) {
        this.txt = txt;
        this.color = color;
        this.createdAtWorldStep = worldStep;
    }
}
