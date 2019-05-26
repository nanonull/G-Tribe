package conversion7.game.stages.world.objects.totem;

import com.badlogic.gdx.graphics.Color;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.team.Team;

public class DefenceTotem extends AbstractTotem {

    public static final int DEF_AMOUNT = 1;
    public static final String BOOST_HINT = "Totem defence: dmg -" + DEF_AMOUNT;

    public DefenceTotem(Cell cell, Team team) {
        super(cell, team);
    }
    @Override
    public boolean givesExpOnHurt() {
        return true;
    }
    @Override
    protected Color getTotemColor() {
        return Color.BLUE;
    }

    @Override
    public String getShortHint() {
        return getClass().getSimpleName() + " " + team.getName();
    }

    @Override
    public String getHint() {
        return "Decreases any damage to allies:\n -1 damage to final damage\n" +
                getRadiusHint();
    }

    private String getRadiusHint() {
        return "Radius " + getRadius();
    }
}
