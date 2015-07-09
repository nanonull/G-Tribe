package conversion7.game.stages.battle;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public class AutoBattle {

    private static final Logger LOG = Utils.getLoggerForClass();

    private Battle battle;

    public AutoBattle(Battle battle) {
        this.battle = battle;
    }

    public void start() {
        while (true) {
            Array<BattleFigure> aliveFigures = battle.getAliveFigures();
            FigureSpeedComparator.sort(aliveFigures);

            // each figure hits random enemy
            // TODO with more chance to hit more valuable enemy
            for (BattleFigure battleFigure : aliveFigures) {
                if (!battleFigure.isKilled()) {
                    BattleFigure enemyFigure = battleFigure.getTeamSide().getRandomEnemyFigure();
                    battleFigure.params.hit(enemyFigure.params);
                }
            }
            PoolManager.ARRAYS_POOL.free(aliveFigures);

            // update and get winner
            battle.updateTeamSides();
            if (battle.hasWinner()) {
                break;
            }
        }

        battle.finish();
    }

}
