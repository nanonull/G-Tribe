package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class MeleeSwingAction extends AbstractSquadAction {

    public static final String DESC = "Attack all enemies around with melee weapon";

    public MeleeSwingAction() {
        super(Group.ATTACK);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.getTextureReg("swing");
    }

    @Override
    public String getShortName() {
        return "Swing";
    }

    @Override
    public String buildDescription() {
        return getName()
                + "\n \n" + DESC;
    }

    @Override
    public void begin() {
        AbstractSquad squad = getSquad();
        for (Cell adjCell : new Array.ArrayIterable<>(squad.getLastCell().getCellsAround())) {
            if (adjCell.hasSquad() && squad.isEnemyWith(adjCell.getSquad().unit)) {
                squad.power.freeNextAttack = true;
                adjCell.getSquad().batchFloatingStatusLines.addLine("Swing");
                squad.initAttack(adjCell).setMeleeAttack(true).start();
            }
        }
        squad.power.madeAttackOnStep = true;
    }
}
