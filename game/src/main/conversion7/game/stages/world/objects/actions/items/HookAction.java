package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.engine.geometry.Point2s;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractHumanSquadAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.TribeRelationType;

public class HookAction extends AbstractHumanSquadAction {


    public static final int BASE_DMG = 1;
    public static final String DESC = "Hook and place unit on adjacent cell";

    public HookAction() {
        super(Group.ATTACK);
    }

    public static void hook(WorldSquad hooker, Cell myCell, Cell hookFrom) {
        Point2s hookMoveDirection = myCell.getDiffWithCell(hookFrom).trim(1);
        Cell hookTo = myCell.getCell(hookMoveDirection.x, hookMoveDirection.y);
        AbstractSquad targetSquad = hookFrom.getSquad();
        targetSquad.team.world.addRelationType(TribeRelationType.ATTACK, targetSquad.team, hooker.team);
        if (hookFrom != hookTo && hookTo.canBeSeized()) {
            targetSquad.power.freeNextMove = true;
            targetSquad.moveOn(hookTo);
            myCell.addFloatLabel("Hook!", Color.ORANGE);
            hookFrom.addFloatLabel("Hook!", Color.ORANGE);
        } else {
            myCell.addFloatLabel("Hook was torn", Color.ORANGE);
            hookFrom.addFloatLabel("Hook was torn", Color.ORANGE);
        }
    }

    public static Boolean canHookFrom(AbstractSquad hooker, Cell fromCell) {
        return fromCell.hasSquad()
                && fromCell.getSquad() != hooker
                && !fromCell.getSquad().team.isAllyOf(hooker.team);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.getTextureReg("hook");
    }

    @Override
    public String getActionWorldHint() {
        return "hook";
    }

    @Override
    public int getDistance() {
        return getSquad().getViewRadius(false);
    }

    @Override
    public String getShortName() {
        return "Hook";
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
        WorldSquad squad = getSquad();
        AbstractSquad targSq = input.squad;
        targSq.hurtBy(BASE_DMG, squad);
        if (targSq.isAlive()) {
            HookAction.hook(squad, squad.cell, input);
        }
    }
}
