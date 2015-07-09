package conversion7.game.stages.world.ai.tasks.single;

import conversion7.game.stages.world.landscape.Cell;

public interface SearchCellTask {

    public boolean isCellMatchesTargetCondition(Cell theBestCell);

    public void continueSearchingTargetCell(Cell theBestCell);
}
