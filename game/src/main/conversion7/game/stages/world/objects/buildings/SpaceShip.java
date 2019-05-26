package conversion7.game.stages.world.objects.buildings;

import com.badlogic.gdx.graphics.Color;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.geometry.Modeler;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.ResourceObject;
import conversion7.game.stages.world.quest.items.SendSosQuest;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Power2;
import conversion7.game.unit_classes.UnitClassConstants;

public class SpaceShip extends BuildingObject {

    public static final int BASE_HP = UnitClassConstants.BASE_POWER * 5;

    public SpaceShip(Cell cell, Team team) {
        super(cell, team);
        power = new Power2(this);
        power.updateMaxValue(BASE_HP);
        init();
    }

    @Override
    public boolean isCellMainSlotObject() {
        return true;
    }

    @Override
    public void init() {
        super.init();
        addDeathListener(object -> {
            for (Team humanPlayer : team.world.humanPlayers) {
                humanPlayer.journal.getOrCreate(SendSosQuest.class).failAllOpen();
            }
        });
        team.world.addImportantObj(this);
        validateView();
    }

    @Override
    public SceneGroup3d buildSceneBody() {
        SceneGroup3d sceneBody = super.buildSceneBody();
        ModelActor modelActor;
        modelActor = new ModelActor("SpaceShip",
                Modeler.buildCampBox(Color.PURPLE), Gdxg.modelBatch);
        sceneBody.addNode(modelActor);
        modelActor.setPosition(ResourceObject.SHIFT_POS);
        return sceneBody;
    }

}
