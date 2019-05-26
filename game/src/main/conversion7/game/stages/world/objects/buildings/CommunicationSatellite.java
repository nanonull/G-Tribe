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

public class CommunicationSatellite extends BuildingObject {

    public CommunicationSatellite(Cell cell, Team team) {
        super(cell, team);
        init();
    }
    @Override
    public void init() {
        super.init();
        team.setSatellite(this);
        if (team.isHumanPlayer()) {
            team.journal.getOrCreate(SendSosQuest.class).complete(SendSosQuest.State.S4);
        }
    }

    @Override
    public SceneGroup3d buildSceneBody() {
        SceneGroup3d sceneBody = super.buildSceneBody();
        ModelActor modelActor;
        modelActor = new ModelActor("CommunicationSatellite",
                Modeler.buildCampBox(Color.PURPLE), Gdxg.modelBatch);
        sceneBody.addNode(modelActor);
        modelActor.setPosition(ResourceObject.SHIFT_POS);
        return sceneBody;
    }

}