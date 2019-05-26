package conversion7.game.ui.world.main_panel;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.HBox;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.AreaObjectDetailsButton;
import conversion7.game.stages.world.objects.AreaObjectDetailsDescriptor;

public class CellDetailsButtonsPanel extends HBox {
    public void load(Cell cell) {
        clear();
        addButton("Cell", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdxg.clientUi.getCellDetailsRootPanel().load(cell);
            }
        });
        for (AreaObject object : cell.getObjectsOnCell()) {
            if (object instanceof AreaObjectDetailsButton) {
                AreaObjectDetailsButton obj = (AreaObjectDetailsButton) object;
                addButton(obj.getDetailsButtonLabel(), obj.getDetailsClickListener());
            } else if (object instanceof AreaObjectDetailsDescriptor) {
                AreaObjectDetailsDescriptor obj = (AreaObjectDetailsDescriptor) object;
                addButton(obj.getDetailsButtonLabel(), new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Gdxg.clientUi.getCellDetailsRootPanel().load(obj);
                    }
                });
            }
        }

        show();
    }

    private void addButton(String label, ClickListener clickListener) {
        TextButton detailsButton = new TextButton(label, Assets.uiSkin);
        add(detailsButton);
        detailsButton.addListener(clickListener);

    }


}
