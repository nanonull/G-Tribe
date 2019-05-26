package conversion7.engine.custom2d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class ActorDrawable extends BaseDrawable {

    private Actor actor;

    public ActorDrawable(Actor actor){
        this.actor = actor;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        actor.draw(batch, actor.getColor().a);
    }
}
