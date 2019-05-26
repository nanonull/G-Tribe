package conversion7.game.stages.world.unit.effects.items;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Predicate;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class CooldownEffect extends AbstractUnitEffect {

    public static final int CD = 5;

    public ObjectMap<Type, CooldownEffectEntry> cooldowns = new ObjectMap<>();

    public CooldownEffect() {
        super(CooldownEffect.class.getSimpleName(), AbstractUnitEffect.Type.POSITIVE_NEGATIVE, null);
    }


    @Override
    public String getShortIconName() {
        return "CDs";
    }

    @Override
    public String getHint() {
        return "\n \nAll unit cool downs:\n" + getCdsHint();
    }

    private String getCdsHint() {
        StringBuilder builder = new StringBuilder();
        for (ObjectMap.Entry<Type, CooldownEffectEntry> cooldown : cooldowns) {
            builder.append(cooldown.key).append(": ").append(cooldown.value.step).append("\n");
        }
        return builder.toString();
    }


    public void addCooldown(Type type) {
        cooldowns.put(type, new CooldownEffectEntry(type));

    }

    @Override
    public void tick() {
        for (ObjectMap.Entry<Type, CooldownEffectEntry> entry : cooldowns) {
            entry.value.step--;
            if (entry.value.step <= 0) {
                if (entry.key.getPostAction() != null) {
                    entry.key.postAction.evaluate(getOwner().unit);
                }
            }
        }

        ObjectMap.Entries<Type, CooldownEffectEntry> cdIterator = cooldowns.iterator();
        while (cdIterator.hasNext()) {
            ObjectMap.Entry<Type, CooldownEffectEntry> entry = cdIterator.next();
            if (entry.value.step <= 0) {
                cdIterator.remove();
            }
        }

        if (cooldowns.size == 0) {
            remove();
        }
    }

    public boolean hasCooldown(Type type) {
        return cooldowns.containsKey(type);
    }

    public enum Type {
        TEST(5, null),
        ARCHON_CONCEALMENT_RELOAD(5, unit -> {
            unit.squad.getEffectManager().getOrCreate(ConcealmentEffect.class);
            return true;
        }),
        SPRINT(2, null);

        private Predicate<Unit> postAction;
        public int steps;

        Type(int steps, Predicate<Unit> postAction) {
            this.steps = steps;
            this.postAction = postAction;
        }

        public Predicate<Unit> getPostAction() {
            return postAction;
        }
    }
}
