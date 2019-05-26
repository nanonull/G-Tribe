package conversion7.engine.custom2d;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import conversion7.engine.utils.Utils;

public class ImageWithLabel extends WidgetGroup {

    public final static int LABEL_SPACING = 2;

    public Image image;
    public Label label;
    private int align;

    public ImageWithLabel(Image image, Label label) {
        this(image, label, Align.left);
    }

    /** Align.left and Align.right are supported only */
    public ImageWithLabel(Image image, Label label, int align) {
        this.image = image;
        this.label = label;
        this.align = align;

        addActor(image);
        addActor(label);
    }

    @Override
    public void layout() {
        image.setWidth(getWidth());
        image.setHeight(getHeight());

        switch (align) {
            case Align.left:
                label.setPosition(LABEL_SPACING, LABEL_SPACING);
                break;
            case Align.right:
                label.setPosition(getWidth() - label.getGlyphLayout().width - LABEL_SPACING, LABEL_SPACING);
                break;
            default:
                Utils.error("not supported yet");
        }
    }
}
