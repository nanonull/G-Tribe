package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitAge;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class TeachAuraEffect extends AbstractUnitEffect {

    public static final int EXP_AMOUNT = 1;
    public static final int STARTS_FROM_LEVEL = UnitAge.MATURE.getLevel();
    public static final int EFFECT_DOUBLED_ON_LEVEL = UnitAge.OLD.getLevel();
    private static final int TEACH_STEPS_INTERVAL = 1;

    public TeachAuraEffect() {
        super(TeachAuraEffect.class.getSimpleName(), Type.POSITIVE);
    }

    @Override
    public String getHint() {
        return super.getHint() + "\n \nLower-level allies around get experience each step: +" + EXP_AMOUNT + "\n" +
                "Experience is doubled if teacher has level: " + UnitAge.getLevelLabelUi(EFFECT_DOUBLED_ON_LEVEL);
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCounter >= TEACH_STEPS_INTERVAL) {
            teachUnitsAround(this.getOwner().unit);
            resetTickCounter();
        }
    }

    private void teachUnitsAround(Unit teacherUnit) {
        for (Cell cell : teacherUnit.squad.getLastCell().getCellsAround()) {
            if (cell.hasSquad() && cell.squad.team == teacherUnit.squad.team) {
                int expAmount = teacherUnit.squad.getAge().getLevel() >= EFFECT_DOUBLED_ON_LEVEL ?
                        EXP_AMOUNT * 2 : EXP_AMOUNT;
                cell.squad.updateExperience(expAmount, "Learning exp");
            }
        }
    }
}
