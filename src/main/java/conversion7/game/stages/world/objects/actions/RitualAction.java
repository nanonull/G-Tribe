package conversion7.game.stages.world.objects.actions;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.PackageReflectedConstants;
import conversion7.game.stages.battle.BattleSide;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.actions.subactions.InputFoodForRitualSubaction;
import conversion7.game.stages.world.objects.effects.AbstractObjectEffect;

public class RitualAction extends AbstractAreaObjectAction {

    public static final int EVOLUTION_SUBPOINT_PER_FOOD = 10;
    private InputFoodForRitualSubaction inputFoodForRitualSubAction = new InputFoodForRitualSubaction(this);


    public RitualAction(AreaObject object) {
        super(object);
    }

    @Override
    public void execute() {
        if (World.getAreaViewer().selectedObject.couldPartiallyMove()) {
            inputFoodForRitualSubAction.execute();
            Gdxg.clientUi.hideBarsForSelectedObject();
        } else {
            Gdxg.clientUi.getInfoDialog().show("Could not start ritual", "There should be at least 1 unit with Action points.");
        }
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.ACTOR_TEXTURES.get(BattleSide.UP);
    }

    @Override
    public void cancel() {
        World.getAreaViewer().unhideSelection();
    }

    public static void complete(int foodUsed, AreaObject areaObject) {
        // TODO use Creator Phase algorithm a/o check how many food used
        areaObject.decreaseActionPoints();
        areaObject.getFoodStorage().updateFoodOnValueAndValidate(-foodUsed);

        areaObject.getTeam().updateEvolutionSubPointsOn(foodUsed * EVOLUTION_SUBPOINT_PER_FOOD);
        if (Utils.RANDOM.nextInt(10) < 2) {
            areaObject.addEffectIfAbsentOtherwiseProlong(getRandomEffect());
        }
    }

    public void complete(Integer foodUsed) {
        complete(foodUsed, getObject());
        Gdxg.clientUi.showBarsForSelectedObject();
    }

    private static Class<? extends AbstractObjectEffect> getRandomEffect() {
        int nextInt = Utils.RANDOM.nextInt(PackageReflectedConstants.AREA_OBJECT_EFFECT_CLASSES.size);
        return PackageReflectedConstants.AREA_OBJECT_EFFECT_CLASSES.get(nextInt);
    }
}
