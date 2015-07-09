package conversion7.game.stages.world.objects.effects;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.game.Assets;
import conversion7.game.interfaces.HintProvider;
import conversion7.game.interfaces.ImageProvider;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.ui.HintForm;

import java.util.Iterator;

public abstract class AbstractObjectEffect implements ImageProvider, HintProvider {

    private static final int DEFAULT_DURATION = 10;

    private AreaObject areaObject;
    private int duration;
    protected Image image;

    public AbstractObjectEffect(AreaObject areaObject) {
        this.areaObject = areaObject;
        prolong();
    }

    public void prolong() {
        duration += DEFAULT_DURATION;
    }

    public void tick(Iterator<AbstractObjectEffect> effectsIterator) {
        duration--;
        if (duration == 0) {
            effectsIterator.remove();
        }
    }

    @Override
    public String getHint() {
        return getClass().getSimpleName();
    }

    @Override
    public Image getImage() {
        if (image == null) {
            image = new Image(Assets.homeIcon);
            HintForm.assignHintTo(image, getHint());
        }
        return image;
    }
}
