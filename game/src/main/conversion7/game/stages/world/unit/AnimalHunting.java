package conversion7.game.stages.world.unit;

import com.badlogic.gdx.graphics.Color;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.inventory.TeamInventory;
import conversion7.game.stages.world.inventory.items.FangItem;
import conversion7.game.stages.world.inventory.items.SkinItem;
import conversion7.game.stages.world.inventory.items.ToothItem;
import conversion7.game.stages.world.inventory.items.TuskItem;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.AnimalHerd;
import conversion7.game.unit_classes.ClassStandard;
import conversion7.game.unit_classes.UnitClassConstants;
import conversion7.game.unit_classes.animals.BaseAnimalClass;
import org.slf4j.Logger;

public class AnimalHunting {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static void hunted(AbstractSquad ownerSquad, Class<? extends BaseAnimalClass> targCls) {
        ownerSquad.team.tryLearnAnimal(ownerSquad.getLastCell(), targCls);
        ClassStandard animalStand = UnitClassConstants.CLASS_STANDARDS.get(targCls);
        huntResourcesIfCan(ownerSquad, animalStand, ownerSquad.cell);

    }

    public static void hunted(AbstractSquad ownerSquad, AbstractSquad targSquad) {
        ownerSquad.team.tryLearnAnimal(targSquad);
        huntResourcesIfCan(ownerSquad, targSquad.unit.classStandard, targSquad.getLastCell());
    }

    public static void huntResourcesIfCan(AbstractSquad ownerSquad, ClassStandard animalStandard, Cell onCell) {
        LOG.info("huntResourcesIfCan: " + animalStandard.unitClass.getSimpleName());
        if (ownerSquad.canHunt()) {
            TeamInventory teamInventory = ownerSquad.team.getInventory();

            int preyAmount = 0;
            teamInventory.startBatch();
            if (animalStandard.skin != null) {
                teamInventory.addItem(SkinItem.class, animalStandard.skin, ownerSquad.cell);
                preyAmount += animalStandard.skin;
            }
            if (animalStandard.tooth != null) {
                teamInventory.addItem(ToothItem.class, animalStandard.tooth, ownerSquad.cell);
                preyAmount += animalStandard.tooth;
            }
            if (animalStandard.fang != null) {
                teamInventory.addItem(FangItem.class, animalStandard.fang, ownerSquad.cell);
                preyAmount += animalStandard.fang;
            }
            if (animalStandard.tusk != null) {
                teamInventory.addItem(TuskItem.class, animalStandard.tusk, ownerSquad.cell);
                preyAmount += animalStandard.tusk;
            }
            teamInventory.endBatch();

            if (preyAmount > 0) {
                onCell.addFloatLabel("Prey amount " + preyAmount, Color.ORANGE);
            } else {
                onCell.addFloatLabel("No prey", Color.ORANGE);
            }

            ownerSquad.updateExperience(AnimalHerd.HUNT_EXP, "Hunt exp");
        }
    }
}
