package conversion7.game.stages.world.elements;

import conversion7.engine.Gdxg;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class Soul {

    public SoulType type;
    private AbstractSquad unit;
    private int unitLevels;
    private int lifes;

    public Soul(SoulType soulType) {
        type = soulType;
    }

    public SoulType getType() {
        return type;
    }

    public AreaObject getUnit() {
        return unit;
    }

    public void setUnit(AbstractSquad unit) {
        this.unit = unit;
    }

    public int getUnitLevels() {
        return unitLevels;
    }

    public int getLifes() {
        return lifes;
    }

    public String getDescription() {
        return "Soul: " + type + "";
    }

    public String getMoreDescription() {
        if (lifes == 0) {
            return "Fresh soul";
        } else {
            return "Lifes: " + lifes +
                    "\nUnit exp levels: " + unitLevels;
        }
    }

    public void free() {
        Gdxg.core.world.soulQueue.add(this);
        unitLevels += unit.getExpLevel();
        lifes++;
    }
}
