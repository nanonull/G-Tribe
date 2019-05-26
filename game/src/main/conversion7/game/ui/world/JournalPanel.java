package conversion7.game.ui.world;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.HBox;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.VBox;
import conversion7.game.Assets;
import conversion7.game.interfaces.HintProvider;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.quest.BaseQuest;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.actions.SelectNextImportantObjectAction;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.hint.PopupHintPanel;

public class JournalPanel extends VBox {

    private Stage stageGUI;
    private TextButton lastPressedShowBtn;

    public JournalPanel(Stage stageGUI) {
        this.stageGUI = stageGUI;
        pad(ClientUi.SPACING);
        setBackground(new TextureRegionColoredDrawable(ClientUi.PANEL_COLOR, Assets.pixel));
    }
    Cell lastPressedShowBtnAtCell;

    public void showFor(Team team) {
        lastPressedShowBtn = null;
        clearChildren();
        addSmallCloseButton();

        for (ObjectMap.Entry<Class, BaseQuest> questEntry : team.journal.quests) {
            Class questClass = questEntry.key;
            BaseQuest quest = questEntry.value;
            addLabel(quest.getName(), Assets.labelStyle18yellow);
            addSpaceLine();

            for (ObjectMap.Entry<Object, BaseQuest.State> stateEntry : quest.states) {
                Object enumId = stateEntry.key;
                BaseQuest.State state = stateEntry.value;
                String msg = quest.stateMessages.get(enumId);
                String line = "  " + state + ": " + msg + quest.getAddInfo();
                HBox hBox = new HBox();
                add(hBox);
                hBox.addLabel(line, Assets.labelStyle14white2);

                Cell cellTarget = quest.stateCellTargets.get(enumId);
                if (cellTarget != null) {
                    TextButton go = new TextButton("Show", Assets.uiSkin);
                    hBox.add(go).padLeft(ClientUi.SPACING);
                    PopupHintPanel.assignHintTo(go, "Click to locate target. \nClick once more to return back.");
                    go.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if (lastPressedShowBtn == go) {
                                SelectNextImportantObjectAction.focusOn(lastPressedShowBtnAtCell);
                                lastPressedShowBtn= null;
                                lastPressedShowBtnAtCell = null;
                            } else {
                                lastPressedShowBtn = go;
                                lastPressedShowBtnAtCell = Gdxg.graphic.getCameraController().getPositionInCells();
                                SelectNextImportantObjectAction.focusOn(cellTarget);
                            }
                        }
                    });
                }
            }

            addSpaceLine();
        }

        stageGUI.addActor(this);
        pack();
        setY(stageGUI.getHeight() - getHeight());
    }
}
