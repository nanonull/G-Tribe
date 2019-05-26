package conversion7.game.stages.world.unit.effects.items;

import com.badlogic.gdx.utils.ObjectSet;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class VengeanceHitEffect extends AbstractUnitEffect {
    public static final String DESC = "Poisons all injured targets after kill made";
    private ObjectSet<AreaObject> hurtObjects = new ObjectSet<>();

    public VengeanceHitEffect() {
        super(VengeanceHitEffect.class.getSimpleName(), Type.POSITIVE);
    }

    @Override
    public String getHint() {
        return super.getHint() +
                "\n \n" + DESC;
    }

    @Override
    public String getShortIconName() {
        return "VenHit";
    }

    public void actOnHurtUnits() {
        for (AreaObject object : hurtObjects) {
            if (object.isSquad()) {
                AbstractSquad squad = object.toSquad();
                if (squad.isAlive()) {
                    squad.effectManager.getOrCreate(VengeancePoisonEffect.class).resetTickCounter();
                }
            }
        }
        hurtObjects.clear();
    }

    public void hurt(AreaObject target) {
        hurtObjects.add(target);
    }
}
