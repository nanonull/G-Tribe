package conversion7.game.ai.global;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.game.ai.global.tasks.DummyCompTask;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.Landscape;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.composite.SandWorm;

public class SandWormAiEvalualor extends CompositeAiEvaluator<SandWorm> {

    @Override
    protected void evalEntityTasks(SandWorm entity) {
        AreaObject head = entity.getHead();

        if (entity.deactivationInProgress) {
            if (head.isAlive()) {
                entity.hideNextPart();
            }
            return;
        }

        if (entity.isPartActive(head)) {
            Array<Cell> nextCells = new Array<>();
            nextCells.add(head.cell.upCell);
            nextCells.add(head.cell.rightCell);
            nextCells.add(head.cell.leftCell);
            nextCells.add(head.cell.downCell);
            nextCells.shuffle();

            Cell enemSquadCell = null;
            Cell moveOnCell = null;
            for (Cell nextCell : nextCells) {
                if (nextCell.hasSquad()) {
                    enemSquadCell = nextCell;
                    moveOnCell = nextCell;
                    break;
                }
                if (moveOnCell == null && nextCell.canBeSeized()) {
                    moveOnCell = nextCell;
                }
            }

            if (enemSquadCell != null) {
                enemSquadCell.addFloatLabel("Worm shallow", Color.ORANGE);
                enemSquadCell.squad.power.setCurrentValue(0);
                enemSquadCell.squad.checkDefeated();
                entity.addPart();
            }

            if (moveOnCell != null && moveOnCell.getLandscape().getSand() > Landscape.DESERT_SAND) {
                entity.moveOn(moveOnCell);
            } else {
                entity.deactivate();
            }
        }
        entity.addAiTask(new DummyCompTask(entity));
    }

}
