package conversion7.game.stages.battle_deprecated;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;

import static org.fest.assertions.api.Assertions.assertThat;

public class TeamSide {

    private Battle battle;
    private Team team;
    private BattleSide battleSide;
    private BattleArmy mainArmy;

    private Array<BattleArmy> armies = PoolManager.ARRAYS_POOL.obtain();
    Array<TeamSide> enemies = PoolManager.ARRAYS_POOL.obtain();
    private boolean defeated;

    public TeamSide(Battle battle, Team team, BattleSide initialBattleBattleSide) {
        this.battle = battle;
        this.team = team;
        this.battleSide = initialBattleBattleSide;
    }

    public BattleArmy getMainArmy() {
        return mainArmy;
    }

    public Team getTeam() {
        return team;
    }

    public boolean isDefeated() {
        return defeated;
    }

    public int getUnitAmount() {
        return 1;
    }

    //

    public BattleSide getBattleSide() {
        return battleSide;
    }

    public void setBattleSide(BattleSide battleSide) {
        this.battleSide = battleSide;
    }

    public BattleFigure getRandomEnemyFigure() {
        Array<BattleFigure> array = PoolManager.ARRAYS_POOL.obtain();
        for (TeamSide enemy : enemies) {
            Array<BattleFigure> figures = enemy.getAliveFigures();
            array.addAll(figures);
            PoolManager.ARRAYS_POOL.free(figures);
        }

        BattleFigure battleFigure = array.get(MathUtils.RANDOM.nextInt(array.size));
        PoolManager.ARRAYS_POOL.free(array);
        return battleFigure;
    }

    public Array<BattleFigure> getAliveFigures() {
        Array<BattleFigure> array = PoolManager.ARRAYS_POOL.obtain();
        for (BattleArmy army : armies) {
            array.addAll(army.getBattleFigures());
        }
        return array;
    }

    public Array<BattleArmy> getArmies() {
        return armies;
    }

    public void addMainArmy(AbstractSquad mainArmy) {
        assertThat(this.mainArmy).isNull();
        this.mainArmy = addArmy(mainArmy);
        assertThat(this.mainArmy).isNotNull();
    }

    /** Add if not exist yet */
    public BattleArmy addArmy(AbstractSquad army) {
        if (!containsAreaObject(army)) {
            BattleArmy battleArmy = new BattleArmy(army);
            armies.add(battleArmy);
            return battleArmy;
        }
        return null;
    }

    public boolean containsAreaObject(AreaObject object) {
        for (BattleArmy battleArmy : armies) {
            if (battleArmy.getSquad().equals(object)) {
                return true;
            }
        }
        return false;
    }

    public void setupFigures() {
        for (BattleArmy army : armies) {
            army.setupFigures(battle, this);
        }
    }

    public void addEnemySide(TeamSide side) {
        assertThat(enemies).doesNotContain(side);
        enemies.add(side);
    }

    public void updateArmies() {
        defeated = true;
        for (BattleArmy aliveArmy : armies) {
            aliveArmy.updateFigures();
            if (!aliveArmy.isDefeated()) {
                defeated = false;
                break;
            }
        }
    }
}
