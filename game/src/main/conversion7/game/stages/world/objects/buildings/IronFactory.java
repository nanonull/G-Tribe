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

public class IronFactory extends BuildingObject {

    public IronFactory(Cell cell, Team team) {
        super(cell, team);
        init();
    }
    @Override
    public void init() {
        super.init();
        team.ironFactory = this;
        if (team.isHumanPlayer()) {
            team.journal.getOrCreate(SendSosQuest.class).complete(SendSosQuest.State.S1);
            team.journal.getOrCreate(SendSosQuest.class).complete(SendSosQuest.State.S2);
        }
    }

    @Override
    public SceneGroup3d buildSceneBody() {
        SceneGroup3d sceneBody = super.buildSceneBody();
        ModelActor modelActor;
        modelActor = new ModelActor("IronFactory",
                Modeler.buildCampBox(Color.GREEN), Gdxg.modelBatch);
        sceneBody.addNode(modelActor);
        modelActor.setPosition(ResourceObject.SHIFT_POS);
        return sceneBody;
    }


}
