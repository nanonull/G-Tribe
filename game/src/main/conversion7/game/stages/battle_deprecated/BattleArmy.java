package conversion7.game.stages.battle_deprecated;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.unit.Unit;

public class BattleArmy {

    private AbstractSquad squad;
    private Array<BattleFigure> battleFigures = PoolManager.ARRAYS_POOL.obtain();
    private boolean defeated;

    public BattleArmy(AbstractSquad squad) {
        this.squad = squad;
    }

    public boolean isDefeated() {
        return defeated;
    }

    public Array<BattleFigure> getBattleFigures() {
        return battleFigures;
    }

    public AbstractSquad getSquad() {
        return squad;
    }

    public void defeatAreaObject(String message) {
        getSquad().addSnapshotLog("defeated in battle: " + message);
        WorldSquad.killUnit(squad);
    }

    public void setupFigures(Battle battle, TeamSide teamSide) {
        for (int i = 0; i < 1; i++) {
            Unit unit = getSquad().unit;
            BattleFigure figure = new BattleFigure(battle, unit, teamSide);
            battle.battleFigures.add(figure);
            battleFigures.add(figure);
        }
    }

    public void updateFigures() {
        defeated = true;
        for (BattleFigure figure : battleFigures) {
            if (!figure.isDead()) {
                defeated = false;
                break;
            }
        }
    }
}
