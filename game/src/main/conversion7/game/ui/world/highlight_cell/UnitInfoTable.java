package conversion7.game.ui.world.highlight_cell;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.VBox;
import conversion7.engine.custom2d.table.DefaultTable;
import conversion7.game.Assets;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.WorldRelations;
import conversion7.game.stages.world.elements.SoulType;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.utils.UiUtils;

public class UnitInfoTable extends DefaultTable {

    private static final int PAD = ClientUi.SPACING;
    private static final int ICON_SIZE = ClientUi.SMALL_ICON_SIZE;
    private static final Label.LabelStyle POS_STYLE;
    private static final Label.LabelStyle NEUTRAL_STYLE;
    private static final Label.LabelStyle NEG_STYLE;
    VBox infoTable;

    static {
        POS_STYLE = new Label.LabelStyle(Assets.font14, Color.BLACK);
        POS_STYLE.background = new TextureRegionColoredDrawable(
                Color.GREEN, Assets.pixel);

        NEUTRAL_STYLE = new Label.LabelStyle(Assets.font14, Color.BLACK);
        NEUTRAL_STYLE.background = new TextureRegionColoredDrawable(
                Color.GRAY, Assets.pixel);

        NEG_STYLE = new Label.LabelStyle(Assets.font14, Color.BLACK);
        NEG_STYLE.background = new TextureRegionColoredDrawable(
                Assets.RED, Assets.pixel);
    }

    public UnitInfoTable() {
        infoTable = new VBox();
        add(infoTable);

        row();

    }

    public void load(AbstractSquad selectedObject, AbstractSquad mouseOverSquad) {
        infoTable.clear();

        if (mouseOverSquad == null
                || Gdxg.core.world.activeTeam == null) {
            return;
        }

        infoTable.add(new Label("Team: " + mouseOverSquad.getTeam().getName(), Assets.labelStyle14yellow));
        infoTable.add(new Label(mouseOverSquad.team.getBehaviourTagsHint()
                , Assets.labelStyle14blackWithBackground));


        Team lastActivePlayerTeam = Gdxg.core.world.lastActivePlayerTeam;
        if (lastActivePlayerTeam != null
                && lastActivePlayerTeam != mouseOverSquad.team ) {
            World world = mouseOverSquad.team.world;
            WorldRelations.RelationData relationData = world.worldRelations
                    .getRelationData(mouseOverSquad.team, lastActivePlayerTeam);
            int relationBalance = relationData.getBalance();
            String hint = mouseOverSquad.team.getRelationName(lastActivePlayerTeam) +
                    " relation (" + UiUtils.getNumberWithSign(relationBalance) + ")";

            String relationsHint = relationData.getRelationTypesHint();
            if (relationsHint.length() > 0) {
                hint += ":\n" + relationsHint;
            }

            infoTable.add(new Label(hint
                    , Assets.labelStyle12_i_whiteAndLittleGreen));
        }

        Image image = new Image(Assets.getUnitClassIcon(mouseOverSquad.unit.getGameClass()));
        infoTable.add(image).size(ICON_SIZE).left().pad(PAD);

        // element
        String additionalElementMsg = "";
        Label.LabelStyle elemStyle = NEUTRAL_STYLE;
        if (selectedObject != null && selectedObject.team != mouseOverSquad.team) {
            if (selectedObject.elementType.doesAttack(mouseOverSquad.elementType)) {
                additionalElementMsg = " - My Element attacks him!";
                elemStyle = POS_STYLE;
            } else if (mouseOverSquad.elementType.doesAttack(selectedObject.elementType)) {
                additionalElementMsg = " - His Element attacks me!";
                elemStyle = NEG_STYLE;
            }
        }
        infoTable.addLabel(mouseOverSquad.elementType.toString() + additionalElementMsg, elemStyle);

        // soul
        String additionalSoulMsg = "";
        Label.LabelStyle soulStyle = NEUTRAL_STYLE;
        if (selectedObject != null && selectedObject.team == mouseOverSquad.team) {
            SoulType.Effect soulEffect = selectedObject.soul.getType().getEffectOn(mouseOverSquad.soul.getType());
            if (soulEffect == SoulType.Effect.POSITIVE) {
                additionalSoulMsg = " - Positive Soul Combo";
                soulStyle = POS_STYLE;
            } else if (soulEffect == SoulType.Effect.NEGATIVE) {
                additionalSoulMsg = " - Negative Soul Combo";
                soulStyle = NEG_STYLE;
            }
        }
        infoTable.addLabel(mouseOverSquad.soul.getType().toString() + additionalSoulMsg, soulStyle);

        // Spirit
        String additionalSpiritMsg = "";
        Label.LabelStyle spiritStyle = NEUTRAL_STYLE;
        if (selectedObject != null && selectedObject.team == mouseOverSquad.team) {
            if (selectedObject.spiritType.doesSupport(mouseOverSquad.spiritType)) {
                additionalSpiritMsg = " - My Spirit supports him!";
                spiritStyle = POS_STYLE;
            } else if (mouseOverSquad.spiritType.doesSupport(selectedObject.spiritType)) {
                additionalSpiritMsg = " - His Spirit supports me!";
                spiritStyle = POS_STYLE;
            }
        }
        infoTable.addLabel(mouseOverSquad.spiritType.toString() + additionalSpiritMsg, spiritStyle);

        if (mouseOverSquad.myGod != null) {
            infoTable.addLabel("Believes in " + mouseOverSquad.myGod.getName(), Assets.labelStyle14blackWithBackground);
        }

        pack();
    }
}
