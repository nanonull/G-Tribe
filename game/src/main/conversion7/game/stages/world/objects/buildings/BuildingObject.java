package conversion7.game.stages.world.objects.buildings;

import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.BallistaObject;
import conversion7.game.stages.world.objects.ScorpionObject;
import conversion7.game.stages.world.team.Team;

abstract public class BuildingObject extends AreaObject {


    public BuildingObject(Cell cell, Team team) {
        super(cell, team);
    }

    public static boolean canHaveBallistaBuiltOn(Cell cell) {
        return !cell.containsObject(ScorpionObject.class) && !cell.containsObject(BallistaObject.class);
    }
    @Override
    public boolean givesExpOnHurt() {
        return true;
    }
    @Override
    public boolean givesCornerDefenceBonus() {
        return true;
    }

    @Override
    public void validateView() {
        getLastCell().setRefreshedInView(false);
        super.validateView();
    }
}
