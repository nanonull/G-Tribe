package conversion7.engine.artemis.ui;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class UnitHeroIndicatorSystem extends IteratingSystem {
    public static ComponentMapper<UnitHeroIndicatorComp> components;

    public UnitHeroIndicatorSystem() {
        super(Aspect.all(UnitHeroIndicatorComp.class));
    }

    @Override
    protected void process(int entityId) {
        UnitHeroIndicatorComp comp = components.get(entityId);
        AbstractSquad owner = comp.squad;
        owner.getUnitInWorldHintPanel().setHeroIndicator(owner.getHeroClass());
        owner.getUnitInWorldHintPanel().getUnitIndicatorIconsPanel().setHeroIndicator(owner.getHeroClass());
        components.remove(entityId);
    }
}