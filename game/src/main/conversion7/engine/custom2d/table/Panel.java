package conversion7.engine.custom2d.table;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.game.Assets;

public class Panel extends DefaultTable {

    protected Group prevParent;

    @Override
    protected void setParent(Group parent) {
        super.setParent(parent);
        if (parent != null) {
            prevParent = parent;
        }
    }

    public void hide() {
        setVisible(false);
    }

    public void show() {
        checkIfCanBeReAddedToParent();
        setVisible(true);
    }

    /** Bad for table layout */
    public void closeWithRemoval() {
        if (hasParent()) {
            prevParent = getParent();
            remove();
        } else {
            throw new GdxRuntimeException("actor cant be hidden if was not shown: " + this);
        }
    }

    protected void checkIfCanBeReAddedToParent() {
        if (getParent() == null && prevParent != null) {
            prevParent.addActor(this);
        }
    }

    protected void addSmallHideButton() {
        TextButton closeButton = new TextButton("-", Assets.uiSkin);
        closeButton.top().left();
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        add(closeButton).right().expandX().height(16).width(16);
    }

    protected void addSmallCloseButton() {
        TextButton closeButton = new TextButton("-", Assets.uiSkin);
        closeButton.top().left();
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                remove();
            }
        });
        add(closeButton).right().expandX().height(16).width(16);
    }

    public void showWithRemoval() {
        if (!hasParent()) {
            prevParent.addActor(this);
        }
    }

}
