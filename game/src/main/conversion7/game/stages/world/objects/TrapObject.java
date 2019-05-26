package conversion7.game.stages.world.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.CellRandomEvents;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.effects.items.StunnedEffect;

public class TrapObject extends AreaObject {
    public static final Vector3 SHIFT_POS = MathUtils.toEngineCoords(new Vector3(-0.3f, -0.1f, 0));
    public static final int BASE_DMG = CellRandomEvents.BASE_DMG * 2;
    public AbstractSquad owner;

    public TrapObject(Cell cell, Team team) {
        super(cell, team);
    }

    @Override
    public String getShortHint() {
        return getName() + " " + team.getName();
    }

    public static boolean canSeize(Cell cell) {
        return cell.canBeSeized() && !cell.containsObject(TrapObject.class);
    }

    @Override
    public boolean givesExpOnHurt() {
        return true;
    }

    @Override
    public SceneGroup3d buildSceneBody() {
        SceneGroup3d sceneBody = super.buildSceneBody();
        ModelActor modelActor;
        modelActor = new ModelActor(getName(), Modeler.buildSmallBox(Color.ORANGE), Gdxg.modelBatch);
        sceneBody.addNode(modelActor);
        modelActor.setPosition(SHIFT_POS);
        return sceneBody;
    }

    public void act(AbstractSquad squad) {
        if (squad.hurtBy(BASE_DMG, owner)) {
        } else {
            StunnedEffect stunnedEffect = squad.effectManager.getOrCreate(StunnedEffect.class);
            stunnedEffect.resetTickCounter();
        }
        destroyObject();
    }
}
