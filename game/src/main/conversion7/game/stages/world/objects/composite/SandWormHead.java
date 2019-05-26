package conversion7.game.stages.world.objects.composite;

import com.badlogic.gdx.graphics.Color;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.geometry.Modeler;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.unit.Power2;
import conversion7.game.unit_classes.UnitClassConstants;

public class SandWormHead extends SandWormPart {

    public SandWormHead(){
        super();
        setName(SandWormHead.class.getSimpleName());
        power.updateMaxValue(BASE_POWER / 2);
    }

    @Override
    public SceneGroup3d buildSceneBody() {
        SceneGroup3d sceneBody = super.buildSceneBody();
        ModelActor modelActor;
        modelActor = new ModelActor(SandWormHead.class.getSimpleName(),
                Modeler.buildBox(Color.MAROON, 0.95f, 0.85f), Gdxg.modelBatch);
        sceneBody.addNode(modelActor);
        return sceneBody;
    }
}
