package conversion7.game.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.pools.system.PoolManager;
import conversion7.game.Assets;

public class HintForm extends Table {

    private static final long DELAY_SHOW_MS = 250;
    private static final long NOT_SCHEDULED = -1;
    private static HintForm thiz;
    private Stage stage;
    private Vector2 screenPos = new Vector2();
    private Table content;
    private long scheduledAt;


    public HintForm(Stage stage) {
        super();
        this.stage = stage;
        thiz = this;
        setBackground(new TextureRegionColoredDrawable(Assets.LIGHT_GREEN, Assets.pixelWhite));
        defaults().left().top();
        stage.addActor(this);
        cancel();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        keepWithinStage();
        super.draw(batch, parentAlpha);
    }

    void keepWithinStage() {
        Stage stage = getStage();
        if (getParent() == stage.getRoot()) {
            float parentWidth = stage.getWidth();
            float parentHeight = stage.getHeight();
            if (getX() < 0) setX(0);
            if (getY() < 0) setY(0);
            if (getRight() > parentWidth) setX(getX() - getWidth());
            if (getTop() > parentHeight) setY(getY() - getHeight());
        }
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
        hintTable.defaults().left().top().pad(ClientUi.SPACING);
        hintTable.add(new Label(text, Assets.labelStyle12_i_black)).fill().expand();
        hintTable.pack();
        return hintTable;
    }
}
