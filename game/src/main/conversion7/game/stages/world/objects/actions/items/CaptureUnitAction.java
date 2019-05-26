package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Normalizer;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.TribeRelationType;
import conversion7.game.stages.world.unit.Power2;
import conversion7.game.stages.world.unit.effects.items.ScaredEffect;
import conversion7.game.stages.world.unit.effects.items.StunnedEffect;
import conversion7.game.unit_classes.UnitClassConstants;
import org.slf4j.Logger;

public class CaptureUnitAction extends AbstractWorldTargetableAction {
    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int GRAD_STEP = UnitClassConstants.maxClassLvl / 3;
    /** Decrease = capture percent ++ */
    public static final int MAX_CAPT_LVL = GRAD_STEP * 3;
    public static final int MIN_CAPT_LVL = -GRAD_STEP;
    public static int MIN_UNITS_AROUND_FOR_CAPTURE = 4;

    public CaptureUnitAction() {
        super(Group.ATTACK);
    }

    public static int getCaptureChance(AbstractSquad attacker, AbstractSquad target) {
        int perc = getBaseLevelDiffPercChance(attacker, target);
        if (target.isDisabled()) {
            perc += StunnedEffect.CAPT_ADD_ON_STUN_PERC;
        }
        if (target.effectManager.containsEffect(ScaredEffect.class)) {
            perc *= ScaredEffect.CAPTURE_MLT;
        }
        return perc;
    }

    public static int getBaseLevelDiffPercChance(AbstractSquad attacker, AbstractSquad target) {
        int lvlDiff = attacker.unit.classStandard.level - target.unit.classStandard.level;
        return (int) Normalizer.normalize(lvlDiff
                , MAX_CAPT_LVL, MIN_CAPT_LVL, 100, 0);
    }

    public static void tryCapture(AbstractSquad attacker, AbstractSquad target) {
        int capturePerc = getCaptureChance(attacker, target);
        LOG.info("capturePerc " + capturePerc);
        if (MathUtils.testPercentChance(capturePerc)) {
            if (MathUtils.random(0,10)< 2) {
                target.cell.addFloatLabel("Where is my memories?", Color.ORANGE);
            }
            attacker.team.joinSquad(target);
            target.cell.addFloatLabel("Unit captured % " + capturePerc, Color.GREEN);
            attacker.updateExperience(Power2.EXPERIENCE_PER_CAPTURE);
        } else {
            target.cell.addFloatLabel("Capture failed % " + capturePerc, Color.GREEN);
        }
        target.team.world.addRelationType(TribeRelationType.UNIT_CAPTURED, target.team, attacker.team);
    }

    @Override
    public int getDistance() {
        return getSquad().getViewRadius(false);
    }

    @Override
    public String getShortName() {
        return "Capture";
    }

    @Override
    public String getActionWorldHint() {
        return "capture unit into your tribe";
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.getTextureReg("capture_unit");
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.SCARLET;
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        tryCapture(getSquad(), input.squad);
    }

    @Override
    protected String buildDescription() {
        return getName() + "\n \nForce unit of another team join to your team.\n \n"
                + "\n" + ScaredEffect.SCARE_TO_CONTROL_HINT
                + "\nBe careful probably other tribe will not love you for this action.";
    }
}
