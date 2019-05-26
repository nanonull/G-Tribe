package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.unit.effects.items.ConcealmentEffect;

public class ConcealmentAction extends AbstractSquadAction {

    public static final String DESC = "Hide and whisper...";

    public ConcealmentAction() {
        super(Group.DEFENCE);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.getTextureReg("conceal");
    }

    @Override
    public String getShortName() {
        return "Conceal";
    }

    @Override
    public String buildDescription() {
        return getName() + "\n \n" + DESC;
    }

    @Override
    public void begin() {
        ConcealmentEffect concealmentEffect = getSquad().getEffectManager().getOrCreate(ConcealmentEffect.class);
        concealmentEffect.resetTickCounter();
    }

}
