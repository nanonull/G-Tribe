package conversion7.game.stages.world.gods;

import conversion7.game.stages.world.elements.ElementType;

public abstract class AbstractGod {

    private String type;
    public int expPoints;
    public int expPercent;
    ElementType elementType;

    public AbstractGod(String type) {
        this.type = type;
        elementType = ElementType.GODS_BY_ELEMENTS.get(this.getClass());
    }

    public String getNameAndType() {
        return getName() + " [" + (elementType == null ? type : elementType.toString()) + "]";
    }

    public String getName() {
        return getClass().getSimpleName();
    }

}
