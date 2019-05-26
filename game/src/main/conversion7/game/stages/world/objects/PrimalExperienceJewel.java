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
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.Unit;

import static conversion7.engine.utils.MathUtils.random;

public class PrimalExperienceJewel extends AreaObject {

    public static final Vector3 SHIFT_POS = MathUtils.toEngineCoords(new Vector3(0.1f, -0.1f, 0));
    public static final int BASE_EXP = Unit.BASE_EXP_FOR_LEVEL / 2;
    public static final int MAX_EXP = BASE_EXP * 2;
    public int exp;
    @Override
    public boolean givesExpOnHurt() {
        return false;
    }
    public PrimalExperienceJewel(Cell cell) {
        super(cell, null);
        init();
        this.exp = random(PrimalExperienceJewel.BASE_EXP, PrimalExperienceJewel.MAX_EXP);
        if (cell.hasSquad()) {
            pickedBy(cell.squad);
        } else {
            validateView();
        }
    }

    @Override
    public String getShortHint() {
        return super.getShortHint() + " " + exp + " exp";
    }

    public static boolean canBeCreatedOn(Cell cell) {
        return cell.getObject(PrimalExperienceJewel.class) == null
                && cell.hasLandscapeAvailableForMove();
    }

    public void consumeBy(AbstractSquad squad) {
        if (squad.unit == null) {
            Utils.LOG.error("consumeBy squad.unit");
        } else {
            squad.updateExperience(exp);
            removeFromWorld();
        }
    }

    public void pickedBy(AbstractSquad squad) {

        if (squad.team.isHumanPlayer()) {
            if (squad.unit == null) {
                consumeBy(squad);
            } else {
                new PrimalExperienceDialog1(squad.unit, this).start();
            }
        } else {
            consumeBy(squad);
        }
    }

    @Override
    public SceneGroup3d buildSceneBody() {
        SceneGroup3d sceneBody = super.buildSceneBody();
        ModelActor modelActor;
        modelActor = new ModelActor("PrimalExperienceJewel", Modeler.buildSmallBox(Color.MAGENTA), Gdxg.modelBatch);
        sceneBody.addNode(modelActor);
        modelActor.setPosition(SHIFT_POS);
        return sceneBody;
    }

    public static PrimalExperienceJewel create(Cell cell) {
        return new PrimalExperienceJewel(cell);
    }
}
