package conversion7.game.ui.world;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.Landscape;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.ui.ClientUi;

public class DebugCellHint extends Window {

    Stage stage;

    private float width = 0;
    private boolean enabled = false;

    public DebugCellHint(Stage stage) {
        super("Hint", Assets.uiSkin);
        this.stage = stage;

        addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
    }


    public void hide() {
        this.remove();
    }


    public void showOn(Cell cell) {
        if (!enabled) {
            return;
        }

        if (cell != null) {
            width = 0;
            clearChildren(); // there is some unexpected object at first start

            getTitleLabel().setText("cell: " + cell.x + "," + cell.y);

            describe(cell);

            if (cell.isSeized()) {
                describe(cell.getSeizedBy());
            }

            float rowHeight = getChildren().get(0).getHeight();

            setHeight(ClientUi.WINDOW_HEADER_HEIGHT + rowHeight * getCells().size);
            setWidth(width + 10);

            setPosition(GdxgConstants.SCREEN_WIDTH_IN_PX - getWidth(),
                    GdxgConstants.SCREEN_HEIGHT_IN_PX - getHeight());

            stage.addActor(this);

        } else {
            hide();
        }
    }


    private void describe(Cell cell) {
        Label label;
        for (String hintRow : cell.getHint().split(GdxgConstants.HINT_SPLITTER)) {
            row();
            label = new Label(hintRow, Assets.labelStyle14yellow);
            label.setAlignment(Align.left);
            this.add(label).left();
            if (label.getWidth() > width) width = label.getWidth();
        }

        if (cell.getLandscape() != null) {
            describe(cell.getLandscape());
        }
    }


    private void describe(Landscape landscape) {
        Label label;

        row();
        label = new Label("  ---   ", Assets.labelStyle14yellow);
        this.add(label).left();

        for (String hintRow : landscape.getHint().split(GdxgConstants.HINT_SPLITTER)) {
            row();
            label = new Label(hintRow, Assets.labelStyle14yellow);
            label.setAlignment(Align.left);
            this.add(label).left();
            if (label.getWidth() > width) width = label.getWidth();
        }
    }


    private void describe(AreaObject object) {
        Label label;

        row();
        label = new Label("  ---   ", Assets.labelStyle14yellow);
        this.add(label).left();

        for (String hintRow : object.getHint().split(GdxgConstants.HINT_SPLITTER)) {
            row().row();
            label = new Label(hintRow, Assets.labelStyle14yellow);
            label.setAlignment(Align.left);
            this.add(label).left();
            if (label.getWidth() > width) width = label.getWidth();
        }
    }


    public void turnEnabled() {
        if (enabled) {
            enabled = false;
            remove();
        } else {
            enabled = true;
        }
    }
}
