package conversion7.game.ui.inputlisteners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.TribeRelationType;
import org.slf4j.Logger;

/**
 * Handles everything after Ui stage and before game stages
 */
public class GlobalInputListener extends InputListener {

    private static final Logger LOG = Utils.getLoggerForClass();

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("touchDown in " + this);
        }
        return false;
    }

    @Override
    public boolean keyUp(InputEvent event, int keycode) {
        if (LOG.isDebugEnabled()) LOG.debug(keycode + " keyUp in " + this);
        return handleKeyUpLocally(event, keycode);
        // lift down on other listeners (dynamic):
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    private boolean handleKeyUpLocally(InputEvent event, int keycode) {
        Cell mouseOverCell = Gdxg.core.areaViewer.mouseOverCell;

        switch (keycode) {

            case Input.Keys.U:
                Gdxg.graphic.getCameraController().switchCameraToFront();
                return true;

            case Input.Keys.I:
                Gdxg.graphic.getCameraController().switchCameraTo3d();
                return true;

            case Input.Keys.O:
                Gdxg.graphic.getCameraController().switchCameraToOrtho();
                return true;

            case Input.Keys.C:
                Gdxg.graphic.getCameraController().moveCameraToLookAtSelectedAreaObject();
                return true;
            case Input.Keys.K:
                if (mouseOverCell != null) {
                    if (mouseOverCell.hasSquad()) {
                        WorldSquad.killUnit(mouseOverCell.squad);
                    }
                }
                return true;
            case Input.Keys.B:
                Team playerTeam = Gdxg.core.world.lastActivePlayerTeam;
                if (mouseOverCell != null) {
                    if (mouseOverCell.hasSquad() && mouseOverCell.squad.team != playerTeam) {
                        playerTeam.addRelation(TribeRelationType.ATTACK, mouseOverCell.squad.team);
                    }
                }
                return true;
            default:
                return false;
        }
    }
}
