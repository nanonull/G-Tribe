package conversion7.game.stages.world.objects.totem;

import com.badlogic.gdx.graphics.Color;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.team.Team;

public class HealingTotem extends AbstractTotem {

    public static final int BOOST = 1;

    public HealingTotem(Cell cell, Team team) {
        super(cell, team);
    }

    @Override
    protected Color getTotemColor() {
        return Color.GREEN;
    }

    @Override
    public String getShortHint() {
        return getClass().getSimpleName() + " " + team.getName();
    }

    @Override
    public String getHint() {
        return "Adds +" + BOOST + " to any ally healing effects or actions\n" +
                getRadiusHint();
    }

    private String getRadiusHint() {
        return "Radius " + getRadius();
    }
}
