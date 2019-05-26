package conversion7.game.stages.world.objects.totem;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.Unit;

public class ExperienceTotem extends AbstractTotem {

    public static final int BOOST = 1;
    public static final int EXP_PER_TURN = Unit.BASE_EXP_FOR_LEVEL / 4;
    Array<AbstractSquad> squadsAround = new Array<>();

    public ExperienceTotem(Cell cell) {
        super(cell, cell.getArea().world.getNeutralTotemTeam());
        cell.getArea().world.addImportantObj(this);
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
        return "Adds experience to each unit around every turn\n" +
                EXP_PER_TURN + " experience is spread between all units\n" +
                getRadiusHint();
    }

    private String getRadiusHint() {
        return "Radius " + getRadius();
    }

    @Override
    public void tick() {
        squadsAround.clear();
        for (Cell affectedCell : affectedCells) {
            if (affectedCell.hasSquad()) {
                squadsAround.add(affectedCell.getSquad());
            }
        }

        int expPerUnit = EXP_PER_TURN / squadsAround.size;
        for (AbstractSquad squad : squadsAround) {
            squad.updateExperience(expPerUnit, "Totem exp");
        }
    }
}
