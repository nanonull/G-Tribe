package conversion7.game.stages.world.objects.composite;

import com.badlogic.gdx.graphics.Color;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.geometry.Modeler;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.unit.Power2;
import conversion7.game.unit_classes.UnitClassConstants;

public class SandWormPart extends AreaObject {

    public static final int BASE_POWER = (int) (UnitClassConstants.BASE_POWER * 1.5f);
    @Override
    public boolean givesExpOnHurt() {
        return true;
    }
    public SandWormPart(){
        super(null, null);
        power = new Power2(this);
        power.updateMaxValue(BASE_POWER);
        setName(SandWormPart.class.getSimpleName());
    }

    @Override
    public boolean isCellMainSlotObject() {
        return true;
    }

    @Override
    public boolean givesCornerDefenceBonus() {
        return true;
    }

    @Override
    public SceneGroup3d buildSceneBody() {
        SceneGroup3d sceneBody = super.buildSceneBody();
        ModelActor modelActor;
        modelActor = new ModelActor(SandWormPart.class.getSimpleName(),
                Modeler.buildBox(Color.VIOLET, 0.65f, 0.75f), Gdxg.modelBatch);
        sceneBody.addNode(modelActor);
        return sceneBody;
    }
}
