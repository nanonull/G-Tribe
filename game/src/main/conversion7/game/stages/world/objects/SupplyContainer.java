package conversion7.game.stages.world.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.dialogs.PrimalExperienceDialog1;
import conversion7.game.stages.world.inventory.BasicInventory;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;

import static conversion7.engine.utils.MathUtils.random;

public class SupplyContainer extends AreaObject {

    public static final Vector3 SHIFT_POS = MathUtils.toEngineCoords(new Vector3(-0.1f, -0.2f, 0));

    public BasicInventory inventory = new BasicInventory();

    public SupplyContainer(Cell cell, Team team) {
        super(cell, team);
    }

    @Override
    public String getShortHint() {
        return getName();
    }

    @Override
    public SceneGroup3d buildSceneBody() {
        SceneGroup3d sceneBody = super.buildSceneBody();
        ModelActor modelActor;
        modelActor = new ModelActor("SupplyContainer",
                Modeler.buildSmallBox(Color.TEAL), Gdxg.modelBatch);
        sceneBody.addNode(modelActor);
        modelActor.setPosition(SHIFT_POS);
        return sceneBody;
    }

    @Override
    public boolean givesExpOnHurt() {
        return false;
    }

    public void pickedBy(AbstractSquad squad) {
        squad.team.getInventory().moveItems(inventory);
        removeFromWorld();
    }
}
