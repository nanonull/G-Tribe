package conversion7.game.stages.world.unit.effects;

import conversion7.game.interfaces.HintProvider;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitParameters;

public class AbstractUnitEffect implements HintProvider {

    public enum Type {
        POSITIVE, NEGATIVE, POSITIVE_NEGATIVE
    }

    public String name;
    public UnitParameters effectParameters;
    public Type type;
    protected int tickCounter = 0;

    private Unit owner;

    public AbstractUnitEffect(String name, Type type, UnitParameters effectParameters) {
        this.name = name;
        this.type = type;
        this.effectParameters = effectParameters;
    }

    public void tick() {
        tickCounter++;
    }

    public int getTickCounter() {
        return tickCounter;
    }

    public Unit getOwner() {
        return owner;
    }

    public void setOwner(Unit owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getHint() {
        return toString();
    }

}
