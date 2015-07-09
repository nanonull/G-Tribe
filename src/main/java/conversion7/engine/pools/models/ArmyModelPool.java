package conversion7.engine.pools.models;

import com.badlogic.gdx.utils.Pool;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.utils.MathUtils;
import conversion7.game.Assets;
import conversion7.scene3dOld.Actor3d;

import static conversion7.game.stages.battle.Battle.ANIM_SPEED;
import static conversion7.game.stages.battle.Battle.ANIM_TRANSITION;
import static conversion7.game.stages.battle.BattleFigure.AnimationMode.IDLE;

public class ArmyModelPool extends Pool<ModelActor> {
    public static final float ACTOR_SCALE = 0.09f;
    public static final float ACTOR_Z = 0.865f;

    @Override
    protected ModelActor newObject() {
        Actor3d knight = new Actor3d(Assets.getModel("knight"));
        ModelActor modelActor = new ModelActor("army", knight, Gdxg.modelBatch);
        modelActor.setEnvironment(Gdxg.graphic.environment);
        modelActor.setScale(ACTOR_SCALE);
        modelActor.translate(MathUtils.toEngineCoords(0, 0, ACTOR_Z));
        knight.animating = true;
        knight.getAnimation().animate(IDLE.toString(), -1, ANIM_SPEED / 5, null, ANIM_TRANSITION);

        modelActor.linkToPool(this);
        return modelActor;
    }

    @Override
    public ModelActor obtain() {
        return super.obtain();
    }
}
