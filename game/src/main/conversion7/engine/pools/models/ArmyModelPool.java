package conversion7.engine.pools.models;

import com.badlogic.gdx.utils.Pool;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.Actor3d;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.utils.MathUtils;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

import static conversion7.game.stages.battle_deprecated.Battle.ANIM_SPEED;
import static conversion7.game.stages.battle_deprecated.Battle.ANIM_TRANSITION;
import static conversion7.game.stages.battle_deprecated.BattleFigure.AnimationMode.IDLE;

public class ArmyModelPool extends Pool<ModelActor> {

    @Override
    protected ModelActor newObject() {
        Actor3d knight = new Actor3d(Assets.getModel("knight"));
        ModelActor modelActor = new ModelActor("army", knight, Gdxg.modelBatch);
        modelActor.setEnvironment(Gdxg.graphic.environment);
        modelActor.setScale(AbstractSquad.ACTOR_SCALE);
        modelActor.translate(MathUtils.toEngineCoords(0, 0, AbstractSquad.ACTOR_Z));
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
