package conversion7.game.ui.hint;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.table.Panel;
import conversion7.engine.pools.system.PoolManager;
import conversion7.game.Assets;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.utils.UiUtils;

public class PopupHintPanel extends Panel {

    private static final long DELAY_SHOW_MS = 250;
    private static final long NOT_SCHEDULED = -1;
    private static PopupHintPanel thiz;
    private static final float HINT_WIDTH = 350;
    private Vector2 screenPos = new Vector2();
    private Table content;
    private long scheduledAt;


    public PopupHintPanel() {
        super();
        thiz = this;
        setBackground(new TextureRegionColoredDrawable(Assets.LIGHT_GREEN, Assets.pixel));
        defaults().left().top();
        cancel();
    }

    public static void assignHintTo(final Actor actor, final Table content) {
        actor.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                Vector2 screenPos = PoolManager.VECTOR_2_POOL.obtain();
                actor.localToStageCoordinates(screenPos);
                screenPos.add(x + 1, y);
                thiz.scheduleWithParameters(screenPos, content);
                return true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                thiz.cancel();
            }
        });
    }

    public static void assignHintTo(final Actor actor, final String hintText) {
        assignHintTo(actor, buildHintWithText(hintText));
    }

    public static Table buildHintWithText(String text) {
        Table hintTable = new Table();
        hintTable.setWidth(HINT_WIDTH);
        hintTable.defaults().left().top().pad(ClientUi.SPACING);
        Label label = new Label(text, Assets.labelStyle12_i_black);
        hintTable.add(label).fill().expand().width(HINT_WIDTH);
        label.setWidth(HINT_WIDTH);
        label.setWrap(true);
        hintTable.pack();
        return hintTable;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        UiUtils.keepWithinStage(this, true);
        super.draw(batch, parentAlpha);
    }

    public void show() {
        setVisible(true);
        add(content).fill().expand();
        setPosition(screenPos.x, screenPos.y);
        setSize(content.getWidth(), content.getHeight());
        toFront();
    }

    public void scheduleWithParameters(Vector2 screenPos, Table content) {
        this.screenPos.set(screenPos);
        this.content = content;
        PoolManager.VECTOR_2_POOL.free(screenPos);
        scheduledAt = System.currentTimeMillis();
    }

    public void cancel() {
        clearChildren();
        scheduledAt = NOT_SCHEDULED;
        setVisible(false);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (scheduledAt != NOT_SCHEDULED && !isVisible() && scheduledAt + DELAY_SHOW_MS < System.currentTimeMillis()) {
            show();
        }
    }
}
