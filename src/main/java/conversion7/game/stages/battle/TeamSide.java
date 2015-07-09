package conversion7.game.stages.battle;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.team.Team;

import java.util.Iterator;

import static org.fest.assertions.api.Assertions.assertThat;

public class TeamSide {

    private Battle battle;
    private Team team;
    private BattleSide battleSide;
    private BattleArmy mainArmy;
    Array<BattleArmy> aliveArmies = PoolManager.ARRAYS_POOL.obtain();
    Array<BattleArmy> deadArmies = PoolManager.ARRAYS_POOL.obtain();
    Array<TeamSide> enemies = PoolManager.ARRAYS_POOL.obtain();

    public TeamSide(Battle battle, Team team, BattleSide initialBattleBattleSide) {
        this.battle = battle;
        this.team = team;
        this.battleSide = initialBattleBattleSide;
    }

    public boolean containsAreaObject(AreaObject object) {
        for (BattleArmy battleArmy : aliveArmies) {
            if (battleArmy.getAreaObject().equals(object)) {
                return true;
            }
        }
        return false;
    }

    /** Add if not exist yet */
    public BattleArmy addArmy(AreaObject army) {
        if (!containsAreaObject(army)) {
            BattleArmy battleArmy = new BattleArmy(army);
            aliveArmies.add(battleArmy);
            return battleArmy;
        }
        return null;
    }

    public void addMainArmy(AreaObject mainArmy) {
        assertThat(this.mainArmy).isNull();
        this.mainArmy = addArmy(mainArmy);
        assertThat(this.mainArmy).isNotNull();
    }

    public void setupFigures() {
        for (BattleArmy army : aliveArmies) {
            army.setupFigures(battle, this);
        }
    }

    //

    public BattleArmy getMainArmy() {
        return mainArmy;
    }

    public Team getTeam() {
        return team;
    }

    public boolean isDefeated() {
        return aliveArmies.size == 0;
    }

    public int getUnitAmount() {
        int amount = 0;
        for (BattleArmy army : aliveArmies) {
            amount += army.getAreaObject().getUnits().size;
        }
        return amount;
    }

    public void addEnemySide(TeamSide side) {
        assertThat(enemies).doesNotContain(side);
        enemies.add(side);
    }

    public void setBattleSide(BattleSide battleSide) {
        this.battleSide = battleSide;
    }

    public BattleSide getBattleSide() {
        return battleSide;
    }

    public BattleFigure getRandomEnemyFigure() {
        Array<BattleFigure> array = PoolManager.ARRAYS_POOL.obtain();
        for (TeamSide enemy : enemies) {
            Array<BattleFigure> figures = enemy.getAliveFigures();
            array.addAll(figures);
            PoolManager.ARRAYS_POOL.free(figures);
        }

        BattleFigure battleFigure = array.get(Utils.RANDOM.nextInt(array.size));
        PoolManager.ARRAYS_POOL.free(array);
        return battleFigure;
    }

    public Array<BattleFigure> getAliveFigures() {
        Array<BattleFigure> array = PoolManager.ARRAYS_POOL.obtain();
        for (BattleArmy army : aliveArmies) {
            array.addAll(army.getAliveFigures());
        }
        return array;
    }

    public void updateArmies() {
        Iterator<BattleArmy> iterator = aliveArmies.iterator();
        while (iterator.hasNext()) {
            BattleArmy army = iterator.next();
            army.updateFigures();
            if (army.isDefeated()) {
                deadArmies.add(army);
                iterator.remove();
            }
        }
    }
}
