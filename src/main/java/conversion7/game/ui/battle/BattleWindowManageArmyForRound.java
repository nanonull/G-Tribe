package conversion7.game.ui.battle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.CustomWindow;
import conversion7.engine.geometry.Drawer2d;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.battle.BattleFigure;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.ClientUi;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;

public class BattleWindowManageArmyForRound extends CustomWindow {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int HEADER_HEIGHT = 20;
    private static final int ROW_HEIGHT = 40;
    private static final int ICON_SIZE = ROW_HEIGHT;
    private static final int PAD = ClientUi.SPACING;

    private static final Color HIGHLIGHT_BORDER_COLOR = new Color(1, 0.85f, 0, 1);
    private static final Color SELECT_BORDER_COLOR = Color.GREEN;
    private static final int HIGHLIGHT_BORDER_THICKNESS = 3;
    private static final int HIGHLIGHT_ELEMENT_SIZE = ICON_SIZE + HIGHLIGHT_BORDER_THICKNESS * 2;

    ScrollPane scroll;
    private Table table = new Table();

    public BattleWindowManageArmyForRound(Stage stageGUI) {
        super(stageGUI, "ManageArmyForRoundWindow", Assets.uiSkin);

        // super Window params
        setPosition(0, GdxgConstants.SCREEN_HEIGHT_IN_PX);
        setWidth(280);
        setHeight(600);

        // main table - elements holder
        scroll = new ScrollPane(table, Assets.uiSkin);
        add(scroll).pad(PAD, PAD, PAD, PAD);
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);

    }

    public void refresh() {
        table.clearChildren();
        table.left().top();
        createHeader();
        createRows();
    }

    private void createHeader() {
        table.row().height(HEADER_HEIGHT).left().top().space(PAD, PAD, PAD, PAD);
        table.add(); // checkbox
        table.add(); // icon

        TextButton button;
        button = new TextButton("DMG", Assets.uiSkin);
        table.add(button).center();
        button = new TextButton(" HP ", Assets.uiSkin);
        table.add(button).center();
        button = new TextButton("DEF", Assets.uiSkin);
        table.add(button).center();
        button = new TextButton("ARM", Assets.uiSkin);
        table.add(button).center();

        table.add().width(ClientUi.SCROLL_LINE_SIZE); // scroll bar pad
    }

    Map<BattleFigure, Cell> mapFigureOnIconCell = new HashMap<>();

    private void createRows() {
        mapFigureOnIconCell.clear();
        Array<Unit> playerArmyUnits = ClientCore.core.battle.humanPlayerArmyLink.getUnits();
        for (int i = 0; i < playerArmyUnits.size; i++) {
            Unit unit = playerArmyUnits.get(i);
            final BattleFigure battleFigure = ClientCore.core.battle.getFigure(unit);
            if (battleFigure.isKilled()) {
                continue;
            }

            table.row().height(ROW_HEIGHT).left().top().space(PAD, PAD, PAD, PAD);

            // checkbox
            final CheckBox checkBox = new CheckBox("", Assets.uiSkin);
            table.add(checkBox).center();
            if (battleFigure.params.step.hasFigureAssigned(battleFigure)) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }

            checkBox.addListener(new InputListener() {

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    LOG.info("touchDown in checkbox = " + checkBox.hashCode());
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    LOG.info("touchUp on checkbox with assignedBattleFigure = " + battleFigure);
                    if (!checkBox.isChecked()) {
                        if (battleFigure.activated) {
                            LOG.info("un-check figure");
                            battleFigure.deactivate();
                        }
                    } else {
                        if (!battleFigure.activated) {
                            LOG.info("set figure checked");
                            battleFigure.activate();
                        }
                    }
                }
            });

            // icon
            Image image = new Image(unit.getClassIcon());
            Cell iconCell = table.add(image).fill().left().width(ICON_SIZE);
            image.setScaling(Scaling.fit);
            mapFigureOnIconCell.put(battleFigure, iconCell);

            // params
            table.add(new Label(valueOf(unit.getMeleeDamage()), Assets.labelStyle18yellow)).center();
            table.add(new Label(valueOf(unit.getParams().getHealth()), Assets.labelStyle18yellow)).center();
            table.add(new Label(valueOf(unit.getDefence()), Assets.labelStyle18yellow)).center();
            table.add(new Label(valueOf(unit.getArmor()), Assets.labelStyle18yellow)).center();
        }
    }

    Cell highlightedCell;
    Cell selectedCell;

    public void highlightFigure(BattleFigure battleFigure) {
        highlightedCell = mapFigureOnIconCell.get(battleFigure);
    }

    public void resetHighlightFigure() {
        highlightedCell = null;
    }

    public void selectFigure(BattleFigure battleFigure) {
        selectedCell = mapFigureOnIconCell.get(battleFigure);
    }

    public void resetSelectFigure() {
        selectedCell = null;
    }

    Vector2 highlightVectorWip = new Vector2();
    Vector2 selectVectorWip = new Vector2();

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (highlightedCell != null) {
            Actor image = highlightedCell.getActor();
            highlightVectorWip.set(0, 0);
            image.localToStageCoordinates(highlightVectorWip);
            Gdxg.spriteBatch.setColor(HIGHLIGHT_BORDER_COLOR);
            Drawer2d.drawRect(
                    (int) highlightVectorWip.x - HIGHLIGHT_BORDER_THICKNESS,
                    (int) highlightVectorWip.y - HIGHLIGHT_BORDER_THICKNESS,
                    HIGHLIGHT_ELEMENT_SIZE, HIGHLIGHT_ELEMENT_SIZE, HIGHLIGHT_BORDER_THICKNESS);
        }

        if (selectedCell != null) {
            Actor image = selectedCell.getActor();
            selectVectorWip.set(0, 0);
            image.localToStageCoordinates(selectVectorWip);
            Gdxg.spriteBatch.setColor(SELECT_BORDER_COLOR);
            Drawer2d.drawRect(
                    (int) selectVectorWip.x - HIGHLIGHT_BORDER_THICKNESS,
                    (int) selectVectorWip.y - HIGHLIGHT_BORDER_THICKNESS,
                    HIGHLIGHT_ELEMENT_SIZE, HIGHLIGHT_ELEMENT_SIZE, HIGHLIGHT_BORDER_THICKNESS);
        }
    }

}
