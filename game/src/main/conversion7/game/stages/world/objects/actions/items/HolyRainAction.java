package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.engine.artemis.ui.float_lbl.FloatingStatusOnCellSystem;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.BurningForest;
import conversion7.game.stages.world.objects.BurntForest;
import conversion7.game.stages.world.objects.actions.AbstractHumanSquadAction;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;
import conversion7.game.stages.world.unit.effects.UnitEffectManager;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HolyRainAction extends AbstractHumanSquadAction {

    public static final int RADIUS = 2;
    public static final int MANA_COST = 10;
    public static final String DESC = "Holy rain. " +
            "\nCleans negative effects and fire." +
            "\nRadius: " + RADIUS;

    public HolyRainAction() {
        super(Group.DEFENCE);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.getTextureReg("rain");
    }

    @Override
    public String getActionWorldHint() {
        return "rain";
    }

    @Override
    public int getDistance() {
        return getSquad().getViewRadius(false);
    }

    @Override
    public String getShortName() {
        return "HRain";
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.SCARLET;
    }

    @Override
    public String buildDescription() {
        return getName() + "\n \n" + DESC;
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        rainOn(input);
        for (Cell adjCell : input.getCellsAroundToRadiusInclusively(RADIUS)) {
            rainOn(adjCell);
        }
    }

    private void rainOn(Cell cell) {
        WorldSquad caster = getSquad();
        FloatingStatusOnCellSystem.scheduleMessage(cell, caster.team, "+ Holy Rain +", Color.CYAN);

        // effects
        if (cell.hasSquad() && cell.squad.team.isAllyOf(caster.team)) {
            UnitEffectManager effectManager = cell.squad.getEffectManager();
            List<AbstractUnitEffect> negEffects = Stream.of(effectManager.effects.toArray())
                    .filter(effect -> effect.type == AbstractUnitEffect.Type.NEGATIVE)
                    .collect(Collectors.toList());
            for (AbstractUnitEffect negEffect : negEffects) {
                effectManager.removeEffect(negEffect);

            }
        }

        // fire
        if (cell.getLandscape().hasFireEffect()) {
            cell.removeObjectIfExist(BurningForest.class);
            cell.removeObjectIfExist(BurntForest.class);
        }
    }
}
