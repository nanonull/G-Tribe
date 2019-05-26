package conversion7.engine.artemis.ui;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class UnitUltIndicatorSystem extends IteratingSystem {
    public static ComponentMapper<UnitUltIndicatorComp> components;

    public UnitUltIndicatorSystem() {
        super(Aspect.all(UnitUltIndicatorComp.class));
    }

    @Override
    protected void process(int entityId) {
        UnitUltIndicatorComp comp = components.get(entityId);
        components.remove(entityId);
        AbstractSquad owner = comp.squad;
        owner.getUnitInWorldHintPanel().getUnitIndicatorIconsPanel().setUltIndicator(owner.hasSuperAbilityReady());
    }
}