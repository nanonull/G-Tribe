package conversion7.engine.artemis.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.actions.items.MeleeAttackAction;
import conversion7.game.stages.world.objects.actions.items.MoveAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.view.AreaViewer;
import conversion7.game.ui.UiLogger;
import org.slf4j.Logger;

/**
 * system which will scan viewer selection changes
 * and will make appropriate changes
 * (to split ui-refresh from game logic)
 */
public class UnitSelectionUiSystem extends BaseSystem {

    private static final Logger LOG = Utils.getLoggerForClass();

    AreaViewer areaViewer;

    RefreshCellValidationDto selectedValidationDto = new RefreshCellValidationDto();
    RefreshCellValidationDto newSelectedValidationDto = new RefreshCellValidationDto();
    RefreshCellValidationDto mouseOverValidationDto = new RefreshCellValidationDto();
    private Array<AbstractWorldTargetableAction> actionSelectionScheduled = new Array<>();
    public Array<AbstractWorldTargetableAction> newActionSelectionScheduled = new Array<>();
    public boolean actionReselection = false;

    @Override
    protected void processSystem() {
        areaViewer = Gdxg.core.areaViewer;
        if (areaViewer == null) {
            return;
        }

        refreshSelectedCellUi();
        refreshMouseOverCellUi();
    }


    private void refreshSelectedCellUi() {
        fillNewSelectedValidationDto();

        if (newSelectedValidationDto.cell != selectedValidationDto.cell
                || newSelectedValidationDto.squad != selectedValidationDto.squad
                || newSelectedValidationDto.squadValidatedOnFrame != selectedValidationDto.squadValidatedOnFrame) {
            selectedValidationDto.cell = newSelectedValidationDto.cell;
            selectedValidationDto.squad = newSelectedValidationDto.squad;
            selectedValidationDto.squadValidatedOnFrame = newSelectedValidationDto.squadValidatedOnFrame;

            areaViewer.clearSelectedCellUi();
            actionReselection = true;
            Gdxg.clientUi.getWorldMainWindow().refresh();
            refreshUnitUiInWorld();
        }

        if (!newActionSelectionScheduled.equals(actionSelectionScheduled) || actionReselection) {
            actionReselection = false;
            actionSelectionScheduled.clear();
            actionSelectionScheduled.addAll(newActionSelectionScheduled);

            areaViewer.clearCellsSelectionSelection();
            areaViewer.refreshUnitActionSelectionInWorld(actionSelectionScheduled);
        }
    }

    private void fillNewSelectedValidationDto() {
        newSelectedValidationDto.cell = areaViewer.selectedCell;

        if (newSelectedValidationDto.cell == null) {
            newSelectedValidationDto.squad = null;
        } else {
            newSelectedValidationDto.squad = newSelectedValidationDto.cell.getSquad();
        }
        if (newSelectedValidationDto.squad == null) {
            newSelectedValidationDto.squadValidatedOnFrame = 0;
        } else {
            newSelectedValidationDto.squadValidatedOnFrame = newSelectedValidationDto.squad.validatedOnFrame;
        }
    }

    private void refreshUnitUiInWorld() {
        if (newSelectedValidationDto.cell != null && newSelectedValidationDto.cell.hasSquad()) {
            AbstractSquad seizedBySquad = newSelectedValidationDto.cell.squad;

            Team activeTeam = Gdxg.core.world.activeTeam;
            LOG.info(" newSelected = " + seizedBySquad);
            if (seizedBySquad.getTeam().equals(activeTeam) || GdxgConstants.DEVELOPER_MODE) {
                if (!seizedBySquad.getTeam().equals(activeTeam)) {
                    UiLogger.addInfoLabel("[DEV] selected object of another team");
                }

                seizedBySquad.getTeam().setLastSelectedObject(seizedBySquad);
                areaViewer.showStealthIcons();
            }
        }
    }

    private void refreshMouseOverCellUi() {
        Cell mouseOverCell = areaViewer.mouseOverCell;
        AbstractSquad mouseOverSquad;
        long validatedOnFrame;

        if (mouseOverCell == null) {
            mouseOverSquad = null;
        } else {
            mouseOverSquad = mouseOverCell.getSquad();
        }
        if (mouseOverSquad == null) {
            validatedOnFrame = 0;
        } else {
            validatedOnFrame = mouseOverSquad.validatedOnFrame;
        }

        if (mouseOverCell != mouseOverValidationDto.cell
                || mouseOverSquad != mouseOverValidationDto.squad
                || validatedOnFrame != mouseOverValidationDto.squadValidatedOnFrame) {
            mouseOverValidationDto.cell = mouseOverCell;
            mouseOverValidationDto.squad = mouseOverSquad;
            mouseOverValidationDto.squadValidatedOnFrame = validatedOnFrame;
            Gdxg.clientUi.getWorldMainWindow().worldUnitControlPanel.unitsComparisonPanel.mouseOverUnitTable.load(mouseOverSquad);
        }
    }

    public void scheduleReselectionUnitAction() {
        if (Gdxg.core.areaViewer.selectedSquad == null) {
            newActionSelectionScheduled.clear();
        } else {
            newActionSelectionScheduled.clear();
            actionSelectionScheduled.clear();
            scheduleNewActionSelection(MoveAction.class);
            scheduleNewActionSelection(MeleeAttackAction.class);
        }
    }

    public void scheduleNewActionSelection(Class<? extends AbstractSquadAction> aClass) {
        AbstractAreaObjectAction action = Gdxg.core.areaViewer.selectedSquad.getActions().get(aClass);
        if (action != null) {
            newActionSelectionScheduled.add((AbstractWorldTargetableAction) action);
        }
    }

}
