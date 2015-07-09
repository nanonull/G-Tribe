package conversion7.game.stages.world.objects.actions;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.game.Assets;
import conversion7.game.interfaces.Cancelable;
import conversion7.game.interfaces.HintProvider;
import conversion7.game.interfaces.IconTextureProvider;
import conversion7.game.stages.AbstractStageObjectAction;
import conversion7.game.stages.world.objects.AreaObject;

public abstract class AbstractAreaObjectAction extends AbstractStageObjectAction
        implements IconTextureProvider, HintProvider, Cancelable {

    private int actionPositionPriority;

    /** Do not create new constructors child classes! They are used in reflection */
    public AbstractAreaObjectAction(AreaObject object) {
        super(object);
        actionPositionPriority = PropertiesLoader.getIntProperty(
                "AreaObjectAction.PositionPriority." + getClass().getSimpleName());
    }

    public int getActionPositionPriority() {
        return actionPositionPriority;
    }

    @Override
    public AreaObject getObject() {
        return (AreaObject) super.getObject();
    }


    public String getHint() {
        return getClass().getSimpleName();
    }

    @Override
    public void cancel() {
        //
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.homeIcon;
    }
}
