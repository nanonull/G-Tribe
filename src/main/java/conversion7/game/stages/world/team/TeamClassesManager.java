package conversion7.game.stages.world.team;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.unit.Unit;

public class TeamClassesManager {

    private ObjectSet<Unit> allTeamUnits = PoolManager.OBJECT_SET_POOL.obtain();
    private Array<UnitClassTeamInfo> unitClassesInfo = PoolManager.ARRAYS_POOL.obtain();
    private Team team;

    public TeamClassesManager(Team team) {
        this.team = team;
    }

    public ObjectSet<Unit> getAllTeamUnits() {
        return allTeamUnits;
    }

    public Team getTeam() {
        return team;
    }

    public UnitClassTeamInfo getTeamInfo(Class<? extends Unit> unitClass) {
        for (UnitClassTeamInfo unitClassTeamInfo : unitClassesInfo) {
            if (unitClassTeamInfo.getUnitClass().equals(unitClass)) {
                return unitClassTeamInfo;
            }
        }
        return null;
    }

    public void addUnitIfNewcomerInTeam(Unit unit) {
        if (allTeamUnits.add(unit)) {
            UnitClassTeamInfo teamInfo = getTeamInfo(unit.getClass());
            if (teamInfo == null) {
                UnitClassTeamInfo unitClassTeamInfo = new UnitClassTeamInfo(unit.getClass(), 1);
                unitClassesInfo.add(unitClassTeamInfo);
            } else {
                teamInfo.updateClassAmount(+1);
            }
        }
    }

    public void removeUnit(Unit unit) {
        Class<? extends Unit> unitClass = unit.getClass();
        if (allTeamUnits.remove(unit)) {
            UnitClassTeamInfo teamInfo = getTeamInfo(unitClass);
            if (teamInfo == null) {
                Utils.error("teamInfo is absent for " + unitClass);
            } else {
                teamInfo.updateClassAmount(-1);
            }
        } else {
            Utils.error("Unit is absent!");
        }
    }

    public Array<UnitClassTeamInfo> getUnitClassesInfo() {
        return unitClassesInfo;
    }

}
