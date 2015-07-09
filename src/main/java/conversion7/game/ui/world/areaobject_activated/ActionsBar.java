package conversion7.game.ui.world.areaobject_activated;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.AbstractStageObjectAction;
import conversion7.game.stages.battle.BattleThreadLocalSort;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.HintForm;
import conversion7.game.utils.collections.Comparators;
import org.slf4j.Logger;

public class ActionsBar extends AnimatedWindow {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final int BUTTON_SIZE = 40;
    private static final int PAD = ClientUi.SPACING;
    public static final int SCROLL_BODY_HEIGHT = BUTTON_SIZE + PAD * 2;
    public static final int SCROLL_BODY_HEIGHT_WITH_SCROLLBAR = SCROLL_BODY_HEIGHT + ClientUi.SCROLL_LINE_SIZE;
    private static final int BUTTONS_AMOUNT_IN_ROW = 5;

    private ScrollPane scroll;
    private Cell<ScrollPane> scrollPaneCell;
    private Table table = new Table();

    public ActionsBar(Stage stage) {
        super(stage, "Actions", Assets.uiSkin, Direction.down);

        // main table - elements holder
        scroll = new ScrollPane(table, Assets.uiSkin);
        scrollPaneCell = add(scroll).pad(PAD).maxWidth(BUTTONS_AMOUNT_IN_ROW * BUTTON_SIZE
                + BUTTONS_AMOUNT_IN_ROW * PAD * 2
                + PAD);
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(false, true);
        scroll.setClamp(false);

        addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
    }

    public void showFor(final AreaObject object) {
        table.clearChildren();
        ButtonWithActor button;
        int buttonsNumber = object.getActions().size;
        BattleThreadLocalSort.instance().sort(object.getActions(), Comparators.AREA_OBJECT_ACTIONS_COMPARATOR);
        for (final AbstractStageObjectAction action : object.getActions()) {
            AbstractAreaObjectAction areaObjectAction = (AbstractAreaObjectAction) action;
            button = new ButtonWithActor(new Image(areaObjectAction.getIconTexture()));
            table.add(button).size(BUTTON_SIZE).left().pad(PAD);
            HintForm.assignHintTo(button, areaObjectAction.getHint());
            button.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (event.getButton() == Input.Buttons.LEFT) {
                        action.execute();
                    }
                }
            });
        }

        if (buttonsNumber > BUTTONS_AMOUNT_IN_ROW) {
            scrollPaneCell.height(SCROLL_BODY_HEIGHT_WITH_SCROLLBAR);
        } else {
            scrollPaneCell.height(SCROLL_BODY_HEIGHT);
        }

        show();
    }

    @Override
    public void onShow() {
        pack();
        setPosition(PAD,
                Gdxg.clientUi.getAreaObjectDetailsBar().getFinalHeight() + PAD * 2);
        updateAnimationBounds();
    }
}
