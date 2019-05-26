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
import conversion7.game.stages.world.unit.Power2;
import conversion7.game.unit_classes.UnitClassConstants;

public class MountainDebris extends BuildingObject {
    public static final Vector3 SHIFT_POS = MathUtils.toEngineCoords(new Vector3(0, 0, 0));

    public MountainDebris(Cell cell, Team team) {
        super(cell, team);
        init();
        power = new Power2(this);
        power.updateMaxValue(UnitClassConstants.BASE_DMG);
        validateView();
    }
    public MountainDebris(Cell cell) {
        this(cell, Gdxg.core.world.animalTeam);
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
        modelActor = new ModelActor("MountainDebris", Modeler.buildMountDebrisModel(Color.DARK_GRAY), Gdxg.modelBatch);
        sceneBody.addNode(modelActor);
        modelActor.setPosition(SHIFT_POS);
        return sceneBody;
    }

}
