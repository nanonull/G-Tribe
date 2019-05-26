package conversion7.engine.artemis.ui

import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class RefreshCellValidationDto {
    public Cell cell
    public AbstractSquad squad
    public long squadValidatedOnFrame
}
