package conversion7.game.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.game.Assets;
import conversion7.game.ui.ClientUi;

public abstract class AbstractInfoDialog extends AnimatedWindow {

    protected Label description;

    public AbstractInfoDialog(Stage stage, String title, Skin skin, int direction) {
        super(stage, title, skin, direction);
        defaults().pad(ClientUi.SPACING);

        description = new Label("input description", Assets.labelStyle14_lightGreen);
        add(description);

    }

    public void setDescription(String description) {
        this.description.setText(description);
    }

    @Override
    public void show() {
        pack();
        setPosition((int) (Gdx.graphics.getWidth() / 2 - getWidth() / 2),
                (int) (Gdx.graphics.getHeight() / 2 - getHeight() / 2));
        updateAnimationBounds();
        super.show();
    }

    @Override
    public void act(float delta) {
        toFront();
    }
}
