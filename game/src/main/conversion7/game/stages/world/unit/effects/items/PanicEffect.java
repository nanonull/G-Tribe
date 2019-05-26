package conversion7.game.stages.world.unit.effects.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class PanicEffect extends AbstractUnitEffect {

    private int durationSteps = 2;

    public PanicEffect() {
        super(PanicEffect.class.getSimpleName(), Type.NEGATIVE);
        setTickLogicEvery(durationSteps);
    }

    @Override
    public String getHint() {
        return super.getHint() + " " + (tickCounter + 1) + "/" + durationSteps
                + "\n \nUnit will randomly move when his turn starts";
    }

    @Override
    public String getShortIconName() {
        return "Panic";
    }

    public static void tryApply(Unit unit) {
        Cell cellMsg = unit.squad.cell;
        if (unit.squad.canMove()) {
            Array<Cell> cells = unit.squad.getLastCell().getCellsAround();
            cells.shuffle();
            for (Cell cell : cells) {
                if (cell.canBeSeized() && unit.squad.canMove()) {
                    unit.squad.moveOn(cell);
                    cellMsg = cell;
                    break;
                }
            }
        }

        cellMsg.addFloatLabel("Panic", Color.ORANGE);
    }

    @Override
    protected void tickLogic() {
        complete();
    }

}
