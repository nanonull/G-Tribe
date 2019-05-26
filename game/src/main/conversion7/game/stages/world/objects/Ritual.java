package conversion7.game.stages.world.objects;

import conversion7.game.dialogs.RitualDialog;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;

@Deprecated
public class Ritual extends AreaObject {
    @Override
    public boolean givesExpOnHurt() {
        return false;
    }
    public static final int STEPS = 4;
    public static final int EVOLUTION_EXP = Team.EVOLUTION_EXP_PER_EVOLUTION_POINT / 3;
    private int progress = 0;
    private final AbstractSquad ritualFor;

    public Ritual(Cell cell, Team team) {
        super(cell, team);
        ritualFor = cell.squad;
    }

    public AbstractSquad getRitualFor() {
        return ritualFor;
    }

    public int getProgress() {
        return progress;
    }

    public void makeProgress() {
        progress++;
        if (canBeFinished()) {
            team.updateEvolutionExp(EVOLUTION_EXP);
            getLastCell().ritual = null;
            new RitualDialog(ritualFor.unit).start();
        }
    }

    public boolean canBeFinished() {
        return progress == STEPS;
    }

    public void cancel() {

    }
}
