package conversion7.game.dialogs;

import conversion7.engine.CameraController;
import conversion7.engine.Gdxg;
import conversion7.engine.dialog.AbstractDialog;
import conversion7.game.stages.world.landscape.Cell;

abstract class AbstractGdxgDialog extends AbstractDialog {
    private Cell focusOnCell;

    public void start() {
        AbstractDialog.start(this, Gdxg.clientUi.getDialogWindow());
    }

    public void focusOn(Cell onCell) {
        this.focusOnCell = onCell;
        setPictureViewActive(false);
        CameraController.scheduleCameraFocusOn(1, onCell);
    }

    @Override
    public void complete() {
        super.complete();
        if (focusOnCell != null) {
            focusOn(focusOnCell);
        }
    }
}
