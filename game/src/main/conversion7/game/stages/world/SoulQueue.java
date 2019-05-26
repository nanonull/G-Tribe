package conversion7.game.stages.world;

import conversion7.game.stages.world.elements.Soul;
import conversion7.game.stages.world.elements.SoulType;

import java.util.LinkedList;

public class SoulQueue extends LinkedList<Soul> {

    @Override
    public Soul pop() {
        if (isEmpty()) {
            return new Soul(SoulType.getRandom());
        } else {
            return super.pop();
        }
    }
}
