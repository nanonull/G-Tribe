package conversion7.game.ai.global.branches;

import com.badlogic.gdx.utils.Predicate;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

import java.util.Iterator;

public class UnitGoalsAiBranch {
    public static void eval(AbstractSquad squad) {
        Iterator<Predicate<AbstractSquad>> iterator = squad.getAiGoals().iterator();
        while (iterator.hasNext()) {
            Predicate<AbstractSquad> squadPredicate = iterator.next();
            boolean completed = squadPredicate.evaluate(squad);
            if (completed) {
                iterator.remove();
            }
        }
    }
}
