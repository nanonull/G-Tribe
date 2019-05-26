package conversion7.engine.custom2d;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import conversion7.game.Assets;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.list_menu.MenuItem;

import java.util.List;

public class SelectBoxList extends VBox {
    public List<MenuItem> items;
    private Skin skin;

    public SelectBoxList(Skin skin) {
        this.skin = skin;
        setBackground(new TextureRegionColoredDrawable(Color.GREEN, Assets.pixel));
    }

    public void setItems(List<MenuItem> items) {
        clear();
        this.items = items;
        for (MenuItem item : this.items) {
            TextButton button = new TextButton(item.text, skin);
            add(button).height(ClientUi.EXPANDED_BUTTON_HEIGHT).fill().expand();
            button.addListener(new ClickListener(Input.Buttons.LEFT) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (item.hideMenuOnClick) {
                        hide();
                    }
                    item.runnable.run();
                }
            });
        }
        pack();
    }
}
