package conversion7.engine.custom2d;


import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import conversion7.engine.custom2d.table.DefaultTable;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import org.slf4j.Logger;

public class CustomWindow extends Window {

    private static final Logger LOG = Utils.getLoggerForClass();
    protected final CustomWindow thiz;

    protected Stage linkedStage;
    private Skin skin;

    public CustomWindow(Stage linkedStage, String title, Skin skin) {
        super(title, skin);
        this.skin = skin;
        this.thiz = this;
        this.linkedStage = linkedStage;
        DefaultTable.applyDefaults(this);

        // handle if not handled by children to avoid mouse events behind window
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (LOG.isDebugEnabled()) LOG.debug("catch to avoid click through window");
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return true;
            }
        });

    }

    protected void addCloseButton() {
        TextButton closeButton = new TextButton(" X ", Assets.uiSkin);
        add(closeButton).right().expandX().height(32);
        closeButton.center();
        closeButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (LOG.isDebugEnabled()) LOG.debug("close!");
                hide();
            }
        });

        row();
    }

    public Skin getSkin() {
        return skin;
    }

    public void show() {
        linkedStage.addActor(this);
    }

    public boolean isShown() {
        return hasParent();
    }

    public void hide() {
        this.remove();
    }

    public Stage getLinkedStage() {
        return linkedStage;
    }
}
