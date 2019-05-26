package conversion7.game.ui.world;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.Landscape;
import conversion7.game.stages.world.objects.AnimalSpawn;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.ui.ClientUi;

public class DebugCellHint extends Window {

    Stage stage;

    private float width = 0;
    private boolean enabled = false;

    public DebugCellHint(Stage stage) {
        super("Hint", Assets.uiSkin);
        this.stage = stage;

        setKeepWithinStage(true);
        setPosition(GdxgConstants.SCREEN_WIDTH_IN_PX,
                GdxgConstants.SCREEN_HEIGHT_IN_PX);

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

    public void showOn(Cell cell) {
        if (!enabled) {
            return;
        }

        if (cell != null) {
            width = 0;
            clearChildren(); // there is some unexpected object at first start

            getTitleLabel().setText("cell: " + cell.x + "," + cell.y);

            describe(cell);
            row().height(ClientUi.LINE_HEIGHT);
            add();
            describe(cell.getArea());
            row().height(ClientUi.LINE_HEIGHT);
            add();
            if (cell.hasSquad()) {
                describe(cell.getSquad());
            }

            float rowHeight = getChildren().get(0).getHeight();

//            setHeight(ClientUi.WINDOW_HEADER_HEIGHT + rowHeight * getCells().size);
//            setWidth(width + 10);

            invalidate();
            pack();
            stage.addActor(this);

        } else {
            hide();
        }
    }

    private void describe(Area area) {
        String areaHint = "AREA params:";
        AnimalSpawn animalSpawn = area.getLastSpawn();
        if (animalSpawn != null) {
            areaHint += "\nanimal spawn chance: " + animalSpawn.getSpawnChance();
        }

        Label label = new Label(areaHint, Assets.labelStyle14yellow);
        label.setAlignment(Align.left);
        row();
        add(label).left();
    }

    private void describe(Cell cell) {
        Label label;
        for (String hintRow : cell.getHint().split(GdxgConstants.HINT_SPLITTER)) {
            row();
            label = new Label(hintRow, Assets.labelStyle14yellow);
            label.setAlignment(Align.left);
            this.add(label).left();
        }

        if (cell.getLandscape() != null) {
            describe(cell.getLandscape());
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
        }
    }

    public void hide() {
        this.remove();
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
