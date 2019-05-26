package conversion7.engine.artemis.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.Color;
import conversion7.engine.Gdxg;
import conversion7.engine.artemis.AnimationSystem;
import conversion7.engine.artemis.BattleAiSystem;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.view.AreaViewer;
import org.slf4j.Logger;

public class AreaViewerCellSelectionSystem extends BaseSystem {

    private static final Logger LOG = Utils.getLoggerForClass();
    AnimationSystem animationSystem;
    BattleAiSystem battleAiSystem;
    private Cell cellToSelect;

    @Override
    protected void processSystem() {
        if (cellToSelect == null || AnimationSystem.isLocking() || battleAiSystem.isProcessing()) {
            return;
        }
        selectCell(Gdxg.core.areaViewer, cellToSelect);
        cellToSelect = null;
    }

    private void selectCell(AreaViewer areaViewer, Cell newCell) {
        if (newCell == areaViewer.selectedCell && newCell.isRefreshedInView()) {
            return;
        }

        areaViewer.focusOnCellIfDoubleClick();
        areaViewer.deselectCell();

        areaViewer.selectedCell = newCell;
        areaViewer.showCellSelectedModel();
        areaViewer.selectedSquad = newCell.squad;
        LOG.info(" selectedSquad = " + areaViewer.selectedSquad);
        if (areaViewer.selectedSquad != null) {
            areaViewer.selectedSquad.getActionsController().forceTreeValidationFromThisNode();
            areaViewer.switchSquadModelHighlight(areaViewer.selectedSquad, true);
            if (areaViewer.selectedSquad.team.isHumanPlayer()) {
                if (areaViewer.selectedSquad.canEquipItems()) {
                    areaViewer.selectedSquad.checkAndNotifyIfCanEquipBetter();
                }
            }
            areaViewer.selectedSquad.batchFloatingStatusLines.flush(Color.WHITE);
        }
        if (areaViewer.selectedCell.hasCamp()) {
            areaViewer.highlightCamp(areaViewer.selectedCell.camp);
        }
        if (areaViewer.selectedCell.hasTotem()) {
            areaViewer.highlightTotem(areaViewer.selectedCell.getTotem());
        }
        Gdxg.core.artemis.getSystem(UnitSelectionUiSystem.class).scheduleReselectionUnitAction();
    }

    public void selectCell(Cell newCell) {
        cellToSelect = newCell;
    }
}
