package conversion7.game.stages.world.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.team.Team;

public class BurntForest extends AreaObject implements AreaObjectTickable {

    public static final int TOTEM_RADIUS = World.BASE_VIEW_RADIUS;
    public static final Vector3 SHIFT_POS = MathUtils.toEngineCoords(new Vector3(0.3f, -0.3f, 0));
    private static final int DURATION = BurningForest.DURATION;
    private int step;
    @Override
    public boolean givesExpOnHurt() {
        return true;
    }
    public BurntForest(Cell cell, Team team) {
        super(cell, team);
        init();
        validateView();
    }

    public int getRadius() {
        return TOTEM_RADIUS;
    }

    @Override
    public String getShortHint() {
        return super.getShortHint() + " " + step + "/" + DURATION;
    }


    @Override
    public SceneGroup3d buildSceneBody() {
        SceneGroup3d sceneBody = super.buildSceneBody();
        ModelActor modelActor;
        modelActor = new ModelActor("BurntForest",
                Modeler.buildHalfCampBox(Color.DARK_GRAY), Gdxg.modelBatch);
        sceneBody.addNode(modelActor);
        modelActor.setPosition(ResourceObject.SHIFT_POS);

        return sceneBody;
    }

    @Override
    public void validateView() {
        getLastCell().setRefreshedInView(false);
        super.validateView();
    }

    @Override
    public void tick() {
        if (step == DURATION) {
            removeFromWorld();
        }
        step++;
    }
}
