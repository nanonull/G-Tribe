package conversion7.game.stages.world.objects.unit;

import com.badlogic.gdx.graphics.Color;
import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.unit.effects.items.PoisonEffect;
import conversion7.game.stages.world.unit.effects.items.WeakeningEffect;
import conversion7.game.unit_classes.UnitClassConstants;

public class CellRandomEvents {
    private static final int BASE_CHANCE = 10;
    public static final int BASE_DMG = (int) (UnitClassConstants.BASE_POWER * 0.1f);
    private static final int NEG_FOREST_CHANCE = BASE_CHANCE / 2;

    public static void apply(AbstractSquad squad) {
        Cell cell = squad.cell;
        squad.batchFloatingStatusLines.start();

        if (cell.getLandscape().hasBog()) {
            if (MathUtils.testPercentChance(BASE_CHANCE)) {
                squad.batchFloatingStatusLines.addLine("Snake attack");
                squad.hurtBy(BASE_DMG, null);
                if (MathUtils.random()) {
                    squad.effectManager.getOrCreate(PoisonEffect.class).resetTickCounter();
                }
            } else if (MathUtils.testPercentChance(BASE_CHANCE)) {
                squad.batchFloatingStatusLines.addLine("Alligator bite");
                squad.hurtBy(MathUtils.random(BASE_DMG, BASE_DMG * 5), null);
            }
        }

        if (cell.getLandscape().hasForest()) {
            if (MathUtils.testPercentChance(NEG_FOREST_CHANCE)) {
                squad.batchFloatingStatusLines.addLine("Bee attack");
                squad.hurtBy(1, null);
            } else if (MathUtils.testPercentChance(BASE_CHANCE)) {
                squad.batchFloatingStatusLines.addLine("Rest in forest: success");
                squad.heal(MathUtils.random(BASE_DMG, BASE_DMG * 3));
            } else if (MathUtils.testPercentChance(NEG_FOREST_CHANCE)) {
                squad.batchFloatingStatusLines.addLine("Bad mushrooms");
                squad.hurtBy(MathUtils.random(1, BASE_DMG), null);
                if (MathUtils.random()) {
                    squad.effectManager.getOrCreate(PoisonEffect.class).resetTickCounter();
                }
            } else if (MathUtils.testPercentChance(NEG_FOREST_CHANCE)) {
                squad.batchFloatingStatusLines.addLine("Wolf attack");
                squad.hurtBy(MathUtils.random(1, BASE_DMG * 2), null);
            }
        }

        if (cell.getLandscape().isDesert()) {
            if (MathUtils.testPercentChance(BASE_CHANCE)) {
                squad.batchFloatingStatusLines.addLine("Scorpion attack");
                squad.hurtBy(MathUtils.random(BASE_DMG, BASE_DMG * 2), null);
            } else if (MathUtils.testPercentChance(BASE_CHANCE / 2)) {
                squad.batchFloatingStatusLines.addLine("Sand storm");
                squad.hurtBy(MathUtils.random(1, BASE_DMG * 2), null);
            }
        }

        if (cell.getLandscape().isStoneLand()) {
            if (MathUtils.testPercentChance(BASE_CHANCE)) {
                squad.batchFloatingStatusLines.addLine("Scorpion attack");
                squad.hurtBy(MathUtils.random(BASE_DMG, BASE_DMG * 2), null);
            } else if (MathUtils.testPercentChance(BASE_CHANCE / 2)) {
                squad.batchFloatingStatusLines.addLine("Leg injury");
                squad.hurtBy(MathUtils.random(1, BASE_DMG), null);
                squad.effectManager.getOrCreate(WeakeningEffect.class).resetTickCounter();
            }
        }

        squad.batchFloatingStatusLines.flush(Color.ORANGE);
    }

}
