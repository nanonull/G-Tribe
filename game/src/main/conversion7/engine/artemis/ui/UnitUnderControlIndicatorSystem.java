package conversion7.engine.artemis.ui;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;

/** Use panel border colors */
@Deprecated
public class UnitUnderControlIndicatorSystem extends IteratingSystem {
    public static ComponentMapper<UnitUnderControlIndicatorComp> components;

    public UnitUnderControlIndicatorSystem() {
        super(Aspect.all(UnitUnderControlIndicatorComp.class));
    }

    @Override
    protected void process(int entityId) {
        UnitUnderControlIndicatorComp comp = components.get(entityId);
        components.remove(entityId);
//        AbstractSquad owner = comp.squad;
//        Team playerTeam;
//        try {
//            playerTeam = Gdxg.core.world.playerTeam;
//        } catch (NullPointerException e) {
//            return;
//        }
//
//        owner.getUnitInWorldHintPanel().getUnitIndicatorIconsPanel().setShamanIndicator(false);
//        UnderControlEffect underControlEffect = owner.unit.getEffectManager().getEffect(UnderControlEffect.class);
//        if (underControlEffect != null) {
//            if (underControlEffect.controller.squad.team == playerTeam) {
//                owner.getUnitInWorldHintPanel().getUnitIndicatorIconsPanel().setShamanIndicator(true);
//            }
//        }
    }
}