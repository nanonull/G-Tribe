package conversion7.game.stages.world.objects.totem;

import com.badlogic.gdx.graphics.Color;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.team.Team;

public class ViewDistanceTotem extends AbstractTotem {

    public static final int BOOST = 1;

    public ViewDistanceTotem(Cell cell, Team team) {
        super(cell, team);
        refreshViewForUnitsInRadius();
    }

    @Override
    protected Color getTotemColor() {
        return Color.CYAN;
    }

    @Override
    public String getShortHint() {
        return getClass().getSimpleName() + " " + team.getName();
    }

    @Override
    public String getHint() {
        return "Adds +" + BOOST + " to view distance of allies\n" +
                getRadiusHint();
    }

    private String getRadiusHint() {
        return "Radius " + getRadius();
    }

    private void refreshViewForUnitsInRadius() {
        for (Cell affectedCell : getAffectedCells()) {
            if (affectedCell.hasSquad() && affectedCell.squad.team == team) {
                affectedCell.squad.refreshVisibleCells();
                affectedCell.squad.refreshStealth();
                affectedCell.squad.validateView();
            }
        }
    }


    @Override
    public void removeFromWorld() {
        // TODO ViewDistanceTotem: recalculate and refresh view for affected units
        super.removeFromWorld();
    }
}
