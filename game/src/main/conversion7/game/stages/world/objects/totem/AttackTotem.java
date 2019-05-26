package conversion7.game.stages.world.objects.totem;

import com.badlogic.gdx.graphics.Color;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.team.Team;

public class AttackTotem extends AbstractTotem {

    public static final int BOOST = 1;
    public static final String BOOST_HINT = "Totem dmg +" + BOOST;

    public AttackTotem(Cell cell, Team team) {
        super(cell, team);
    }

    @Override
    protected Color getTotemColor() {
        return Color.SCARLET;
    }

    @Override
    public String getShortHint() {
        return getClass().getSimpleName() + " " + team.getName();
    }


    private String getRadiusHint() {
        return "Radius " + getRadius();
    }
}
