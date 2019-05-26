package conversion7.game.stages.world.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.buildings.BuildingObject;
import conversion7.game.stages.world.team.Team;

public class ScorpionObject extends BuildingObject {
    public static final Vector3 SHIFT_POS = MathUtils.toEngineCoords(new Vector3(0, 0, 0));

    public ScorpionObject(Cell cell, Team team) {
        super(cell, team);
    }
    @Override
    public SceneGroup3d buildSceneBody() {
        SceneGroup3d sceneBody = super.buildSceneBody();
        ModelActor modelActor;
        modelActor = new ModelActor("ScorpionObject", Modeler.buildMountDebrisModel(Color.ORANGE), Gdxg.modelBatch);
        sceneBody.addNode(modelActor);
        modelActor.setPosition(SHIFT_POS);
        return sceneBody;
    }
}
