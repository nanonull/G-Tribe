package conversion7.game.ai.global.tasks;

import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.actions.ActionEvaluation;
import conversion7.game.unit_classes.UnitClassConstants;

public class AnimalMigrationTask extends AbstractUnitTask<WorldSquad> {

    private static final Integer BASE_MIGR_TEMP = UnitClassConstants.
            roundTemperatureToClosestExistingTemperature(Unit.HEALTHY_TEMPERATURE_MIN);

    public AnimalMigrationTask(WorldSquad owner) {
        super(owner);
    }

    @Override
    public boolean isValid() {
        return ActionEvaluation.MOVE.evaluateOwner(owner);
    }

    @Override
    public void run() {
        Cell cell = owner.getLastCell();
        Cell upCell = cell.getCell(0, 1);
        Cell downCell = cell.getCell(0, -1);

        Integer migrationTemperature = owner.unit.classStandard.migrationTemperature;
        if (migrationTemperature == null) {
            migrationTemperature = BASE_MIGR_TEMP;
        }

        if (migrationTemperature == owner.cell.getTemperature()) {
            return;
        }


        int diffUp = migrationTemperature - cell.getCell(0, 1).getTemperature();
        int diffDown = migrationTemperature - cell.getCell(0, -1).getTemperature();

        Cell targetCell;
        if (diffUp < diffDown) {
            targetCell = upCell.getCell(0, Area.HEIGHT_IN_CELLS * 2);
        } else {
            targetCell = downCell.getCell(0, Area.HEIGHT_IN_CELLS * -2);
        }
        if (targetCell != owner.cell) {
            MoveTask.move(owner, targetCell, this, globalStrategy);
        }
    }

}
