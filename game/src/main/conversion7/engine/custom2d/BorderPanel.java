package conversion7.engine.custom2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.engine.custom2d.table.Panel;
import conversion7.game.Assets;

public class BorderPanel extends Panel {

    private Cell actorCell;
    private float borderSize;
    private Color borderColor;

    public BorderPanel(float borderSize, Color borderColor) {
        this.borderSize = borderSize;
        this.borderColor = borderColor;
        add(getTexture()).size(borderSize);
        add(getTexture()).expandX().fillX().height(borderSize);
        add(getTexture()).size(borderSize);
        row();
        add(getTexture()).width(borderSize).growY();
        actorCell = add();
        add(getTexture()).width(borderSize).growY();
        row();
        add(getTexture()).size(borderSize);
        add(getTexture()).expandX().fillX().height(borderSize);
        add(getTexture()).size(borderSize);
    }

    private Actor getTexture() {
        Image image = new Image(Assets.pixel);
        image.setColor(borderColor);
        return image;
    }

    public Cell setActor(Actor actor){
        actorCell.setActor(actor);
        return actorCell;
    }
}
