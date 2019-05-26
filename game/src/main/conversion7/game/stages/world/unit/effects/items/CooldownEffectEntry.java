package conversion7.game.stages.world.unit.effects.items;

public class CooldownEffectEntry {
    public int step;

    public CooldownEffectEntry(CooldownEffect.Type type) {
        this.step = type.steps;

    }
}
