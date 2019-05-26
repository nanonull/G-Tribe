package conversion7.game.unit_classes.ufo;

import com.badlogic.gdx.graphics.Color;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.effects.items.CooldownEffect;

// player
public class Archon extends AbstractUfoUnit {

    public static void testContact(Unit active, Unit passive) {
        if (true) return;
        if (active.getClass() == Archon.class) {
            if (passive.squad.testScaredByUfo()) {
                passive.squad.batchFloatingStatusLines.start();
                passive.squad.batchFloatingStatusLines.addImportantLine("Contact Unknowns");
                passive.squad.setPanic();
                passive.squad.batchFloatingStatusLines.flush(Color.ORANGE);
            }
        }
    }

    @Override
    public void revealIfConcealed() {
        super.revealIfConcealed();
        CooldownEffect cooldownEffect = squad.getEffectManager().getOrCreate(CooldownEffect.class);
        cooldownEffect.addCooldown(CooldownEffect.Type.ARCHON_CONCEALMENT_RELOAD);
    }

}
