package conversion7.game.stages.world.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.team.Team;

public abstract class ResourceObject extends AreaObject implements AreaObjectTickable {
    public static final Vector3 SHIFT_POS = MathUtils.toEngineCoords(new Vector3(0, 0, 0));

    public ResourceObject(Cell cell, Team team) {
        super(cell, team);
        init();
    }
    @Override
    public boolean givesExpOnHurt() {
        return false;
    }
    @Override
    public SceneGroup3d buildSceneBody() {
        SceneGroup3d sceneBody = super.buildSceneBody();
        ModelActor modelActor;
        modelActor = new ModelActor("ResourceObject", Modeler.buildMountDebrisModel(Color.CYAN), Gdxg.modelBatch);
        sceneBody.addNode(modelActor);
        modelActor.setPosition(SHIFT_POS);
        return sceneBody;
    }

    @Override
    public void validateView() {
        getLastCell().setRefreshedInView(false);
        super.validateView();
    }

}
