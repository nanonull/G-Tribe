package conversion7.game.ui.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.game.Assets;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.events.AbstractEventNotification;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.HintForm;
import junit.framework.Assert;

public class EventsBar extends AnimatedWindow {

    public static final int BUTTON_SIZE = 40;
    private static final int SCROLLBAR_SIZE = 20;
    private static final int PAD = ClientUi.SPACING;
    private static final int BUTTONS_AMOUNT_ON_SCREEN = 4;
    private static final float POS_Y = 400;

    Stage stage;
    ScrollPane scroll;
    private Table table = new Table();
    private Team team;

    public EventsBar(Stage stage) {
        super(stage, "Events", Assets.uiSkin, Direction.right);
        this.stage = stage;

        // main table - elements holder
        scroll = new ScrollPane(table, Assets.uiSkin);
        add(scroll).pad(PAD).maxHeight(BUTTONS_AMOUNT_ON_SCREEN * BUTTON_SIZE
                + BUTTONS_AMOUNT_ON_SCREEN * PAD * 2
                + PAD);
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);

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

    public void showFor(final Team team) {
        if (team.getEvents().size == 0) {
            return;
        }

        this.team = team;
        table.clearChildren();
        table.defaults().left().top();
        ButtonWithActor button;
        boolean withScrollBar = team.getEvents().size > BUTTONS_AMOUNT_ON_SCREEN;
        for (final AbstractEventNotification eventNotification : team.getEvents()) {
            table.row();
            button = eventNotification.getIcon();
            final Cell<ButtonWithActor> actorCell = table.add(button).width(BUTTON_SIZE).height(BUTTON_SIZE).pad(PAD);
            if (withScrollBar) {
                table.add().width(SCROLLBAR_SIZE + PAD);
            }

            HintForm.assignHintTo(button, eventNotification.getHint());

            button.addListener(new InputListener() {

                AbstractEventNotification thisEventNotification = eventNotification;

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (!Gdx.input.isTouched()) {
                        switch (button) {
                            case Input.Buttons.LEFT:
                                thisEventNotification.action();
                                break;

                            case Input.Buttons.RIGHT:
                                removeNotificationEvent(thisEventNotification, actorCell);
                                break;
                        }
                    }
                }
            });
        }

        show();
    }

    @Override
    public void onShow() {
        setPosition(PAD, POS_Y);
        pack();
        updateAnimationBounds();
    }

    private void removeNotificationEvent(AbstractEventNotification eventNotification, Cell<ButtonWithActor> actorCell) {
        Array<AbstractEventNotification> events = team.getEvents();
        Assert.assertTrue(team.getEvents().removeValue(eventNotification, true));
        if (events.size == 0) {
            hide();
        } else {
            actorCell.getActor().remove();
            actorCell.height(0).pad(0);
            pack();
            updateAnimationBounds();
        }
    }
}
