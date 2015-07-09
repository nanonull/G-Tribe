package conversion7.game.stages.battle;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.utils.collections.IterationRegistrators;

import java.util.Iterator;

public class BattleArmy {

    private AreaObject areaObject;
    private Array<BattleFigure> aliveFigures = PoolManager.ARRAYS_POOL.obtain();
    private Array<BattleFigure> deadFigures = PoolManager.ARRAYS_POOL.obtain();

    public BattleArmy(AreaObject areaObject) {
        this.areaObject = areaObject;
    }

    public AreaObject getAreaObject() {
        return areaObject;
    }

    public boolean isDefeated() {
        return aliveFigures.size == 0;
    }

    public void defeatAreaObject(String message) {
        areaObject.defeat();
        getAreaObject().addSnapshotLog("defeated in battle: " + message);
    }

    public void setupFigures(Battle battle, TeamSide teamSide) {
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
        for (int i = 0; i < getAreaObject().getUnits().size; i++) {
            Unit unit = getAreaObject().getUnits().get(i);
            BattleFigure figure = new BattleFigure(battle, unit, teamSide);
            battle.battleFigures.add(figure);
            aliveFigures.add(figure);
        }
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();
    }

    public Array<BattleFigure> getAliveFigures() {
        return aliveFigures;
    }

    public void updateFigures() {
        Iterator<BattleFigure> iterator = aliveFigures.iterator();
        while (iterator.hasNext()) {
            BattleFigure battleFigure = iterator.next();
            if (battleFigure.isKilled()) {
                deadFigures.add(battleFigure);
                iterator.remove();
            }
        }
    }
}
